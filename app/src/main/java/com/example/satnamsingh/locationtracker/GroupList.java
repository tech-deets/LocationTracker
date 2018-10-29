package com.example.satnamsingh.locationtracker;

public class GroupList {
    private String name;
    private String photo;
    private String phoneNumber;
    GroupList(){

    }
    GroupList(String name, String photo)
    {
        this.name=name;
        this.photo=photo;

    }
    GroupList(String name, String photo, String PhoneNumber)
    {
        this.name=name;
        this.photo=photo;
        this.phoneNumber=PhoneNumber;

    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
