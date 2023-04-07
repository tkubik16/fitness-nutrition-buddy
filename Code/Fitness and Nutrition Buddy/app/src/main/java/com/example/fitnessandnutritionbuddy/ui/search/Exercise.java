package com.example.fitnessandnutritionbuddy.ui.search;

import java.util.Date;

public class Exercise {

    public String name;
    public Double nf_calories;
    public Photo photo;
    public String tag_id;
    public Double duration_min;
    public Date time;
    public String exType;

    public Exercise(String name, Double calories, Photo photo, String tag_id, Double duration_min, Date time, String exType){
        this.name = name;
        this.nf_calories = calories;
        this.photo = photo;
        this.tag_id = tag_id;
        this.duration_min = duration_min;
        this.time = time;
        this.exType = exType;
    }

    public Exercise(String name, Double calories, Photo photo, String tag_id, Double duration_min, Date time){
        this.name = name;
        this.nf_calories = calories;
        this.photo = photo;
        this.tag_id = tag_id;
        this.duration_min = duration_min;
        this.time = time;
    }

    public String getImage() { return photo.getThumb(); }
}
