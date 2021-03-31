package com.sang.sc_tatica;

public class FeedbackModel {

    private String feedbackData;
    private String userEmail;


    public FeedbackModel(String feedbackData, String userEmail) {
        this.feedbackData = feedbackData;
        this.userEmail = userEmail;
    }

    public String getFeedbackData() {
        return feedbackData;
    }

    public String getUserEmail() {
        return userEmail;
    }
}




