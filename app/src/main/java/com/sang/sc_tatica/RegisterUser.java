package com.sang.sc_tatica;

public class RegisterUser {
    private String userEmail;
    private String userName;
    private String userRank;

    public RegisterUser() {
    }

    public RegisterUser(String userEmail, String userName, String userRank) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.userRank = userRank;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRank() {
        return userRank;
    }

    public void setUserRank(String userRank) {
        this.userRank = userRank;
    }

    @Override
    public String toString() {
        return "RegisterUser{" +
                "userEmail='" + userEmail + '\'' +
                ", userName='" + userName + '\'' +
                ", userRank='" + userRank + '\'' +
                '}';
    }
}
