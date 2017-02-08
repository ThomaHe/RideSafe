package com.example.thenry.ridesafe;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private Realm realm;
    public Marker selectedMarker;

    private View bottomSheet;
    private BottomSheetBehavior behavior;
    @BindView(R.id.title_sheet)
    TextView title_sheet;
    @BindView(R.id.desc_sheet)
    TextView desc_sheet;
    @BindView(R.id.add_fab)
    FloatingActionButton add_fab;


    // variables pour la mise à jour de la localisation
    private LocationRequest mLocationRequest;
    private boolean mLocationUpdateState;
    private static final int REQUEST_CHECK_SETTINGS = 2;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();

        getSupportActionBar().show();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        add_fab.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // on cache la bottom sheet quand on revient sur l'activité
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.getUiSettings().setZoomControlsEnabled(true); le + et - pour zoomer
        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLongClickListener(this);

        generateMap();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_propos) {
            Intent newIntent = new Intent(this, ProposActivity.class);
            startActivity(newIntent);
        }
        if (id == R.id.action_updateMap) {
            mMap.clear();
            generateMap();
        }
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {  // à la connection du play services
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //demande permission localisation
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);

        } else { // localisation déja autorisée
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LatLng currentloc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(currentloc).title("Ici").snippet("Danger !! Zone de grosse merde"));
            mMap.addMarker(new MarkerOptions().position(new LatLng(45.775801, 4.857337)).title("Parc").snippet("Roseraie, ici ça sent bon"));
            mMap.addMarker(new MarkerOptions().position(new LatLng(45.773860, 4.859770)).title("Botanic").snippet("Viens acheter un lapin"));
            mMap.addMarker(new MarkerOptions().position(new LatLng(45.783825, 4.869003)).title("CPE").snippet("C'était quand même bien"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentloc, 15));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                LatLng currentloc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(currentloc).title("Ici"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentloc, 12));
            } else {
                Toast.makeText(getApplicationContext(), "Sans localisation l'application ne peut pas fonctionner", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        title_sheet.setText(marker.getTitle());
        if (marker.getTitle().equals("Nouveau")) {  // nouveau marqueur ajouté par l'utilisateur
            add_fab.setVisibility(View.VISIBLE);
            selectedMarker = marker;                // pour envoyer avec le bouton +
            desc_sheet.setText("Appuyez sur le + pour ajouter une nouvelle zone de danger ");
        } else {                                    // marqueur de la BDD
            add_fab.setVisibility(View.GONE);
            if (marker.getSnippet() != null) {
                desc_sheet.setText(marker.getSnippet());
            }
        }

        return true;
    }

    @OnClick(R.id.add_fab)  // appui sur le bouton +
    public void submit() {
        Intent intent = new Intent(MapsActivity.this, FormActivity.class);
        Bundle extras = new Bundle();
        extras.putDouble("latitude", selectedMarker.getPosition().latitude);
        extras.putDouble("longitude", selectedMarker.getPosition().longitude);
        intent.putExtras(extras);
        startActivity(intent);

    }


    @Override
    public void onMapLongClick(LatLng latLng) {    // ajout d'un nouveau marqueur sur la carte
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Nouveau")
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        LatLng currentloc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currentloc).title("Ici"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentloc, 15));

    }

    public void generateMap() {
        RealmResults<Zone> allZones = realm.where(Zone.class).findAll();
        try {
            for (Zone zone : allZones) {
                LatLng latLng = new LatLng(zone.getLatitude(), zone.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title(String.valueOf(zone.getId())));
            }
        }catch (NullPointerException e){
            Log.e("NULL", "Pas de zones à afficher");
        }

    }


}
