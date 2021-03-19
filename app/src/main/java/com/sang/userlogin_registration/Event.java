package com.sang.userlogin_registration;

import java.util.ArrayList;

public class Event {
    private ArrayList<User> usersList;
    private String name;
    private String eventID;
    private String image;
    private String startTime;
    private String startDate;
    private String dueTime;
    private String dueDate;
    private String description;
    private String isLocal;
    private String limitRegister;

    public Event(String name, String eventID, String image, String startTime, String startDate, String dueTime, String dueDate, String description, String isLocal, String limitRegister) {
        usersList = new ArrayList<>();
        this.name = name;
        this.eventID = eventID;
        this.image = image;
        this.startTime = startTime;
        this.startDate = startDate;
        this.dueTime = dueTime;
        this.dueDate = dueDate;
        this.description = description;
        this.isLocal = isLocal;
        this.limitRegister = limitRegister;
    }

    public Event() {
    }

    public ArrayList<User> getUsersList() {
        return usersList;
    }

    public void setUsersList(ArrayList<User> usersList) {
        this.usersList = usersList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEvenID(String eventID) {
        this.eventID = eventID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsLocal() {
        return isLocal;
    }

    public void setIsLocal(String isLocal) {
        this.isLocal = isLocal;
    }

    public String getLimitRegister() {
        return limitRegister;
    }

    public void setLimitRegister(String limitRegister) {
        this.limitRegister = limitRegister;
    }

    @Override
    public String toString() {
        return "Event{" +
                "usersList=" + usersList +
                ", name='" + name + '\'' +
                ", evenID='" + eventID + '\'' +
                ", image='" + image + '\'' +
                ", startTime='" + startTime + '\'' +
                ", startDate='" + startDate + '\'' +
                ", dueTime='" + dueTime + '\'' +
                ", dueDate='" + dueDate + '\'' +
                ", description='" + description + '\'' +
                ", isLocal='" + isLocal + '\'' +
                ", limitRegister='" + limitRegister + '\'' +
                '}';
    }
}
