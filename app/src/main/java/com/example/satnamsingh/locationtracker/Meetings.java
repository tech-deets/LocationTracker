package com.example.satnamsingh.locationtracker;

import java.util.ArrayList;

public class Meetings {
    private String meetingId;
    private String groupCode;
    private ArrayList<String> groupMembers;
    private String meetingLocation; //location name
    private double latitude;
    private  double longitude;
    private  String meetingAgenda;
    private String  meetingDate;
    private String  meetingTime;
    private String  meetingHost;

    public Meetings() {
    }
    public Meetings(Meetings meeting){
        this.meetingId=meeting.meetingId;
        this.groupCode = meeting.groupCode;
        this.groupMembers = meeting.groupMembers;
        this.meetingLocation = meeting.meetingLocation;
        this.latitude = meeting.latitude;
        this.longitude = meeting.longitude;
        this.meetingAgenda = meeting.meetingAgenda;
        this.meetingDate = meeting.meetingDate;
        this.meetingTime = meeting.meetingTime;
        this.meetingHost = meeting.meetingHost;
    }
    public Meetings(String meetingId,String groupCode, ArrayList<String> groupMembers, String meetingLocation, double latitude, double longitude, String meetingAgenda, String meetingDate, String meetingTime, String meetingHost) {
        this.meetingId=meetingId;
        this.groupCode = groupCode;
        this.groupMembers = groupMembers;
        this.meetingLocation = meetingLocation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.meetingAgenda = meetingAgenda;
        this.meetingDate = meetingDate;
        this.meetingTime = meetingTime;
        this.meetingHost = meetingHost;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public ArrayList<String> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(ArrayList<String> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public String getMeetingLocation() {
        return meetingLocation;
    }

    public void setMeetingLocation(String meetingLocation) {
        this.meetingLocation = meetingLocation;
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

    public String getMeetingAgenda() {
        return meetingAgenda;
    }

    public void setMeetingAgenda(String meetingAgenda) {
        this.meetingAgenda = meetingAgenda;
    }

    public String getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(String meetingDate) {
        this.meetingDate = meetingDate;
    }

    public String getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(String meetingTime) {
        this.meetingTime = meetingTime;
    }

    public String getMeetingHost() {
        return meetingHost;
    }

    public void setMeetingHost(String meetingHost) {
        this.meetingHost = meetingHost;
    }
}
