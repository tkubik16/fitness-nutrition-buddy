package com.example.fitnessandnutritionbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.fitnessandnutritionbuddy.databinding.ActivityMainBinding;
import com.example.fitnessandnutritionbuddy.ui.home.HomeFragment;
import com.example.fitnessandnutritionbuddy.ui.map.MapFragment;
import com.example.fitnessandnutritionbuddy.ui.planning.PlanningFragment;
import com.example.fitnessandnutritionbuddy.ui.profile.ProfileFragment;
import com.example.fitnessandnutritionbuddy.ui.search.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;

import java.time.LocalDate;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private int startingPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_meals, R.id.navigation_planning, R.id.navigation_profile)
                .build();
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        //NavigationUI.setupWithNavController(binding.navView, navController);

        navView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        int newPosition = 0;
                        Fragment fragment = null;
                        if(navView.getSelectedItemId() != menuItem.getItemId()) {
                            switch (menuItem.getItemId()) {
                                case R.id.navigation_home:
                                    fragment = new HomeFragment();
                                    newPosition = 0;
                                    break;
                                case R.id.navigation_search:
                                    fragment = new SearchFragment();
                                    newPosition = 2;
                                    break;
                                case R.id.navigation_meals:
                                    fragment = new MapFragment();
                                    newPosition = 3;
                                    break;
                                case R.id.navigation_planning:
                                    fragment = new PlanningFragment();
                                    newPosition = 4;
                                    break;
                                case R.id.navigation_profile:
                                    fragment = new ProfileFragment();
                                    newPosition = 5;
                                    break;
                            }
                        }
                        return loadFragment(fragment, newPosition);
                    }
                }
        );


    }


    private boolean loadFragment(Fragment fragment, int newPosition) {
        if(fragment != null) {
            Log.i("startingPosition", startingPosition+"");
            Log.i("newPosition", newPosition+"");
            if(startingPosition > newPosition) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out )
                        .replace(R.id.nav_host_fragment_activity_main, fragment).commit();

            }
            else if(startingPosition < newPosition) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.nav_host_fragment_activity_main, fragment).commit();

            }
            startingPosition = newPosition;
            return true;
        }

        return false;
    }

}