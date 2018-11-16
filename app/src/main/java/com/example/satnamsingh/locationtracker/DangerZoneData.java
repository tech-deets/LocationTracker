package com.example.satnamsingh.locationtracker;

public class DangerZoneData {
    private String zoneId;
    private String markedBy;
    private double latitude;
    private double longitude;
    private String name;
    private String comment;

    public DangerZoneData() {
    }
public DangerZoneData(DangerZoneData zone){
    this.zoneId    =    zone.zoneId;
    this.markedBy  =    zone.markedBy;
    this.latitude  =    zone.latitude;
    this.longitude =    zone.longitude;
    this.name      =    zone.name;
    this.comment   =    zone.comment;
}
    public DangerZoneData(String zoneId, String markedBy, double latitude, double longitude, String name, String comment) {
        this.zoneId = zoneId;
        this.markedBy = markedBy;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.comment = comment;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public String getMarkedBy() {
        return markedBy;
    }

    public void setMarkedBy(String markedBy) {
        this.markedBy = markedBy;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
