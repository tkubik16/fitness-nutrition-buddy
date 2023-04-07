package com.example.fitnessandnutritionbuddy.ui.planning;

public class WorkoutPlan {

    public String title;
    public String description;
    public int photo;
    public boolean selected;
    public int cardioMin;
    public int strengthMin;
    public int yogaMin;
    public int calorieMin;

    public WorkoutPlan(String title, String description, int photo, boolean selected, int cardioMin, int strengthMin, int yogaMin, int calorieMin){
        this.title = title;
        this.photo = photo;
        this.description = description;
        this.selected = selected;
        this.cardioMin = cardioMin;
        this.strengthMin = strengthMin;
        this.yogaMin = yogaMin;
        this.calorieMin = calorieMin;
    }

}
