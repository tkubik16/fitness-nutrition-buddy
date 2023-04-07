package com.example.fitnessandnutritionbuddy.ui.search;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.fitnessandnutritionbuddy.R;
import com.example.fitnessandnutritionbuddy.databinding.FragmentSearchBinding;
import com.example.fitnessandnutritionbuddy.databinding.FragmentTabWorkoutsBinding;

public class WorkoutsTabFragment extends Fragment {

    private FragmentTabWorkoutsBinding binding;
    private FragmentSearchBinding binding3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTabWorkoutsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button b1 = root.findViewById(R.id.addCardioButton);
        Button b2 = root.findViewById(R.id.addweightLiftingButton);
        Button b3 = root.findViewById(R.id.addYogaButton);

        SearchFragment parentFrag = ((SearchFragment) WorkoutsTabFragment.this.getParentFragment());

        b1.setOnClickListener(v -> {
            SearchFragment.searchType = "Cardio";
            parentFrag.updateSearchHint("cardio");
        });
        b2.setOnClickListener(v -> {
            SearchFragment.searchType = "Weightlifting";
            parentFrag.updateSearchHint("weightlifting");

        });
        b3.setOnClickListener(v -> {
            SearchFragment.searchType = "Yoga";
            parentFrag.updateSearchHint("yoga");
        });


        return root;
    }
}