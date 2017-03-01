package com.example.thenry.ridesafe.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.thenry.ridesafe.R;
import com.example.thenry.ridesafe.models.Zone;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class FormActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    @BindView(R.id.form_title)EditText form_title;
    @BindView(R.id.form_desc)EditText form_desc;
    @BindView(R.id.form_address)TextView form_address;
    @BindView(R.id.lat)TextView form_lat;
    @BindView(R.id.longi)TextView form_longi;
    @BindView(R.id.type_spinner)Spinner spinner;

    private Realm realm;
    private double latitude;
    private double longitude;
    private String address;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        getSupportActionBar().show();
        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

        Bundle extras = getIntent().getExtras();
        latitude = extras.getDouble("latitude");
        longitude = extras.getDouble("longitude");
        address = extras.getString("address");

        // we concatenate the strings outside of the UI functions
        String str_address =getString(R.string.address)+ address;
        String str_longitude =getString(R.string.longitude)+latitude;
        String str_latitude =getString(R.string.latitude)+ longitude;

        form_address.setText(str_address);
        form_lat.setText(str_latitude);
        form_longi.setText(str_longitude);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.type_array,R.layout.support_simple_spinner_dropdown_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

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
        newZone.setType(type);
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        type=i;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
