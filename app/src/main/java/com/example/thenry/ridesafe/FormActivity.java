package com.example.thenry.ridesafe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class FormActivity extends AppCompatActivity {

    @BindView(R.id.lat)TextView txt_lat;
    @BindView(R.id.longi)TextView text_longi;
    @BindView(R.id.form_title)EditText form_title;
    @BindView(R.id.form_desc)EditText form_desc;

    private Realm realm;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        getSupportActionBar().show();
        ButterKnife.bind(this);
        realm=Realm.getDefaultInstance();

        Bundle extras = getIntent().getExtras();
        latitude = extras.getDouble("latitude");
        longitude = extras.getDouble("longitude");

        txt_lat.setText("Latitude : " + latitude);
        text_longi.setText("Longitude : " + longitude);

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
