package com.example.fitnessandnutritionbuddy.ui.search;

import java.util.ArrayList;

public class DataContainer {

    public DataContainer(ArrayList<Meal> branded, ArrayList<Meal> self, ArrayList<Meal> common) {
        this.branded = branded;
        this.self = self;
        this.common = common;
    }

    public ArrayList<Meal> getBranded() {
        return branded;
    }

    public ArrayList<Meal> getSelf() {
        return self;
    }

    public ArrayList<Meal> getCommon() {
        return common;
    }

    private ArrayList<Meal> branded;
    private ArrayList<Meal> self;
    private ArrayList<Meal> common;
}
