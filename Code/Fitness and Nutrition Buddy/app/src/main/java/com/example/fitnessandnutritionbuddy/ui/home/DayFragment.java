package com.example.fitnessandnutritionbuddy.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.fitnessandnutritionbuddy.R;
import com.example.fitnessandnutritionbuddy.databinding.FragmentDayBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.time.LocalDate;

public class DayFragment extends Fragment {

    private @NonNull FragmentDayBinding binding;
    TabLayout tabLayout;
    ViewPager2 viewPagerMeals;
    DemoCollectionAdapter demoCollectionAdapter;
    MealsTab2Fragment mealsChild;
    Fragment workoutsChild;
    public LocalDate currentFragmentLocalDate;

    public DayFragment(LocalDate currentFragmentLocalDate) {
        this.currentFragmentLocalDate=currentFragmentLocalDate;
        Log.i("currentDate", currentFragmentLocalDate.toString());
    }

    public DayFragment() {
        // Required empty public constructor
    }

    public class DemoCollectionAdapter extends FragmentStateAdapter {
        public DemoCollectionAdapter(Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                mealsChild = new MealsTab2Fragment(currentFragmentLocalDate);
                return mealsChild;
            }
            else {
                workoutsChild = new WorkoutsTab2Fragment(currentFragmentLocalDate);
                return workoutsChild;
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tabLayout = view.findViewById(R.id.mealsTabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        new TabLayoutMediator(tabLayout, viewPagerMeals, (tab, position) -> {
            if (position == 0)
                tab.setText("Meals");
            else
                tab.setText("Workouts");
        }).attach();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDayBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        demoCollectionAdapter = new DayFragment.DemoCollectionAdapter(this);
        viewPagerMeals = root.findViewById(R.id.mealViewPager2);
        viewPagerMeals.setAdapter(demoCollectionAdapter);
        viewPagerMeals.setOffscreenPageLimit(1);

        return root;
    }

}