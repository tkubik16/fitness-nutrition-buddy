package com.example.fitnessandnutritionbuddy.ui.map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<Restaurant> {

    public ListAdapter(Context context, ArrayList<Restaurant> restaurants) {
        super(context, 0, restaurants);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Restaurant restaurant = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(com.example.fitnessandnutritionbuddy.R.layout.list_item, parent, false);
        }
        // Lookup view for data population
        TextView title = (TextView) convertView.findViewById(com.example.fitnessandnutritionbuddy.R.id.item_meal_name);
        TextView distance = (TextView) convertView.findViewById(com.example.fitnessandnutritionbuddy.R.id.item_calories);
        // Populate the data into the template view using the data object
        title.setText(restaurant.name);
        distance.setText(restaurant.distance);
        // Return the completed view to render on screen

        return convertView;
    }
}