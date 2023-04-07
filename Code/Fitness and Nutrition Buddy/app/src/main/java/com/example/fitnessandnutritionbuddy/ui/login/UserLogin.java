package com.example.fitnessandnutritionbuddy.ui.login;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.fitnessandnutritionbuddy.MainActivity;
import com.example.fitnessandnutritionbuddy.R;
import com.example.fitnessandnutritionbuddy.ui.planning.MealPlan;
import com.example.fitnessandnutritionbuddy.ui.planning.WorkoutPlan;
import com.example.fitnessandnutritionbuddy.ui.planning.WorkoutPlanAdapter;
import com.example.fitnessandnutritionbuddy.ui.profile.User;
import com.example.fitnessandnutritionbuddy.ui.search.Exercise;
import com.example.fitnessandnutritionbuddy.ui.search.Meal;
import com.example.fitnessandnutritionbuddy.ui.search.Photo;
import com.google.firebase.Timestamp;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class UserLogin extends AppCompatActivity implements LocationListener{

    private Button loginButton, registerButton;
    private LinearLayout myLayout;
    private EditText username;
    public static User user;
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static List<Map<String, Object>> groups;
    protected LocationManager locationManager;
    public static ArrayList<Meal> mealArrayList = new ArrayList<>();
    public static ArrayList<Meal> sortedMealArrayList = new ArrayList<>();
    public static ArrayList<Exercise> exerciseArrayList = new ArrayList<>();
    public static ArrayList<Exercise> sortedExerciseArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        getSupportActionBar().hide();

        myLayout = findViewById(R.id.layout);
        myLayout.getBackground().setAlpha(30);

        username = findViewById(R.id.username);

        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            user = new User(String.valueOf(username.getText()), "password");
            DocumentReference docRef = db.collection("users").document(User.getUsername());
            populatePreferences( docRef);
            verifyLogin();
        });

        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> {
            Intent i = new Intent(this, UserRegister.class);
            startActivity( i);
        });

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        @SuppressLint("MissingPermission") ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, this);
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.

                            } else {
                                // No location access granted.
                            }
                        }
                );

        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

    }

    public void onLocationChanged(Location location) {
        user.coordinates[0] = String.valueOf(location.getLatitude());
        user.coordinates[1] = String.valueOf(location.getLongitude());
    }

    public void verifyLogin(){
        DocumentReference docRef = db.collection("users").document(user.getUsername());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    if(document.get("height") != null){
                        user.height = (int)((Number)document.get("height")).longValue();
                    }
                    if(document.get("weight") != null){
                        user.weight = (int)((Number)document.get("weight")).longValue();
                    }
                    if(document.get("age") != null){
                        user.age = (int)((Number)document.get("age")).longValue();
                    }
                    if(document.get("name") != null){
                        user.fullname = String.valueOf(document.get("name"));
                    }
                    if(document.get("gender") != null){
                        user.gender = String.valueOf(document.get("gender"));
                    }
                    Toast.makeText(this, "Successful Login", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(this, MainActivity.class );
                    startActivity( i);
                } else {
                    Toast.makeText(this, "Invalid Username", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void populatePreferences(DocumentReference docRef) {
        Log.i("NUTRITION_INFO", "In populatePreferences");
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                groups = (List<Map<String, Object>>) document.get("preferences");

                if(groups != null){
                    for (Map<String, Object> group : groups) {
                        String less_than = String.valueOf(group.get("less_than"));
                        String name = String.valueOf(group.get("name"));
                        int num = Integer.parseInt(String.valueOf(group.get("num")));
                        Log.i("NUTRITION_INFO", "Name: " + name);
                        Log.i("NUTRITION_INFO", "Num: " + num);

                        if( name.equals("Calorie") ){
                            if( less_than.equals("gt")){
                                User.setCaloriesGte( num);
                            }
                            else if( less_than.equals("lt")){
                                User.setCaloriesLte( num);
                            }
                        }
                        else if( name.equals("Protein")){
                            if( less_than.equals("gt")){
                                User.setProteinGte( num);
                            }
                            else if( less_than.equals("lt")){
                                User.setProteinLte( num);
                            }
                        }
                        else if( name.equals("Total Fat")){
                            if( less_than.equals("gt")){
                                User.setFatGte( num);
                            }
                            else if( less_than.equals("lt")){
                                User.setFatLte( num);
                            }
                        }
                        else if( name.equals("Sugar")){
                            if( less_than.equals("gt")){
                                User.setSugarsGte( num);
                            }
                            else if( less_than.equals("lt")){
                                User.setSugarsLte( num);
                            }
                        }
                    }
                }

                groups = (List<Map<String, Object>>) document.get("meals");
                if (groups != null) {
                    for (Map<String, Object> group : groups) {
                        Map<String, Object> groupPhoto = (Map<String, Object>) group.get("photo");
                        Timestamp timestamp = (Timestamp) group.get("time");
                        Meal m = new Meal(
                                String.valueOf(group.get("food_name")),
                                String.valueOf(group.get("image")),
                                new Photo(String.valueOf(groupPhoto.get("thumb")), "useImageURL"),
                                String.valueOf(group.get("serving_unit")),
                                String.valueOf(group.get("nix_brand_id")),
                                String.valueOf(group.get("brand_name_item_name")),
                                Double.parseDouble(String.valueOf(group.get("serving_qty"))),
                                (int) Double.parseDouble(String.valueOf(group.get("nf_calories"))),
                                String.valueOf(group.get("brand_name")),
                                String.valueOf(group.get("nix_item_id")),
                                String.valueOf(group.get("brand_type")),
                                String.valueOf(group.get("uuid")),
                                String.valueOf(group.get("tag_id")),
                                timestamp.toDate(),
                                String.valueOf(group.get("mealType")),
                                (int) Double.parseDouble(String.valueOf(group.get("nf_protein"))),
                                (int) Double.parseDouble(String.valueOf(group.get("nf_fat"))),
                                (int) Double.parseDouble(String.valueOf(group.get("nf_sugars"))),
                                (int) Double.parseDouble(String.valueOf(group.get("nf_carbs"))),
                                (int) Double.parseDouble(String.valueOf(group.get("nf_fiber")))
                        );
                        Timestamp ts = (Timestamp) group.get("time");
                        m.time = ts.toDate();
                        mealArrayList.add(m);
                    }
                    //Reverse chronological order
                    sortedMealArrayList = (ArrayList<Meal>) mealArrayList.clone();
                    sortedMealArrayList.sort(new Comparator<Meal>() {
                        @Override
                        public int compare(Meal meal1, Meal meal2) {
                            if (meal1.time == null || meal2.time == null)
                                return 0;
                            return meal2.time.compareTo(meal1.time);
                        }
                    });
                }
                groups = (List<Map<String, Object>>) document.get("exercises");
                if (groups != null) {
                    for (Map<String, Object> group : groups) {
                        Map<String, Object> groupPhoto = (Map<String, Object>) group.get("photo");
                        Timestamp timestamp = (Timestamp) group.get("time");
                        Exercise e = new Exercise(
                                String.valueOf(group.get("name")),
                                (Double) group.get("nf_calories"),
                                new Photo(String.valueOf(groupPhoto.get("thumb")), "useImageURL"),
                                String.valueOf(group.get("tag_id")),
                                (Double) group.get("duration_min"),
                                timestamp.toDate(),
                                String.valueOf(group.get("exType"))
                        );
                        Timestamp ts = (Timestamp) group.get("time");
                        e.time = ts.toDate();
                        exerciseArrayList.add(e);
                    }
                    //Reverse chronological order
                    sortedExerciseArrayList = (ArrayList<Exercise>) exerciseArrayList.clone();
                    Collections.sort(sortedExerciseArrayList, (meal1, meal2) -> {
                        if (meal1.time == null || meal2.time == null)
                            return 0;
                        return meal2.time.compareTo(meal1.time);
                    });
                }



            } else {
                Log.i("NUTRITION_INFO", "Failed to get snapshot of preferences");
            }
        });
    }
}