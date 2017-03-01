package com.example.thenry.ridesafe;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.example.thenry.ridesafe.models.Zone;

import java.io.IOException;
import java.util.List;

import io.realm.Realm;

/**
  Created by thenry on 24/01/2017.
 */

public class MapsController {

    private Realm realm;

    public MapsController(Realm realm) {
        this.realm = realm;
    }

    public Zone getZone(int id) {
        Zone zone = realm.where(Zone.class)
                .equalTo("id", id)
                .findFirst();
        return zone;
    }

    public String getAddress (double latitude, double longitude, Context ctx) // récupère une string adresse
    {
        String address = "Adresse inconnue";
        Geocoder geocoder = new Geocoder(ctx);
        try {
            List<Address> listAddress = geocoder.getFromLocation(latitude,longitude, 1);
            if(listAddress.size()!=0) {
                address = listAddress.get(0).getAddressLine(0)+" "+listAddress.get(0).getAddressLine(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

}
