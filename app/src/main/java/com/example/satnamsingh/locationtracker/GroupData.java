package com.example.satnamsingh.locationtracker;

import java.util.ArrayList;

public class GroupData {
    private String groupId;
    private String groupName;
    private String groupOwner;
    private ArrayList<String> groupMembers ;


    GroupData() {

    }
    GroupData(String groupId,String groupName,String groupOwner,ArrayList<String> groupMembers)
    {
        this.groupId=groupId;
        this.groupName=groupName;
        this.groupOwner=groupOwner;
        this.groupMembers=groupMembers;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupOwner() {
        return groupOwner;
    }

    public void setGroupOwner(String groupOwner) {
        this.groupOwner = groupOwner;
    }

    public ArrayList<String> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(ArrayList<String> groupMembers) {
        this.groupMembers = groupMembers;
    }
}
