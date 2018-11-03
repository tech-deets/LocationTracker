package com.example.satnamsingh.locationtracker;

public class Locations {
     private double latitude;
     private double longitude;
     private String date;
     private long time;
    Locations(){

    }

    public Locations(double latitude, double longitude, String date, long time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.time = time;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
