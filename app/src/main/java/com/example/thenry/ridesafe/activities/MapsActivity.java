package com.example.thenry.ridesafe.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thenry.ridesafe.MapsController;
import com.example.thenry.ridesafe.R;
import com.example.thenry.ridesafe.models.Zone;
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

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    public Context ctx;
    public MapsController mapsController;

    private Realm realm;
    public Marker selectedMarker;
    public Zone selectedZone;

    private View bottomSheet;
    private BottomSheetBehavior behavior;
    @BindView(R.id.add_fab)
    FloatingActionButton add_fab;
    @BindView(R.id.title_sheet)
    TextView sheet_title;
    @BindView(R.id.desc_sheet)
    TextView sheet_desc;
    @BindView(R.id.address_sheet)
    TextView sheet_address;
    @BindView(R.id.btn_signal)
    Button btn_signal;

    @BindView(R.id.address_search)
    EditText address_search;


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

        RealmConfiguration config = new RealmConfiguration  // a modifier une fois que l'appli part en prod, il faudra fournir une migration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(config);
        ctx = MapsActivity.this;
        mapsController = new MapsController(realm);

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
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        sheet_address.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        sheet_address.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        sheet_address.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        address_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    String location = address_search.getText().toString();
                    List<Address> addressList = null;

                    if (location != null || !location.equals("")) {
                        Geocoder geocoder = new Geocoder(MapsActivity.this);
                        try {
                            addressList = geocoder.getFromLocationName(location, 1);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(addressList.size()!=0) {
                            Address address = addressList.get(0);
                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                            mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title("Nouveau")
                                    .draggable(true)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Adresse inconnue", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                    return true;
                }
                return false;
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
        if (id == R.id.action_deleteRealm)  // à virer avant la prod
        {

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.deleteAll();
                }
            });

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
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentloc, 15));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { // executée à la réponse positive de l'autorisation de localisation
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

    // sert à afficher la bottom sheet et la remplir
    @Override
    public boolean onMarkerClick(Marker marker) {
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        if (marker.getTitle().equals("Nouveau"))  // nouveau marqueur ajouté par l'utilisateur
        {
            add_fab.setVisibility(View.VISIBLE);
            btn_signal.setVisibility(View.GONE);

            selectedMarker = marker;                // pour envoyer avec le bouton +
            sheet_title.setText(getString(R.string.newZone));
            sheet_desc.setText(getString(R.string.addZone));
            sheet_address.setText(marker.getSnippet());
        }
        else {                                    // Marker en bdd
            add_fab.setVisibility(View.GONE);
            btn_signal.setVisibility(View.VISIBLE);
            selectedZone = mapsController.getZone(Integer.parseInt(marker.getTitle()));

            sheet_title.setText(selectedZone.getTitle());
            sheet_desc.setText(selectedZone.getDescription());
            sheet_address.setText(selectedZone.getAddress());

        }

        return true;
    }

    @OnClick(R.id.add_fab)  // appui sur le bouton +
    public void submit() {
        Intent intent = new Intent(MapsActivity.this, FormActivity.class);
        Bundle extras = new Bundle();
        extras.putDouble("latitude", selectedMarker.getPosition().latitude);
        extras.putDouble("longitude", selectedMarker.getPosition().longitude);
        extras.putString("address", selectedMarker.getSnippet());
        intent.putExtras(extras);
        startActivity(intent);

    }

    @OnClick(R.id.btn_signal)
    public void signal(){  //TODO : vérifier que l'utilisateur n'ai pas déja signalé la zone
        final int count = (selectedZone.getCount_delete())+1;
        if(count<3){  // on supprime le marqueur après 3 signalement
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    selectedZone.setCount_delete(count);  // incrémente le compteur
                    realm.copyToRealmOrUpdate(selectedZone);
                }
            });
        }else{ // déja signalé 3 fois
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    selectedZone.deleteFromRealm();  // on l'efface
                }
            });
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN); // on cache la Bottom Sheet vidée
        }
        Toast.makeText(getApplicationContext(), getString(R.string.thx_signal), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onMapLongClick(LatLng latLng) {    // ajout d'un nouveau marqueur sur la carte
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Nouveau")
                .snippet(mapsController.getAddress(latLng.latitude,latLng.longitude, ctx))
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
            Log.e("NULL", getString(R.string.noZones));
        }

    }




}
