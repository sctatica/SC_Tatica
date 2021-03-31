package com.sang.sc_tatica;

import android.os.Parcel;
import android.os.Parcelable;

public class ModelUserNotes implements Parcelable {
    private String userID;
    private String userEmail;
    private String id;
    private String title;
    private String description;

    public ModelUserNotes(String userID, String userEmail, String id, String title, String description) {
        this.userID = userID;
        this.userEmail = userEmail;
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public ModelUserNotes(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    protected ModelUserNotes(Parcel in) {
        userID = in.readString();
        userEmail = in.readString();
        id = in.readString();
        title = in.readString();
        description = in.readString();
    }

    public static final Creator<ModelUserNotes> CREATOR = new Creator<ModelUserNotes>() {
        @Override
        public ModelUserNotes createFromParcel(Parcel in) {
            return new ModelUserNotes(in);
        }

        @Override
        public ModelUserNotes[] newArray(int size) {
            return new ModelUserNotes[size];
        }
    };

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ModelUserNotes{" +
                "userID='" + userID + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userID);
        dest.writeString(userEmail);
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
    }
}

