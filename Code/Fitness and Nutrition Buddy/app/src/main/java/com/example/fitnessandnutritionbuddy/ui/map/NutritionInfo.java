package com.example.fitnessandnutritionbuddy.ui.map;

import org.json.JSONArray;
import org.json.JSONException;

public class NutritionInfo {
    private String food_name;
    private String brand_name;
    private double serving_qty;
    private String serving_unit;
    private double serving_weight_grams;
    private double nf_calories;
    private double nf_total_fat;
    private double nf_saturated_fat;
    private double nf_cholesterol;
    private double nf_sodium;
    private double nf_total_carbohydrate;
    private double nf_dietary_fiber;
    private double nf_sugars;
    private double nf_protein;
    private double nf_potassium;
    private double nf_p;

    public NutritionInfo( String food_name, String brand_name, double serving_qty, String serving_unit, double serving_weight_grams, double nf_calories, double nf_total_fat, double nf_saturated_fat, double nf_cholesterol, double nf_sodium, double nf_total_carbohydrate, double nf_dietary_fiber, double nf_sugars, double nf_protein, double nf_potassium, double nf_p){
        this.food_name = food_name;
        this.brand_name = brand_name;
        this.serving_qty = serving_qty;
        this.serving_unit = serving_unit;
        this.serving_weight_grams = serving_weight_grams;
        this.nf_calories = nf_calories;
        this.nf_total_fat = nf_total_fat;
        this.nf_saturated_fat = nf_saturated_fat;
        this.nf_cholesterol = nf_cholesterol;
        this.nf_sodium = nf_sodium;
        this.nf_total_carbohydrate = nf_total_carbohydrate;
        this.nf_dietary_fiber = nf_dietary_fiber;
        this.nf_sugars = nf_sugars;
        this.nf_protein = nf_protein;
        this.nf_potassium = nf_potassium;
        this.nf_p = nf_p;

    }

    public NutritionInfo( JSONArray nutritionInfo){
        try{
            this.food_name = nutritionInfo.getJSONObject(0).getString("food_name");
            this.brand_name = nutritionInfo.getJSONObject(0).getString("brand_name");
            this.serving_qty = nutritionInfo.getJSONObject(0).getDouble("serving_qty");
            this.serving_unit = nutritionInfo.getJSONObject(0).getString("serving_unit");
            this.serving_weight_grams = nutritionInfo.getJSONObject(0).getDouble("serving_weight_grams");
            this.nf_calories = nutritionInfo.getJSONObject(0).getDouble("nf_calories");
            this.nf_total_fat = nutritionInfo.getJSONObject(0).getDouble("nf_total_fat");
            this.nf_saturated_fat = nutritionInfo.getJSONObject(0).getDouble("nf_saturated_fat");
            this.nf_cholesterol = nutritionInfo.getJSONObject(0).getDouble("nf_cholesterol");
            this.nf_sodium = nutritionInfo.getJSONObject(0).getDouble("nf_sodium");
            this.nf_total_carbohydrate = nutritionInfo.getJSONObject(0).getDouble("nf_total_carbohydrate");
            this.nf_dietary_fiber = nutritionInfo.getJSONObject(0).getDouble("nf_dietary_fiber");
            this.nf_sugars = nutritionInfo.getJSONObject(0).getDouble("nf_sugars");
            this.nf_protein = nutritionInfo.getJSONObject(0).getDouble("nf_protein");
            this.nf_potassium = nutritionInfo.getJSONObject(0).getDouble("nf_potassium");
            this.nf_p = nutritionInfo.getJSONObject(0).getDouble("nf_p");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getFood_name(){
        return this.food_name;
    }
}
