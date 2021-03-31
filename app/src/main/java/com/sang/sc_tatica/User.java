package com.sang.sc_tatica;

import java.util.ArrayList;

public class User {
    private String name;
    private String email;
    private String phone;
    private String image;
    private String coverImage;
    private String userID;
    private String birthday;
    private String timeWork;
    private String rank;
    private ArrayList<String> friends;

    public User() {
    }

    public User(String email, String userID) {
        this.name = "";
        this.email = email;
        this.phone = "";
        this.image = "";
        this.coverImage = "";
        this.userID = userID;
        this.birthday = "";
        this.timeWork = "0";
        this.rank = "Newbie";
        this.friends = new ArrayList<>();
    }

    public User(String name, String email, String phone, String image, String coverImage, String userID, String birthday, String timeWork, String rank) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.image = image;
        this.coverImage = coverImage;
        this.userID = userID;
        this.birthday = birthday;
        this.timeWork = timeWork;
        this.rank = rank;
        this.friends = new ArrayList<>();
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getTimeWork() {
        return timeWork;
    }

    public void setTimeWork(String timeWork) {
        this.timeWork = timeWork;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public ArrayList<String> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", image='" + image + '\'' +
                ", coverImage='" + coverImage + '\'' +
                ", userID='" + userID + '\'' +
                ", birthday='" + birthday + '\'' +
                ", timeWork='" + timeWork + '\'' +
                ", rank='" + rank + '\'' +
                ", friends=" + friends +
                '}';
    }
}
