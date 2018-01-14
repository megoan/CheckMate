package com.example.user.check_mate.model.entities;

/**
 * Created by User on 03/12/2017.
 */

public class MyLocation {
    private double latitude;
    private double longitude;
    private String country;
    private String city;

    public MyLocation() {
    }

    public MyLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public MyLocation(double latitude, double longitude, String country, String city) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
    }

    public MyLocation(MyLocation other) {
        this.latitude = other.latitude;
        this.longitude = other.longitude;
        this.country = other.country;
        this.city = other.city;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
