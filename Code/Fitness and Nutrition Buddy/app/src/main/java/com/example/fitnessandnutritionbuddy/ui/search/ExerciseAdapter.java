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

public class ExerciseAdapter extends ArrayAdapter<Exercise> {
    private Context c;

    private ArrayList<Meal> meals;

    public ExerciseAdapter(Context c, int resId, ArrayList<Exercise> ex) {
        super(c, resId, ex);
        this.c = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Exercise ex = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.meal_list_item, parent, false);
        }

        // Lookup view for data population
        TextView foodName = convertView.findViewById(R.id.foodname_text);
        TextView calories = convertView.findViewById(R.id.calorie_text);
        TextView servingSize = convertView.findViewById(R.id.serving_text);
        TextView brandName = convertView.findViewById(R.id.brandname_text);
        ImageView foodThumb = convertView.findViewById(R.id.image_list);
        // Populate the data into the template view using the data object
        foodName.setText(ex.name);
        calories.setText(ex.nf_calories + " cal");
        brandName.setText("Duration: " + ex.duration_min +" min");

        if (ex.getImage() != null && !ex.getImage().equals("")) {
            Picasso.with(c).load(ex.getImage()).into(foodThumb);
        }
        // Return the completed view to render on screen
        return convertView;


    }
}
