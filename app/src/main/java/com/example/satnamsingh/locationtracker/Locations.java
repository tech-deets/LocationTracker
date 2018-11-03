package com.example.satnamsingh.locationtracker;

public class Locations {
    private double latitude;
    private double longitude;
    String date;
    long time;
    Locations(){

    }
    Locations(double latitude, double longitude, String date, long time){
        this.latitude =latitude;
        this.longitude=longitude;
        this.date=date;
        this.time=time;
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

    public void setLongitude(double longotude) {
        this.longitude = longotude;
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
