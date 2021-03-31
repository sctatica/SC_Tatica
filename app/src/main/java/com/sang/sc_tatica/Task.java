package com.sang.sc_tatica;

import android.os.Parcel;
import android.os.Parcelable;

public class Task implements Parcelable {
    private String userName;
    private String userEmail;
    private String userID;
    private String name;
    private String startTime;
    private String startDate;
    private String dueTime;
    private String dueDate;
    private String shortDesc;
    private String priority;
    private String taskId;
    private boolean doneStatus;

    public Task() {
    }

    public Task(String userName, String userEmail, String userID, String name, String startTime, String startDate, String dueTime, String dueDate, String shortDesc, String priority, String taskId) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userID = userID;
        this.name = name;
        this.startTime = startTime;
        this.startDate = startDate;
        this.dueTime = dueTime;
        this.dueDate = dueDate;
        this.shortDesc = shortDesc;
        this.priority = priority;
        this.taskId = taskId;
        this.doneStatus = false;
    }

    protected Task(Parcel in) {
        userName = in.readString();
        userEmail = in.readString();
        userID = in.readString();
        name = in.readString();
        startTime = in.readString();
        startDate = in.readString();
        dueTime = in.readString();
        dueDate = in.readString();
        shortDesc = in.readString();
        priority = in.readString();
        taskId = in.readString();
        doneStatus = in.readByte() != 0;
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public boolean isDoneStatus() {
        return doneStatus;
    }

    public void setDoneStatus(boolean doneStatus) {
        this.doneStatus = doneStatus;
    }

    @Override
    public String toString() {
        return "Task{" +
                "userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userID='" + userID + '\'' +
                ", name='" + name + '\'' +
                ", startTime='" + startTime + '\'' +
                ", startDate='" + startDate + '\'' +
                ", dueTime='" + dueTime + '\'' +
                ", dueDate='" + dueDate + '\'' +
                ", shortDesc='" + shortDesc + '\'' +
                ", priority='" + priority + '\'' +
                ", taskId='" + taskId + '\'' +
                ", doneStatus=" + doneStatus +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(userEmail);
        dest.writeString(userID);
        dest.writeString(name);
        dest.writeString(startTime);
        dest.writeString(startDate);
        dest.writeString(dueTime);
        dest.writeString(dueDate);
        dest.writeString(shortDesc);
        dest.writeString(priority);
        dest.writeString(taskId);
        dest.writeByte((byte) (doneStatus ? 1 : 0));
    }
}
