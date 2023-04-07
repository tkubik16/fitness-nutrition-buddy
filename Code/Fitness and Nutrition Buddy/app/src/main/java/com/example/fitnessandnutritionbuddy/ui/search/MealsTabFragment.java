package com.example.fitnessandnutritionbuddy.ui.search;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.fitnessandnutritionbuddy.R;
import com.example.fitnessandnutritionbuddy.databinding.FragmentTabMealsBinding;

public class MealsTabFragment extends Fragment {


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentTabMealsBinding binding2;
        binding2 = FragmentTabMealsBinding.inflate(inflater, container, false);
        View root2 = binding2.getRoot();

        Button b1 = root2.findViewById(R.id.addBreakfastButton);
        Button b2 = root2.findViewById(R.id.addLunchButton);
        Button b3 = root2.findViewById(R.id.addDinnerButton);
        Button b4 = root2.findViewById(R.id.addSnackButton);
        Button customMealButton = root2.findViewById(R.id.customMealButton);

        SearchFragment parentFrag = ((SearchFragment) MealsTabFragment.this.getParentFragment());
        b1.setOnClickListener(v -> {
            SearchFragment.searchType = "Breakfast";
            parentFrag.updateSearchHint("breakfast");
        });
        b2.setOnClickListener(v -> {
            SearchFragment.searchType = "Lunch";
            parentFrag.updateSearchHint("lunch");
        });
        b3.setOnClickListener(v -> {
            SearchFragment.searchType = "Dinner";
            parentFrag.updateSearchHint("dinner");
        });
        b4.setOnClickListener(v -> {
            SearchFragment.searchType = "Snack";
            parentFrag.updateSearchHint("snack");
        });
        customMealButton.setOnClickListener(v -> {
            Intent i = new Intent( getContext(), CustomMealActivity.class );
            startActivity( i);
        });

        return root2;
    }

}