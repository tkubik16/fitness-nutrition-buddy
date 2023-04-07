package com.example.fitnessandnutritionbuddy.ui.search;

public class Photo {

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String url) {
        this.thumb = url;
    }

    public Photo(String thumb, String highres) {
        this.thumb = thumb;
        this.highres = highres;
    }

    private String thumb;
    private String highres;

}