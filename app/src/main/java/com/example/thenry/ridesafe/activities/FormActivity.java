package com.example.thenry.ridesafe.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import com.example.thenry.ridesafe.R;
import com.example.thenry.ridesafe.models.Zone;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class FormActivity extends AppCompatActivity {

    @BindView(R.id.form_title)EditText form_title;
    @BindView(R.id.form_desc)EditText form_desc;
    @BindView(R.id.form_address)TextView form_address;
    @BindView(R.id.lat)TextView form_lat;
    @BindView(R.id.longi)TextView form_longi;

    private Realm realm;
    private double latitude;
    private double longitude;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        getSupportActionBar().show();
        ButterKnife.bind(this);

        RealmConfiguration config = new RealmConfiguration  // a modifier une fois que l'appli part en prod, il faudra fournir une migration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(config);

        Bundle extras = getIntent().getExtras();
        latitude = extras.getDouble("latitude");
        longitude = extras.getDouble("longitude");
        address = extras.getString("address");

        // on concatène les strings en dehors des fonctions
        String str_address =getString(R.string.address)+ address;
        String str_longitude =getString(R.string.longitude)+ latitude;
        String str_latitude =getString(R.string.latitude)+ longitude;

        form_address.setText(str_address);
        form_lat.setText(str_latitude);
        form_longi.setText(str_longitude);

    }

    @OnClick(R.id.form_submit)
    public void saveZone()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String date = sdf.format(new Date());

        final Zone newZone = new Zone();
        newZone.setId(getNextPrimaryKey());
        newZone.setTitle(form_title.getText().toString());
        newZone.setDescription(form_desc.getText().toString());
        newZone.setAddress(address);
        newZone.setLatitude(latitude);
        newZone.setLongitude(longitude);
        newZone.setDate(date);
        newZone.setCount_delete(0);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // This will create a new object in Realm or throw an exception if the
                // object already exists (same primary key)
                // realm.copyToRealm(obj);

                // This will update an existing object with the same primary key
                // or create a new object if an object with no primary key = 42
                realm.copyToRealmOrUpdate(newZone);
            }
        });

        finish();
    }

    public int getNextPrimaryKey()
    {
        int key;
        try {
            key = realm.where(Zone.class).max("id").intValue() + 1;
        } catch(ArrayIndexOutOfBoundsException ex) {
            key = 0;
        } catch (NullPointerException e){
            key = 0;
        }
        return key;
    }

}
