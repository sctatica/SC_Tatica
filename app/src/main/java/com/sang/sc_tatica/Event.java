package com.sang.sc_tatica;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Source;

import java.util.ArrayList;

public class Event implements Parcelable {
    private String name;
    private String organizers;
    private String location;
    private String description;
    private String startTime;
    private String startDate;
    private String dueTime;
    private String dueDate;
    private String eventID;
    private String image;
    private String isLocal;
    private String limitRegister;
    private String isDonate;
    private String timeDonate;
    private String moneyDonate;
    private ArrayList<RegisterUser> usersList;

    public Event() {
    }

    public Event(String name, String organizers, String location, String description, String startTime, String startDate, String dueTime, String dueDate, String eventID, String image, String isLocal, String limitRegister, String isDonate, String timeDonate, String moneyDonate, ArrayList<RegisterUser> usersList) {
        this.name = name;
        this.organizers = organizers;
        this.location = location;
        this.description = description;
        this.startTime = startTime;
        this.startDate = startDate;
        this.dueTime = dueTime;
        this.dueDate = dueDate;
        this.eventID = eventID;
        this.image = image;
        this.isLocal = isLocal;
        this.limitRegister = limitRegister;
        this.isDonate = isDonate;
        this.timeDonate = timeDonate;
        this.moneyDonate = moneyDonate;
        this.usersList = usersList;
    }

    public Event(String name, String organizers, String location, String description, String startTime, String startDate, String dueTime, String dueDate, String eventID, String image, String isLocal, String limitRegister, ArrayList<RegisterUser> usersList) {
        this.name = name;
        this.organizers = organizers;
        this.location = location;
        this.description = description;
        this.startTime = startTime;
        this.startDate = startDate;
        this.dueTime = dueTime;
        this.dueDate = dueDate;
        this.eventID = eventID;
        this.image = image;
        this.isLocal = isLocal;
        this.limitRegister = limitRegister;
        this.isDonate = "false";
        this.timeDonate = "0";
        this.moneyDonate = "0";
        this.usersList = usersList;
    }

    protected Event(Parcel in) {
        name = in.readString();
        organizers = in.readString();
        location = in.readString();
        description = in.readString();
        startTime = in.readString();
        startDate = in.readString();
        dueTime = in.readString();
        dueDate = in.readString();
        eventID = in.readString();
        image = in.readString();
        isLocal = in.readString();
        limitRegister = in.readString();
        isDonate = in.readString();
        timeDonate = in.readString();
        moneyDonate = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganizers() {
        return organizers;
    }

    public void setOrganizers(String organizers) {
        this.organizers = organizers;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getIsDonate() {
        return isDonate;
    }

    public void setIsDonate(String isDonate) {
        this.isDonate = isDonate;
    }

    public String getTimeDonate() {
        return timeDonate;
    }

    public void setTimeDonate(String timeDonate) {
        this.timeDonate = timeDonate;
    }

    public String getMoneyDonate() {
        return moneyDonate;
    }

    public void setMoneyDonate(String moneyDonate) {
        this.moneyDonate = moneyDonate;
    }

    public ArrayList<RegisterUser> getUsersList() {
        return usersList;
    }

    public void setUsersList(ArrayList<RegisterUser> usersList) {
        this.usersList = usersList;
    }

    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", organizers='" + organizers + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", startTime='" + startTime + '\'' +
                ", startDate='" + startDate + '\'' +
                ", dueTime='" + dueTime + '\'' +
                ", dueDate='" + dueDate + '\'' +
                ", eventID='" + eventID + '\'' +
                ", image='" + image + '\'' +
                ", isLocal='" + isLocal + '\'' +
                ", limitRegister='" + limitRegister + '\'' +
                ", isDonate='" + isDonate + '\'' +
                ", timeDonate='" + timeDonate + '\'' +
                ", moneyDonate='" + moneyDonate + '\'' +
                ", usersList=" + usersList +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(organizers);
        dest.writeString(location);
        dest.writeString(description);
        dest.writeString(startTime);
        dest.writeString(startDate);
        dest.writeString(dueTime);
        dest.writeString(dueDate);
        dest.writeString(eventID);
        dest.writeString(image);
        dest.writeString(isLocal);
        dest.writeString(limitRegister);
        dest.writeString(isDonate);
        dest.writeString(timeDonate);
        dest.writeString(moneyDonate);
    }
}
