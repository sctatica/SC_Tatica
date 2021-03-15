package com.sang.userlogin_registration;

public class ModelUserNotes {
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
}

