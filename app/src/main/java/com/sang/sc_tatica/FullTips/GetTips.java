package com.sang.sc_tatica.FullTips;

public class GetTips {

    // model class
    String type;
    String title;
    String imageUrl;
    String description;

    public GetTips() {
    }

    public GetTips(String type, String title, String image, String description) {
        this.type = type;
        this.title = title;
        this.imageUrl = image;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
