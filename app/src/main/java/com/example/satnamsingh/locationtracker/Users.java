package com.example.satnamsingh.locationtracker;

public class Users {
    private String name;
    private String email;
    private String phoneNumber;
    private String photo;
    Users(){

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
        if(this.phoneNumber.startsWith("+91"))
        {
            this.phoneNumber = this.phoneNumber.replace("+91","");
        }
        this.photo ="https://firebasestorage.googleapis.com/v0/b/locationtracker-28250.appspot.com/o/default_pic.jpg?alt=media&token=7ed5b932-09bf-422e-aa5e-77eca30f378f";
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
