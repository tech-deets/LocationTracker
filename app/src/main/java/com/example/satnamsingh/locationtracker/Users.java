package com.example.satnamsingh.locationtracker;

import java.util.ArrayList;

public class Users {
     private String name;
    private String email;
    private String phoneNumber;
    private String photo;
    private ArrayList<String> Invitations;
    private ArrayList<String> GroupName;
    private ArrayList<String> GroupCode;
    private ArrayList<Locations> userLocations;
    private ArrayList<Meetings> meetingsList;

    public ArrayList<Meetings> getMeetingsList() {
        return meetingsList;
    }

    public Users(String name, String email, String phoneNumber, String photo, ArrayList<String> invitations, ArrayList<String> groupName, ArrayList<String> groupCode, ArrayList<Locations> userLocations, ArrayList<Meetings> meetingsList) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.photo = photo;
        Invitations = invitations;
        GroupName = groupName;
        GroupCode = groupCode;
        this.userLocations = userLocations;
        this.meetingsList = meetingsList;
    }

    public void setMeetingsList(ArrayList<Meetings> meetingsList) {
        this.meetingsList = meetingsList;
    }
    // private Locations LastLocation;
    //private LastLocation lastLocation;

   // public Users(LastLocation lastLocation) {
  //      this.lastLocation = lastLocation;
    //}

//    public Users(String name, String email, String phoneNumber, String photo, ArrayList<String> invitations, ArrayList<String> groupName, ArrayList<String> groupCode, ArrayList<Locations> userLocations, LastLocation lastLocation) {
//        this.name = name;
//        this.email = email;
//        this.phoneNumber = phoneNumber;
//        this.photo = photo;
//        Invitations = invitations;
//        GroupName = groupName;
//        GroupCode = groupCode;
//        this.userLocations = userLocations;
//        this.lastLocation = lastLocation;
//    }
//
//    public LastLocation getLastLocation() {
//
//        return lastLocation;
//    }

//    public void setLastLocation(LastLocation lastLocation) {
//        this.lastLocation = lastLocation;
//    }

    public void setUserLocations(ArrayList<Locations> userLocations) {
        this.userLocations = userLocations;
    }

//    public Locations getLastLocation() {
//        return LastLocation;
//    }
//
//    public void setLastLocation(Locations LastLocation) {
//        this.LastLocation = LastLocation;
//    }

    public ArrayList<Locations> getUserLocations() {
        return userLocations;
    }

    Users(){

    }


    public ArrayList<String> getInvitations() {
        return Invitations;
    }

    public void setInvitations(ArrayList<String> invitations) {
        Invitations = invitations;
    }

    public ArrayList<String> getGroupName() {
        return GroupName;
    }

    public void setGroupName(ArrayList<String> groupName) {
        GroupName = groupName;
    }

    public ArrayList<String> getGroupCode() {
        return GroupCode;
    }

    public void setGroupCode(ArrayList<String> groupCode) {
        GroupCode = groupCode;
    }

    public boolean equals(Object obj) {

        Users users = (Users) obj;
        String p1 = this.phoneNumber.replace("+91", "");
        String p2 = users.phoneNumber.replace("+91", "");

        p1 = p1.replace(" ", "");
        p2 = p2.replace(" ", "");

        if(p1.equals(p2)){
            this.setName(users.getName());
            return true;
        }
        else {
            return false;
        }

    }
    public  Users(String name,String photo){
        this.name =name;
        this.photo=photo;

    }
//    public  Users(String name,String photo,Locations lastLocation){
//        this.name =name;
//        this.photo=photo;
////        this.LastLocation=lastLocation;
//    }
    public Users(String name, String phoneNumber, String email, String photo) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        if(this.phoneNumber.startsWith("+91"))
        {
            this.phoneNumber =this.phoneNumber.replace("+91","");
        }
        this.photo = photo;
    }

    public Users(String phoneNumber, String name, String email) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        //if(this.phoneNumber.startsWith("+91"))
        //{
            //this.phoneNumber = this.phoneNumber.replace("+91","");
        //}
        this.photo ="https://firebasestorage.googleapis.com/v0/b/locationtracker-28250.appspot.com/o/default_pic.jpg?alt=media&token=7ed5b932-09bf-422e-aa5e-77eca30f378f";
    }
public Users(Users u){
        this.name=u.getName();
        this.photo=u.getPhoto();
        this.phoneNumber=u.getPhoneNumber();
}
    public Users(String name, String email, String phoneNumber, String photo,
                 ArrayList<String> invitations, ArrayList<String> groupName, ArrayList<String> groupCode) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.photo = photo;
        if(this.phoneNumber.startsWith("+91"))
        {
            this.phoneNumber = this.phoneNumber.replace("+91","");
        }
        Invitations = invitations;
        GroupName = groupName;
        GroupCode = groupCode;
    }
//    public Users(Locations u){
////        this.name = u.name;
////       // this.email = u.email;
////        this.phoneNumber = u.phoneNumber;
////        this.photo = u.photo;
//        //this.userLocations=u.userLocations;
////        this.LastLocation =u;
//    }

//    public Users(String name, String email, String phoneNumber, String photo, ArrayList<String> invitations, ArrayList<String> groupName, ArrayList<String> groupCode, ArrayList<Locations> userLocations, Locations lastLocation) {
//        this.name = name;
//        this.email = email;
//        this.phoneNumber = phoneNumber;
//        this.photo = photo;
//        Invitations = invitations;
//        GroupName = groupName;
//        GroupCode = groupCode;
//        this.userLocations = userLocations;
////        LastLocation = lastLocation;
//    }
public Users(String name,String photo,String phoneNumber,boolean status){

}

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
