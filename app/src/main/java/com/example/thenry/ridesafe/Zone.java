package com.example.thenry.ridesafe;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by thenry on 27/01/2017.
 */

public class Zone extends RealmObject {

    @PrimaryKey
    public int id;
    public String title;
    public String description;
    public double latitude;
    public double longitude;
    public String address;
    public String date;
    public int count_delete;

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
}
