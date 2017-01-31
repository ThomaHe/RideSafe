package com.example.thenry.ridesafe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FormActivity extends AppCompatActivity {

    @BindView(R.id.lat)TextView txt_lat;
    @BindView(R.id.longi)TextView text_longi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        getSupportActionBar().show();
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        Double latitude = extras.getDouble("latitude");
        Double longitude = extras.getDouble("longitude");

        txt_lat.setText("Latitude : " + latitude);
        text_longi.setText("Longitude : " + longitude);

    }


}
