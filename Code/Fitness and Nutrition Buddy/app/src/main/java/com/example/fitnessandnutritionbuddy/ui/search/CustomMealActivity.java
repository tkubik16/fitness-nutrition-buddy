package com.example.fitnessandnutritionbuddy.ui.search;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.Context;

import com.example.fitnessandnutritionbuddy.MainActivity;
import com.example.fitnessandnutritionbuddy.R;
import com.example.fitnessandnutritionbuddy.ui.login.UserLogin;
import com.example.fitnessandnutritionbuddy.ui.search.Meal;
import com.example.fitnessandnutritionbuddy.ui.search.Photo;
import com.example.fitnessandnutritionbuddy.ui.profile.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

public class CustomMealActivity extends AppCompatActivity {

    ActivityResultLauncher<Intent> launchSomeActivity;
    protected Button addCustomMealButton;
    protected EditText name;
    protected EditText calories;
    protected EditText brand;
    protected EditText servings;
    protected EditText proteins;
    protected EditText fats;
    protected EditText sugars;
    protected EditText carbs;
    protected EditText fiber;
    Spinner mealChooser;
    String mealType = "Snack";

    public FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_meal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addCustomMealButton = (Button) findViewById(R.id.add_meal);
        addCustomMealButton.setOnClickListener( addCustomMealButtonListener);
        mealChooser = findViewById(R.id.mealTypeSpinner);
        ArrayAdapter<CharSequence> mealAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.meal_type_array,
                android.R.layout.simple_spinner_dropdown_item);
        mealChooser.setAdapter(mealAdapter);
        mealChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mealType = adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        name = (EditText) findViewById( R.id.editTextMealName);
        calories = (EditText) findViewById( R.id.editTextMealCalories);
        brand = (EditText) findViewById( R.id.editTextMealBrand);
        servings = (EditText) findViewById( R.id.editTextMealServings);
        proteins = (EditText) findViewById( R.id.editTextMealProtein);
        fats = (EditText) findViewById( R.id.editTextMealFat);
        sugars = (EditText) findViewById( R.id.editTextMealSugars);
        carbs = (EditText) findViewById( R.id.editTextMealCarbs);
        fiber = (EditText) findViewById( R.id.editTextMealFiber);


        launchSomeActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //....result
                    }
                });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        launchSomeActivity.launch(myIntent);
        return true;
    }

    public View.OnClickListener addCustomMealButtonListener = new View.OnClickListener() {
        public void onClick(View v){
            //custom photo for custom meals. Needs a photo to work
            Photo foodImg = new Photo("https://cdn.pixabay.com/photo/2017/06/23/01/16/coffee-drink-2433133_1280.jpg","https://cdn.pixabay.com/photo/2017/06/23/01/16/coffee-drink-2433133_1280.jpg");
            String mealName = name.getText().toString();
            String mealCalories = calories.getText().toString();
            String servingNum = servings.getText().toString();
            String brandName = brand.getText().toString();
            String mealProtein = proteins.getText().toString();
            String mealFat = fats.getText().toString();
            String mealSugars = sugars.getText().toString();
            String mealCarbs = carbs.getText().toString();
            String mealFiber = fiber.getText().toString();

            if( servingNum.equals("")){
                servingNum = "0";
            }
            if( mealCalories.equals("")){
                mealCalories = "0";
            }
            if( mealProtein.equals("")){
                mealProtein = "0";
            }
            if( mealFat.equals("")){
                mealFat = "0";
            }
            if( mealSugars.equals("")){
                mealSugars = "0";
            }
            if( mealCarbs.equals("")){
                mealCarbs = "0";
            }
            if( mealFiber.equals("")){
                mealFiber = "0";
            }

            Meal meal = new Meal(mealName, brandName, Integer.parseInt( mealCalories), Double.parseDouble( servingNum), foodImg,
                    Integer.parseInt( mealProtein),
                    Integer.parseInt( mealFat),
                    Integer.parseInt( mealSugars),
                    Integer.parseInt( mealCarbs),
                    Integer.parseInt( mealFiber));
            meal.mealType = mealType;

            DocumentReference ref = db.collection("users").document(User.getUsername());
            if(SearchFragment.localDateFromMealLog == null)
                meal.time = Calendar.getInstance().getTime();
            else {
                meal.time = Date.from(SearchFragment.localDateFromMealLog.atStartOfDay(ZoneId.systemDefault()).toInstant());
                SearchFragment.localDateFromMealLog=null;
            }

            ref.update("meals", FieldValue.arrayUnion(meal));
            UserLogin.mealArrayList.add(meal);
            UserLogin.sortedMealArrayList = (ArrayList<Meal>) UserLogin.mealArrayList.clone();
            UserLogin.sortedMealArrayList.sort(new Comparator<Meal>() {
                @Override
                public int compare(Meal meal1, Meal meal2) {
                    if (meal1.time == null || meal2.time == null)
                        return 0;
                    return meal2.time.compareTo(meal1.time);
                }
            });

            Context context = getApplicationContext();
            CharSequence text = "Meal Added: "+ mealName;
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    };

}