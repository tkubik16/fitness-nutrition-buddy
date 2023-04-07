package com.example.fitnessandnutritionbuddy.ui.home;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.fitnessandnutritionbuddy.MainActivity;
import com.example.fitnessandnutritionbuddy.R;
import com.example.fitnessandnutritionbuddy.ui.login.UserLogin;
import com.example.fitnessandnutritionbuddy.ui.search.Meal;
import com.example.fitnessandnutritionbuddy.ui.search.MealAdapter;
import com.example.fitnessandnutritionbuddy.ui.search.Photo;

import com.example.fitnessandnutritionbuddy.ui.profile.User;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MealHistory extends AppCompatActivity {

    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListView mealLogListView;
    private ArrayList<Meal> mealArrayList = new ArrayList<>();
    public List<Map<String, Object>> groups;
    public ActivityResultLauncher<Intent> launchSomeActivity;
    public CalendarDay selectedDate;
    CalendarDay startDay;
    public ProgressBar calorieBar;
    public TextView calCount;
    public  int calories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_history);
        getSupportActionBar().setTitle("Meal History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        calorieBar = findViewById(R.id.progressBar);
        calCount = findViewById(R.id.calorie_text);
        calories = 0;

        selectedDate = startDay = CalendarDay.from(HomeFragment.lastScrolledToDay.getYear(),
                HomeFragment.lastScrolledToDay.getMonthValue(),
                HomeFragment.lastScrolledToDay.getDayOfMonth());

        launchSomeActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //....result
                    }
                });

        mealLogListView = this.findViewById(R.id.listViewMealLog);

        DocumentReference docRef = db.collection("users").document(User.getUsername());
        populateMealLog(docRef, startDay);

        MaterialCalendarView mcv = findViewById(R.id.calendarView);

        mcv.state().edit()
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit();
        mcv.setDateSelected(selectedDate, true);
        mcv.setCurrentDate(startDay);

        mcv.setOnDateChangedListener((widget, date, selected) -> {
            selectedDate = date;
            calCount.setText("0/2000");
            calorieBar.setProgress(0);
            calories = 0;
            populateMealLog(docRef, date);
        });



    }

    public static void applyDim(@NonNull ViewGroup parent, float dimAmount){
        Drawable dim = new ColorDrawable(Color.BLACK);
        dim.setBounds(0, 0, parent.getWidth(), parent.getHeight());
        dim.setAlpha((int) (255 * dimAmount));

        ViewGroupOverlay overlay = parent.getOverlay();
        overlay.add(dim);
    }

    public static void clearDim(@NonNull ViewGroup parent) {
        ViewGroupOverlay overlay = parent.getOverlay();
        overlay.clear();
    }

    public void modifyListViewSize(ListView listview) {
        ListAdapter listadp = listview.getAdapter();
        if (listadp != null) {
            int totalHeight = 0;
            for (int i = 0; i < listadp.getCount(); i++) {
                View listItem = listadp.getView(i, null, listview);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listview.getLayoutParams();
            params.height = totalHeight + (listview.getDividerHeight() * (listadp.getCount() - 1));
            listview.setLayoutParams(params);
            listview.requestLayout();
        }
    }


    public void populateMealLog(DocumentReference docRef, CalendarDay date) {
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mealArrayList.clear();
                DocumentSnapshot document = task.getResult();

                groups = (List<Map<String, Object>>) document.get("meals");

                for (Map<String, Object> group : groups) {
                    Map<String, Object> groupPhoto = (Map<String, Object>) group.get("photo");
                    Timestamp timestamp = (Timestamp) group.get("time");
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(timestamp.toDate());

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
                            String.valueOf(group.get("brand_type")),
                            String.valueOf(group.get("nix_item_id")),
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

                    if ((cal.get(Calendar.DAY_OF_MONTH) == date.getDay()) && (cal.get(Calendar.MONTH) + 1 == date.getMonth()) && (cal.get(Calendar.YEAR) == date.getYear())) {
                        mealArrayList.add(m);
                        incrementCalories(m);
                    }
                }
                //Reverse chronological order
                UserLogin.sortedMealArrayList = (ArrayList<Meal>) UserLogin.mealArrayList.clone();
                Collections.sort(UserLogin.sortedMealArrayList, (meal1, meal2) -> {
                    if (meal1.time == null || meal2.time == null)
                        return 0;
                    return meal2.time.compareTo(meal1.time);
                });
                MealAdapter adapter = new MealAdapter(this, android.R.layout.simple_list_item_1, mealArrayList);
                mealLogListView.setAdapter(adapter);
                mealLogListView.setOnItemClickListener((adapterView, view, position, l) -> {
                    Meal selectedMeal = mealArrayList.get(position);

                    //Layout stuff
                    LayoutInflater inflater1 = (LayoutInflater) this.getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    final View popupView = inflater1.inflate(R.layout.editable_popup_window, null);
                    final PopupWindow mPopupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, 1600);
                    mPopupWindow.setElevation(5.0f);

                    //View Declarations
                    ImageButton exitButton = popupView.findViewById(R.id.epwExitButton);
                    EditText foodNameET = popupView.findViewById(R.id.epwFoodEditText);
                    EditText caloriesET = popupView.findViewById(R.id.epwCaloriesEditText);
                    EditText brandNameET = popupView.findViewById(R.id.epwBrandEditText);
                    EditText servingsET = popupView.findViewById(R.id.epwServingsEditText);
                    ImageView foodImage = popupView.findViewById(R.id.epwFoodImageView);
                    Button removeButton = popupView.findViewById(R.id.epwRemoveButton);
                    Button updateButton = popupView.findViewById(R.id.epwUpdateButton);
                    Spinner mealChooser = popupView.findViewById(R.id.epwMealTypeSpinner);
                    ArrayAdapter<CharSequence> mealAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.meal_type_array,
                            android.R.layout.simple_spinner_dropdown_item);
                    mealChooser.setAdapter(mealAdapter);
                    mealChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            selectedMeal.mealType = adapterView.getItemAtPosition(i).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                    selectSpinnerValue(mealChooser, selectedMeal.mealType);

                    removeButton.setText("Remove");
                    removeButton.setBackgroundColor(Color.rgb(189,26,26));

                    //Set texts
                    foodNameET.setText(selectedMeal.getFood_name());
                    caloriesET.setText((int)selectedMeal.getNf_calories() + " cal");
                    if (selectedMeal.getBrand_name() != null) {
                        brandNameET.setText(selectedMeal.getBrand_name());
                    } else {
                        brandNameET.setText(R.string.common);
                    }
                    servingsET.setText((int)selectedMeal.getServing_qty() + " " + selectedMeal.getServing_unit());
                    Picasso.with(this.getApplicationContext()).load(selectedMeal.getImage()).into(foodImage);
                    mPopupWindow.setOutsideTouchable(false);
                    mPopupWindow.setFocusable(true);
                    mPopupWindow.showAtLocation(findViewById(android.R.id.content).getRootView(), Gravity.CENTER, 0, 0);

                    updateButton.setOnClickListener(v -> {
                        clearDim((ViewGroup)findViewById(android.R.id.content).getRootView());
                        //Get all fields from user input
                        String foodName = foodNameET.getText().toString();
                        String brandName = brandNameET.getText().toString();
                        String calories = caloriesET.getText().toString();
                        String servings = servingsET.getText().toString();

                        //Delete current object from list
                        Meal updatedMeal = mealArrayList.get(position);
                        updatedMeal.setFood_name(foodName);
                        updatedMeal.setBrand_name(brandName);
                        calories = calories.replaceAll("[^0-9]", "");
                        servings = servings.replaceAll("[^0-9]", "");
                        updatedMeal.setNf_calories(Integer.parseInt(calories));
                        updatedMeal.setServing_qty(Double.parseDouble(servings));

                        int pos = -1;
                        Log.i("Mealtime", selectedMeal.time.toString());
                        for(int i = 0; i < UserLogin.sortedMealArrayList.size(); i++) {
                            Log.i("Mealtime", UserLogin.sortedMealArrayList.get(i).time.toString());
                            if(UserLogin.sortedMealArrayList.get(i).time==selectedMeal.time) {
                                pos = i;
                                break;
                            }
                        }

                        groups.remove(UserLogin.mealArrayList.indexOf(UserLogin.sortedMealArrayList.get(pos)));
                        DocumentReference ref = db.collection("users").document(User.getUsername());
                        ref.update("meals", groups);
                        ref.update("meals", FieldValue.arrayUnion(updatedMeal));
                        UserLogin.mealArrayList.remove(selectedMeal);
                        UserLogin.sortedMealArrayList.remove(selectedMeal);
                        UserLogin.mealArrayList.add(updatedMeal);
                        UserLogin.sortedMealArrayList = (ArrayList<Meal>) UserLogin.mealArrayList.clone();
                        UserLogin.sortedMealArrayList.sort(new Comparator<Meal>() {
                            @Override
                            public int compare(Meal meal1, Meal meal2) {
                                if (meal1.time == null || meal2.time == null)
                                    return 0;
                                return meal2.time.compareTo(meal1.time);
                            }
                        });
                        populateMealLog(docRef, selectedDate);
                        mPopupWindow.dismiss();
                    });

                    removeButton.setOnClickListener(v -> {
                        clearDim((ViewGroup)findViewById(android.R.id.content).getRootView());
                        DocumentReference ref = db.collection("users").document(User.getUsername());
                        int pos = -1;
                        for(int i = 0; i < UserLogin.sortedMealArrayList.size(); i++) {
                            if(UserLogin.sortedMealArrayList.get(i).time==selectedMeal.time) {
                                pos = i;
                                break;
                            }
                        }

                        groups.remove(UserLogin.mealArrayList.indexOf(UserLogin.sortedMealArrayList.get(pos)));
                        UserLogin.mealArrayList.remove(selectedMeal);
                        UserLogin.sortedMealArrayList = (ArrayList<Meal>) UserLogin.mealArrayList.clone();
                        UserLogin.sortedMealArrayList.sort(new Comparator<Meal>() {
                            @Override
                            public int compare(Meal meal1, Meal meal2) {
                                if (meal1.time == null || meal2.time == null)
                                    return 0;
                                return meal2.time.compareTo(meal1.time);
                            }
                        });
                        ref.update("meals", groups);
                        populateMealLog(docRef, selectedDate);
                        mPopupWindow.dismiss();
                    });

                    exitButton.setOnClickListener(view1 -> {
                        clearDim((ViewGroup)findViewById(android.R.id.content).getRootView());
                        mPopupWindow.dismiss();
                    });

                    applyDim((ViewGroup) findViewById(android.R.id.content).getRootView(), 0.5f);

                });
                modifyListViewSize(mealLogListView);
            } else {
                Log.d("Error", "Failed to get snapshot", task.getException());
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
        String s = selectedDate.toString();
        s = s.substring(12);
        s=s.substring(0, s.length()-1);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-M-d");
        LocalDate a = LocalDate.parse(s, df);
        HomeFragment.lastScrolledToDay = a;
        Log.i("setDate", a.toString());
        Log.i("setDate", "MealHistory");
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        String s = selectedDate.toString();
        s = s.substring(12);
        s=s.substring(0, s.length()-1);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-M-d");
        LocalDate a = LocalDate.parse(s, df);
        HomeFragment.lastScrolledToDay = a;
        Log.i("setDateOptions", a.toString());
        Log.i("setDateOptions", "MealHistory");
        Log.i("onOptionsItemSelected","Made it");
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        launchSomeActivity.launch(myIntent);
        return true;
    }

    public void incrementCalories(Meal m){
        calorieBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(3,169,244)));
        calCount.setTextColor(Color.WHITE);
        int newCal = (int)m.getNf_calories();
        calories += newCal;
        int setTo = (newCal * 100)/2000;
        calorieBar.incrementProgressBy(setTo);
        if(calories >= 1500 && calories <= 1800){
            calorieBar.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
            calCount.setTextColor(Color.BLACK);
        }
        if(calories > 1800){
            calorieBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
        }
        calCount.setText(calories + "/2000");
    }
    private void selectSpinnerValue(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(myString)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

}