package com.example.thenry.ridesafe.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
  Created by thenry on 27/01/2017.
 */

public class Zone extends RealmObject {

    @PrimaryKey
    private int id;
    private String title;
    private String description;
    private double latitude;
    private double longitude;
    private String address;
    private String date;
    private int count_delete;
    private int type;  // 0: risk, 1: shop, 2: mechanic, 3: bar/restaurant

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCount_delete() {
        return count_delete;
    }

    public void setCount_delete(int count_delete) {
        this.count_delete = count_delete;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
