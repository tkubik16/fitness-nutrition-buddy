package com.example.fitnessandnutritionbuddy.ui.planning;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fitnessandnutritionbuddy.R;
import com.example.fitnessandnutritionbuddy.ui.search.Meal;

import java.util.ArrayList;

public class WorkoutPlanAdapter extends ArrayAdapter<WorkoutPlan> {

    private Context c;

    public WorkoutPlanAdapter(Context c, int resId, ArrayList<WorkoutPlan> plans) {
        super(c, resId, plans);
        this.c = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        WorkoutPlan plan = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.plan_list_item, parent, false);
        }

        TextView title = convertView.findViewById(R.id.title);
        TextView req = convertView.findViewById(R.id.requirements);
        ImageView planImg = convertView.findViewById(R.id.planImage);
        ImageView infoImg = convertView.findViewById(R.id.info_img);

        if(plan.selected){
            infoImg.setVisibility(View.VISIBLE);
        }

        planImg.setImageResource(plan.photo);
        planImg.setClipToOutline(true);
        title.setText(plan.title);
        req.setText(plan.description);
        return convertView;


    }

}