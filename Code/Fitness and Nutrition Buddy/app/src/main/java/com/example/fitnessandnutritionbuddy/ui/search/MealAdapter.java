package com.example.fitnessandnutritionbuddy.ui.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fitnessandnutritionbuddy.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MealAdapter extends ArrayAdapter<Meal> {

    private Context c;

    private ArrayList<Meal> meals;

    public MealAdapter(Context c, int resId, ArrayList<Meal> meals) {
        super(c, resId, meals);
        this.c = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Meal meal = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.meal_list_item, parent, false);
        }

        // Lookup view for data population
        TextView foodName = convertView.findViewById(R.id.foodname_text);
        TextView calories = convertView.findViewById(R.id.calorie_text);
        TextView servingSize = convertView.findViewById(R.id.serving_text);
        TextView brandName = convertView.findViewById(R.id.brandname_text);
        ImageView foodThumb = convertView.findViewById(R.id.image_list);
        TextView protein = convertView.findViewById(R.id.protein_text);
        TextView fat = convertView.findViewById(R.id.fat_text);
        TextView sugars = convertView.findViewById(R.id.sugars_text);
        // Populate the data into the template view using the data object
        foodName.setText(meal.getFood_name());
        calories.setText(meal.getNf_calories() + " cal");
        protein.setText(meal.nf_protein + " g protein");
        fat.setText(meal.nf_fat + " g fat");
        sugars.setText(meal.nf_sugars + " g sugars");
        if (meal.getBrand_name() != null) {
            brandName.setText(meal.getBrand_name());
        } else {
            brandName.setText(R.string.common);
        }
        if (meal.getServing_unit() != null) {
            servingSize.setText(meal.getServing_qty() + " " + meal.getServing_unit());
        }
        if (meal.getImage() != null && !meal.getImage().equals("")) {
            Picasso.with(c).load(meal.getImage()).into(foodThumb);
        }
        // Return the completed view to render on screen
        return convertView;


    }
}