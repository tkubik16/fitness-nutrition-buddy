package com.example.fitnessandnutritionbuddy.ui.search;


import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;

//Rough sketch of a class for a meal object. Make any changes to my code necessary. No one owns code in agile -Thomas
public class Meal {


    public String getFood_name() {
        return food_name;
    }

    public String getImageURL() { return imageURL;}

    public String getImage() { return photo.getThumb(); }

    public String getServing_unit() {
        return serving_unit;
    }

    public String getNix_brand_id() {
        return nix_brand_id;
    }

    public String getBrand_name_item_name() {
        return brand_name_item_name;
    }

    public double getServing_qty() {
        return serving_qty;
    }

    public double getNf_calories() {
        return nf_calories;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public String getBrand_type() {
        return brand_type;
    }

    public String getNix_item_id() {
        return nix_item_id;
    }

    public String getUuid() {
        return uuid;
    }

    public String getTag_id() {
        return tag_id;
    }

    public Meal(String food_name, String image, Photo photo, String serving_unit, String nix_brand_id, String brand_name_item_name,
                double serving_qty, int nf_calories, String brand_name, String brand_type, String nix_item_id, String uuid, String tag_id, Date time, String mealType) {
        this.food_name = food_name;
        this.imageURL = image;
        this.serving_unit = serving_unit;
        this.nix_brand_id = nix_brand_id;
        this.brand_name_item_name = brand_name_item_name;
        this.serving_qty = serving_qty;
        this.nf_calories = nf_calories;
        this.brand_name = brand_name;
        this.brand_type = brand_type;
        this.nix_item_id = nix_item_id;
        this.uuid = uuid;
        this.tag_id = tag_id;
        this.photo = photo;
        this.time = time;
        this.mealType = mealType;
    }

    public Meal(String food_name, String image, Photo photo, String serving_unit, String nix_brand_id, String brand_name_item_name,
                double serving_qty, int nf_calories, String brand_name, String brand_type, String nix_item_id, String uuid, String tag_id, Date time, String mealType, int protein, int fat, int sugars, int carbs, int fiber) {
        this.food_name = food_name;
        this.imageURL = image;
        this.serving_unit = serving_unit;
        this.nix_brand_id = nix_brand_id;
        this.brand_name_item_name = brand_name_item_name;
        this.serving_qty = serving_qty;
        this.nf_calories = nf_calories;
        this.brand_name = brand_name;
        this.brand_type = brand_type;
        this.nix_item_id = nix_item_id;
        this.uuid = uuid;
        this.tag_id = tag_id;
        this.photo = photo;
        this.time = time;
        this.mealType = mealType;
        this.nf_protein = protein;
        this.nf_fat = fat;
        this.nf_sugars = sugars;
        this.nf_carbs = carbs;
        this.nf_fiber = fiber;
    }


    public Meal(String food_name) {
        this.food_name = food_name;
    }

    public Meal(String food_name, String brand, int calories, double servings, Photo photo, int protein, int fat, int sugars, int carbs, int fiber) {

        this.food_name = food_name;
        this.brand_name = brand;
        this.nf_calories = calories;
        this.serving_qty = servings;
        this.photo = photo;
        this.nf_protein = protein;
        this.nf_fat = fat;
        this.nf_sugars = sugars;
        this.nf_carbs = carbs;
        this.nf_fiber = fiber;
    }

    public Meal() {
        this.photo = new Photo("", "");

    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public void setImage(String url) {
        this.photo.setThumb(url);
    }

    public void setServing_unit(String serving_unit) {
        this.serving_unit = serving_unit;
    }

    public void setNix_brand_id(String nix_brand_id) {
        this.nix_brand_id = nix_brand_id;
    }

    public void setBrand_name_item_name(String brand_name_item_name) {
        this.brand_name_item_name = brand_name_item_name;
    }

    public void setServing_qty(double serving_qty) {
        this.serving_qty = serving_qty;
    }

    public void setNf_calories(int nf_calories) {
        this.nf_calories = nf_calories;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public void setBrand_type(String brand_type) {
        this.brand_type = brand_type;
    }

    public void setNix_item_id(String nix_item_id) {
        this.nix_item_id = nix_item_id;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setTag_id(String tag_id) {
        this.tag_id = tag_id;
    }

    private String food_name;
    public Photo photo;
    private String serving_unit;
    private String nix_brand_id;
    private String brand_name_item_name;
    private double serving_qty;
    private double nf_calories;
    private String brand_name;
    private String brand_type;
    private String nix_item_id;
    private String uuid;
    private String tag_id;
    public Date time;
    private String imageURL;
    public String mealType;
    public int nf_protein;
    public int nf_fat;
    public int nf_sugars;
    public int nf_carbs;
    public int nf_fiber;
}
