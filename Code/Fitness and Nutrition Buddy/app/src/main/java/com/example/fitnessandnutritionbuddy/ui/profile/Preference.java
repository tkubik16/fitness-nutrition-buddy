package com.example.fitnessandnutritionbuddy.ui.profile;

public class Preference {
    public String name;
    public String less_than;
    public int num;

    public Preference(String n, String lt, int numb){
        name = n;
        less_than = lt;
        num = numb;
    }

    public String getPrefName(){
        return name;
    }

    public String getCompName(){
        return less_than;
    }

    public int getNum(){
        return num;
    }
}
