package com.example.fitnessandnutritionbuddy.ui.planning;

import android.graphics.drawable.Drawable;

public class MealPlan {

    public String title;
    public String description;
    public int photo;
    public boolean selected;
    public int carbMin;
    public int proteinMin;
    public int fiberMin;
    public int calorieLimit;
    public int fatLimit;
    public int sugarLimit;

    public MealPlan(String title, String description, int photo, boolean selected, int carbMin, int proteinMin, int fiberMin, int fatLimit, int sugarLimit, int calorieLimit){
        this.title = title;
        this.photo = photo;
        this.description = description;
        this.selected = selected;
        this.carbMin = carbMin;
        this.proteinMin = proteinMin;
        this.fiberMin = fiberMin;
        this.calorieLimit = calorieLimit;
        this.fatLimit = fatLimit;
        this.sugarLimit = sugarLimit;
    }

}
