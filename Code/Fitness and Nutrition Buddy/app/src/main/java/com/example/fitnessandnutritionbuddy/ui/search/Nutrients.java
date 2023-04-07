package com.example.fitnessandnutritionbuddy.ui.search;

import org.json.JSONArray;
import org.json.JSONException;

public class Nutrients {

    public int calories;
    public int protein;
    public int fat;
    public int sugars;
    public int carbs;
    public int fiber;

    public Nutrients() {
        this.calories = 0;
        this.protein = 0;
        this.fat = 0;
        this.sugars = 0;
        this.carbs = 0;
        this.fiber = 0;
    }

    public void setAllNutrients(JSONArray fullNutrients){
        try {
            for (int j = 0; j < fullNutrients.length(); j++) {

                if (fullNutrients.getJSONObject(j).getInt("attr_id") == 208) {
                    this.calories = fullNutrients.getJSONObject(j).getInt("value");
                }
                if (fullNutrients.getJSONObject(j).getInt("attr_id") == 203) {
                    this.protein = fullNutrients.getJSONObject(j).getInt("value");
                }
                if (fullNutrients.getJSONObject(j).getInt("attr_id") == 204) {
                    this.fat = fullNutrients.getJSONObject(j).getInt("value");
                }
                if (fullNutrients.getJSONObject(j).getInt("attr_id") == 269) {
                    this.sugars = fullNutrients.getJSONObject(j).getInt("value");
                }
                if (fullNutrients.getJSONObject(j).getInt("attr_id") == 205) {
                    this.carbs = fullNutrients.getJSONObject(j).getInt("value");
                }
                if (fullNutrients.getJSONObject(j).getInt("attr_id") == 291) {
                    this.fiber = fullNutrients.getJSONObject(j).getInt("value");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }


}
