package com.sang.sc_tatica;

public class Priority {
    private String priority;
    private String description;
    private int ColorImage;

    public Priority(String priority, String description, int colorImage) {
        this.priority = priority;
        this.description = description;
        ColorImage = colorImage;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getColorImage() {
        return ColorImage;
    }

    public void setColorImage(int colorImage) {
        ColorImage = colorImage;
    }

    @Override
    public String toString() {
        return "Priority{" +
                "priority='" + priority + '\'' +
                ", description='" + description + '\'' +
                ", ColorImage=" + ColorImage +
                '}';
    }
}
