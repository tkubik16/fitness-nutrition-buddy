package com.example.fitnessandnutritionbuddy.ui.home;

import static java.time.temporal.ChronoUnit.DAYS;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.fitnessandnutritionbuddy.R;
import com.example.fitnessandnutritionbuddy.databinding.FragmentHomeBinding;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class HomeFragment extends Fragment {

    private MealLogViewModel MealLogViewModel;
    DayDemoCollectionAdapter dayDemoCollectionAdapter;
    private FragmentHomeBinding binding;
    ViewPager2 viewPagerDay;
    int startInt = Integer.MAX_VALUE/2;
    public static int lastViewed = 0;
    public static LocalDate lastScrolledToDay;
    Button cButton;
    ImageButton prevDayButton;
    ImageButton nextDayButton;
    LocalDate currentDate;

    public class DayDemoCollectionAdapter extends FragmentStateAdapter {
        public DayDemoCollectionAdapter(Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            LocalDate currentFragmentDate = LocalDate.now().plusDays(position - startInt);
            return new DayFragment(currentFragmentDate);
        }

        @Override
        public int getItemCount() {
            return Integer.MAX_VALUE;
        }
    }

    public void updateDate(int difference) {
        LocalDate currentFragmentDate = LocalDate.now().plusDays(difference);
        currentDate = currentFragmentDate;
        lastScrolledToDay = currentDate;
        cButton.setText(currentFragmentDate.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        MealLogViewModel = new ViewModelProvider(this).get(MealLogViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dayDemoCollectionAdapter = new DayDemoCollectionAdapter(this);
        viewPagerDay = root.findViewById(R.id.changeDayPager2);
        viewPagerDay.setAdapter(dayDemoCollectionAdapter);
        viewPagerDay.setOffscreenPageLimit(1);
        viewPagerDay.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                lastViewed = position;
                Log.i("pos", position+"");
                Log.i("poslastViewed", lastViewed+"");
                updateDate(position - startInt);
            }
        });
        cButton = root.findViewById(R.id.calendarButton);
        cButton.setOnClickListener(view -> {
            Intent i = new Intent(getContext(), MealHistory.class );
            startActivity(i);
        });

        prevDayButton = root.findViewById(R.id.previousDayImageButton);
        prevDayButton.setOnClickListener(view -> {
            viewPagerDay.setCurrentItem(viewPagerDay.getCurrentItem()-1);
        });

        nextDayButton = root.findViewById(R.id.nextDayImageButton);
        nextDayButton.setOnClickListener(view -> {
            viewPagerDay.setCurrentItem(viewPagerDay.getCurrentItem()+1);
        });

        if(lastScrolledToDay != null) {
            long daysBetween = DAYS.between(LocalDate.now() , lastScrolledToDay);
            lastViewed = (int)daysBetween;
            viewPagerDay.setCurrentItem(lastViewed+startInt, false);
            Log.i("setDate", "onCreate");
        }
        else {
            if(lastViewed == 0) {
                viewPagerDay.setCurrentItem(startInt, false);
                lastViewed = startInt;
            }
            else {
                viewPagerDay.setCurrentItem(lastViewed, false);
            }
        }

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(lastScrolledToDay != null) {
            long daysBetween = DAYS.between(LocalDate.now() , lastScrolledToDay);
            lastViewed = (int)daysBetween;
            viewPagerDay.setCurrentItem(lastViewed+startInt, false);
            Log.i("setDate", "onResume");
        }
        else {
            if(lastViewed == 0) {
                viewPagerDay.setCurrentItem(startInt, false);
                lastViewed = startInt;
            }
            else {
                viewPagerDay.setCurrentItem(lastViewed, false);
            }
        }
    }

}