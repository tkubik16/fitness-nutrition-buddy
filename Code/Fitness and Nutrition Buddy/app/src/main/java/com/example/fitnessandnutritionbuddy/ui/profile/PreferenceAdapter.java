package com.example.fitnessandnutritionbuddy.ui.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fitnessandnutritionbuddy.R;
import com.example.fitnessandnutritionbuddy.ui.profile.Preference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PreferenceAdapter extends ArrayAdapter<Preference> {

    private Context c;

    private ArrayList<Preference> preferences;

    public PreferenceAdapter(Context c, int resId, ArrayList<Preference> preferences) {
        super(c, resId, preferences);
        this.c = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Preference preference = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.preference_list_item, parent, false);
        }

        // Lookup view for data population
        TextView prefName = convertView.findViewById(R.id.prefname_text);
        TextView prefAmt = convertView.findViewById(R.id.comparision_text);
        TextView IL = convertView.findViewById(R.id.image_list1);
        //ImageView foodThumb = convertView.findViewById(R.id.image_list1);
        // Populate the data into the template view using the data object
        String comp = preference.getCompName();
        if(preference.getCompName() == "lt"){
            comp = "<";
        }else{
            comp = ">";
        }
        prefName.setText(preference.getPrefName());
        prefAmt.setText( comp + " " + String.valueOf(preference.getNum()));
        IL.setText("Hello");
//        if (preference.getImage() != null && !meal.getImage().equals("")) {
//            Picasso.with(c).load(meal.getImage()).into(foodThumb);
//        }
        // Return the completed view to render on screen
        return convertView;


    }
}