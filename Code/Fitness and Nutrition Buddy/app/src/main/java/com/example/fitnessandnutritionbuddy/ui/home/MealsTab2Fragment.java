package com.example.fitnessandnutritionbuddy.ui.home;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import android.widget.ScrollView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fitnessandnutritionbuddy.R;
import com.example.fitnessandnutritionbuddy.databinding.FragmentMealsTab2Binding;
import com.example.fitnessandnutritionbuddy.ui.login.UserLogin;
import com.example.fitnessandnutritionbuddy.ui.profile.User;
import com.example.fitnessandnutritionbuddy.ui.search.Exercise;
import com.example.fitnessandnutritionbuddy.ui.search.Meal;
import com.example.fitnessandnutritionbuddy.ui.search.MealAdapter;
import com.example.fitnessandnutritionbuddy.ui.search.Photo;
import com.example.fitnessandnutritionbuddy.ui.search.SearchFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MealsTab2Fragment extends Fragment {

    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public DocumentReference docRef;
    public List<Map<String, Object>> groups;

    public ArrayList<Meal> breakfastArrayList = new ArrayList<>();
    public ArrayList<Meal> lunchArrayList = new ArrayList<>();
    public ArrayList<Meal> dinnerArrayList = new ArrayList<>();
    public ArrayList<Meal> snackArrayList = new ArrayList<>();


    public LocalDate currentFragmentLocalDate;
    //This is used to display each day's meals
    public ArrayList<Meal> todayMealArrayList = new ArrayList<>();

    private ListView listViewBreakfast;
    private ListView listViewLunch;
    private ListView listViewDinner;
    private ListView listViewSnack;
    View root;
    ScrollView s;

    public MealsTab2Fragment(LocalDate currentFragmentLocalDate) {
        this.currentFragmentLocalDate = currentFragmentLocalDate;
    }

    public MealsTab2Fragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentMealsTab2Binding binding2;
        binding2 = FragmentMealsTab2Binding.inflate(inflater, container, false);
        root = binding2.getRoot();

        Button b1 = root.findViewById(R.id.addBreakfastButton2);
        Button b2 = root.findViewById(R.id.addLunchButton2);
        Button b3 = root.findViewById(R.id.addDinnerButton2);
        Button b4 = root.findViewById(R.id.addSnackButton2);
        s = root.findViewById(R.id.mealsScrollView);

        b1.setOnClickListener(view -> {
            SearchFragment.searchFromMealLog = "breakfast";
            this.getParentFragment().getActivity().findViewById(R.id.navigation_search).performClick();
            SearchFragment.localDateFromMealLog = currentFragmentLocalDate;
            SearchFragment.foodSelected = true;
        });
        b2.setOnClickListener(view -> {
            SearchFragment.searchFromMealLog = "lunch";
            this.getParentFragment().getActivity().findViewById(R.id.navigation_search).performClick();
            SearchFragment.localDateFromMealLog = currentFragmentLocalDate;
            SearchFragment.foodSelected = true;
        });
        b3.setOnClickListener(view -> {
            SearchFragment.searchFromMealLog = "dinner";
            this.getParentFragment().getActivity().findViewById(R.id.navigation_search).performClick();
            SearchFragment.localDateFromMealLog = currentFragmentLocalDate;
            SearchFragment.foodSelected = true;
        });
        b4.setOnClickListener(view -> {
            SearchFragment.searchFromMealLog = "snack";
            this.getParentFragment().getActivity().findViewById(R.id.navigation_search).performClick();
            SearchFragment.localDateFromMealLog = currentFragmentLocalDate;
            SearchFragment.foodSelected = true;
        });

        listViewBreakfast = root.findViewById(R.id.breakfastListView);
        listViewLunch = root.findViewById(R.id.lunchListView);
        listViewDinner = root.findViewById(R.id.dinnerListView);
        listViewSnack = root.findViewById(R.id.snackListView);

        docRef = db.collection("users").document(User.getUsername());
        populateMealLog(docRef);
        return root;
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

    public void populateMealLog(DocumentReference docRef) {
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                groups = (List<Map<String, Object>>) document.get("meals");
                todayMealArrayList = new ArrayList<>();
                breakfastArrayList = new ArrayList<>();
                lunchArrayList = new ArrayList<>();
                dinnerArrayList = new ArrayList<>();
                snackArrayList = new ArrayList<>();

                    //Getting today's meals
                    DayFragment d = (DayFragment) getParentFragment();
                    Date currentFragmentDate = Date.from(d.currentFragmentLocalDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                    todayMealArrayList.clear();
                    Log.i("currentDate", currentFragmentDate.toString());
                    for (int i = 0; i < UserLogin.sortedMealArrayList.size(); i++) {
                        if (UserLogin.sortedMealArrayList.get(i).time.getDate() == currentFragmentDate.getDate() &&
                                UserLogin.sortedMealArrayList.get(i).time.getYear() == currentFragmentDate.getYear() &&
                                UserLogin.sortedMealArrayList.get(i).time.getMonth() == currentFragmentDate.getMonth()) {
                            todayMealArrayList.add(UserLogin.sortedMealArrayList.get(i));
                        }
                    }
                    for (int i = 0; i < todayMealArrayList.size(); i++) {
                        if (todayMealArrayList.get(i).mealType.equals("Breakfast"))
                            breakfastArrayList.add(todayMealArrayList.get(i));
                        else if (todayMealArrayList.get(i).mealType.equals("Lunch"))
                            lunchArrayList.add(todayMealArrayList.get(i));
                        else if (todayMealArrayList.get(i).mealType.equals("Dinner"))
                            dinnerArrayList.add(todayMealArrayList.get(i));
                        else
                            snackArrayList.add(todayMealArrayList.get(i));
                    }
                    MealAdapter adapter2 = new MealAdapter(getContext(), android.R.layout.simple_list_item_1, breakfastArrayList);
                    listViewBreakfast.setAdapter(adapter2);
                    listViewBreakfast.setOnItemClickListener((adapterView, view, position, l) -> {

                        //Layout stuff
                        LayoutInflater inflater1 = (LayoutInflater) getActivity().getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                        final View popupView = inflater1.inflate(R.layout.editable_popup_window, null);
                        final PopupWindow mPopupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, 1500);
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
                        ArrayAdapter<CharSequence> mealAdapter = ArrayAdapter.createFromResource(getContext(),
                                R.array.meal_type_array,
                                android.R.layout.simple_spinner_dropdown_item);
                        mealChooser.setAdapter(mealAdapter);

                        removeButton.setText("Remove");
                        removeButton.setBackgroundColor(Color.rgb(189, 26, 26));

                        Meal selectedMeal = breakfastArrayList.get(position);

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

                        //Set texts
                        foodNameET.setText(selectedMeal.getFood_name().substring(0, 1).toUpperCase() + selectedMeal.getFood_name().substring(1));
                        caloriesET.setText((int) selectedMeal.getNf_calories() + " cal");
                        if (selectedMeal.getBrand_name() != null) {
                            brandNameET.setText(selectedMeal.getBrand_name());
                        } else {
                            brandNameET.setText(R.string.common);
                        }
                        servingsET.setText((int) selectedMeal.getServing_qty() + " " + selectedMeal.getServing_unit());
                        Picasso.with(getActivity().getApplicationContext()).load(selectedMeal.getImage()).into(foodImage);
                        mPopupWindow.setOutsideTouchable(false);
                        mPopupWindow.setFocusable(true);
                        mPopupWindow.showAtLocation(root.findViewById(R.id.MealsFrameLayout), Gravity.CENTER, 0, 0);

                        updateButton.setOnClickListener(v -> {
                            clearDim((ViewGroup) root);
                            //Get all fields from user input
                            String foodName = foodNameET.getText().toString();
                            String brandName = brandNameET.getText().toString();
                            String calories = caloriesET.getText().toString();
                            String servings = servingsET.getText().toString();

                            //Delete current object from list
                            Meal updatedMeal = breakfastArrayList.get(position);
                            updatedMeal.setFood_name(foodName);
                            updatedMeal.setBrand_name(brandName);
                            calories = calories.replaceAll("[^0-9]", "");
                            servings = servings.replaceAll("[^0-9]", "");
                            updatedMeal.setNf_calories(Integer.parseInt(calories));
                            updatedMeal.setServing_qty(Double.parseDouble(servings));

                            int pos = -1;
                            for(int i = 0; i < UserLogin.sortedMealArrayList.size(); i++) {
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
                            populateMealLog(ref);
                            mPopupWindow.dismiss();
                        });

                        removeButton.setOnClickListener(v -> {
                            clearDim((ViewGroup) root);
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
                            populateMealLog(ref);
                            mPopupWindow.dismiss();
                        });
                        exitButton.setOnClickListener(view1 -> {
                            clearDim((ViewGroup) root);
                            mPopupWindow.dismiss();
                        });
                        applyDim((ViewGroup) root, 0.5f);
                    });
                    modifyListViewSize(listViewBreakfast);
                    Log.i("currentDateMadeIt", "child");
                    MealAdapter adapter3 = new MealAdapter(getContext(), android.R.layout.simple_list_item_1, lunchArrayList);
                    listViewLunch.setAdapter(adapter3);
                    listViewLunch.setOnItemClickListener((adapterView, view, position, l) -> {

                        //Layout stuff
                        LayoutInflater inflater1 = (LayoutInflater) getActivity().getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                        final View popupView = inflater1.inflate(R.layout.editable_popup_window, null);
                        final PopupWindow mPopupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, 1500);
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
                        ArrayAdapter<CharSequence> mealAdapter = ArrayAdapter.createFromResource(getContext(),
                                R.array.meal_type_array,
                                android.R.layout.simple_spinner_dropdown_item);
                        mealChooser.setAdapter(mealAdapter);

                        removeButton.setText("Remove");
                        removeButton.setBackgroundColor(Color.rgb(189, 26, 26));

                        Meal selectedMeal = lunchArrayList.get(position);

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

                        //Set texts
                        foodNameET.setText(selectedMeal.getFood_name().substring(0, 1).toUpperCase() + selectedMeal.getFood_name().substring(1));
                        caloriesET.setText((int) selectedMeal.getNf_calories() + " cal");
                        if (selectedMeal.getBrand_name() != null) {
                            brandNameET.setText(selectedMeal.getBrand_name());
                        } else {
                            brandNameET.setText(R.string.common);
                        }
                        servingsET.setText((int) selectedMeal.getServing_qty() + " " + selectedMeal.getServing_unit());
                        Picasso.with(getActivity().getApplicationContext()).load(selectedMeal.getImage()).into(foodImage);
                        mPopupWindow.setOutsideTouchable(false);
                        mPopupWindow.setFocusable(true);
                        mPopupWindow.showAtLocation(root.findViewById(R.id.MealsFrameLayout), Gravity.CENTER, 0, 0);

                        updateButton.setOnClickListener(v -> {
                            clearDim((ViewGroup) root);
                            //Get all fields from user input
                            String foodName = foodNameET.getText().toString();
                            String brandName = brandNameET.getText().toString();
                            String calories = caloriesET.getText().toString();
                            String servings = servingsET.getText().toString();

                            //Delete current object from list
                            Meal updatedMeal = lunchArrayList.get(position);
                            updatedMeal.setFood_name(foodName);
                            updatedMeal.setBrand_name(brandName);
                            calories = calories.replaceAll("[^0-9]", "");
                            servings = servings.replaceAll("[^0-9]", "");
                            updatedMeal.setNf_calories(Integer.parseInt(calories));
                            updatedMeal.setServing_qty(Double.parseDouble(servings));

                            int pos = -1;
                            for(int i = 0; i < UserLogin.sortedMealArrayList.size(); i++) {
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
                            populateMealLog(ref);
                            mPopupWindow.dismiss();
                        });

                        removeButton.setOnClickListener(v -> {
                            clearDim((ViewGroup) root);
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
                            populateMealLog(ref);
                            mPopupWindow.dismiss();
                        });
                        exitButton.setOnClickListener(view1 -> {
                            clearDim((ViewGroup) root);
                            mPopupWindow.dismiss();
                        });
                        applyDim((ViewGroup) root, 0.5f);
                    });
                    modifyListViewSize(listViewLunch);
                    MealAdapter adapter4 = new MealAdapter(getContext(), android.R.layout.simple_list_item_1, dinnerArrayList);
                    listViewDinner.setAdapter(adapter4);
                    listViewDinner.setOnItemClickListener((adapterView, view, position, l) -> {

                        //Layout stuff
                        LayoutInflater inflater1 = (LayoutInflater) getActivity().getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                        final View popupView = inflater1.inflate(R.layout.editable_popup_window, null);
                        final PopupWindow mPopupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, 1500);
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
                        ArrayAdapter<CharSequence> mealAdapter = ArrayAdapter.createFromResource(getContext(),
                                R.array.meal_type_array,
                                android.R.layout.simple_spinner_dropdown_item);
                        mealChooser.setAdapter(mealAdapter);

                        removeButton.setText("Remove");
                        removeButton.setBackgroundColor(Color.rgb(189, 26, 26));

                        Meal selectedMeal = dinnerArrayList.get(position);

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

                        //Set texts
                        foodNameET.setText(selectedMeal.getFood_name().substring(0, 1).toUpperCase() + selectedMeal.getFood_name().substring(1));
                        caloriesET.setText((int) selectedMeal.getNf_calories() + " cal");
                        if (selectedMeal.getBrand_name() != null) {
                            brandNameET.setText(selectedMeal.getBrand_name());
                        } else {
                            brandNameET.setText(R.string.common);
                        }
                        servingsET.setText((int) selectedMeal.getServing_qty() + " " + selectedMeal.getServing_unit());
                        Picasso.with(getActivity().getApplicationContext()).load(selectedMeal.getImage()).into(foodImage);
                        mPopupWindow.setOutsideTouchable(false);
                        mPopupWindow.setFocusable(true);
                        mPopupWindow.showAtLocation(root.findViewById(R.id.MealsFrameLayout), Gravity.CENTER, 0, 0);

                        updateButton.setOnClickListener(v -> {
                            clearDim((ViewGroup) root);
                            //Get all fields from user input
                            String foodName = foodNameET.getText().toString();
                            String brandName = brandNameET.getText().toString();
                            String calories = caloriesET.getText().toString();
                            String servings = servingsET.getText().toString();

                            //Delete current object from list
                            Meal updatedMeal = dinnerArrayList.get(position);
                            updatedMeal.setFood_name(foodName);
                            updatedMeal.setBrand_name(brandName);
                            calories = calories.replaceAll("[^0-9]", "");
                            servings = servings.replaceAll("[^0-9]", "");
                            updatedMeal.setNf_calories(Integer.parseInt(calories));
                            updatedMeal.setServing_qty(Double.parseDouble(servings));

                            int pos = -1;
                            for(int i = 0; i < UserLogin.sortedMealArrayList.size(); i++) {
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
                            populateMealLog(ref);
                            mPopupWindow.dismiss();
                        });

                        removeButton.setOnClickListener(v -> {
                            clearDim((ViewGroup) root);
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
                            populateMealLog(ref);
                            mPopupWindow.dismiss();
                        });
                        exitButton.setOnClickListener(view1 -> {
                            clearDim((ViewGroup) root);
                            mPopupWindow.dismiss();
                        });
                        applyDim((ViewGroup) root, 0.5f);
                    });
                    modifyListViewSize(listViewDinner);
                    MealAdapter adapter5 = new MealAdapter(getContext(), android.R.layout.simple_list_item_1, snackArrayList);
                    listViewSnack.setAdapter(adapter5);
                    listViewSnack.setOnItemClickListener((adapterView, view, position, l) -> {

                        //Layout stuff
                        LayoutInflater inflater1 = (LayoutInflater) getActivity().getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                        final View popupView = inflater1.inflate(R.layout.editable_popup_window, null);
                        final PopupWindow mPopupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, 1500);
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
                        ArrayAdapter<CharSequence> mealAdapter = ArrayAdapter.createFromResource(getContext(),
                                R.array.meal_type_array,
                                android.R.layout.simple_spinner_dropdown_item);
                        mealChooser.setAdapter(mealAdapter);

                        removeButton.setText("Remove");
                        removeButton.setBackgroundColor(Color.rgb(189, 26, 26));

                        Meal selectedMeal = snackArrayList.get(position);

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

                        //Set texts
                        foodNameET.setText(selectedMeal.getFood_name().substring(0, 1).toUpperCase() + selectedMeal.getFood_name().substring(1));
                        caloriesET.setText((int) selectedMeal.getNf_calories() + " cal");
                        if (selectedMeal.getBrand_name() != null) {
                            brandNameET.setText(selectedMeal.getBrand_name());
                        } else {
                            brandNameET.setText(R.string.common);
                        }
                        servingsET.setText((int) selectedMeal.getServing_qty() + " " + selectedMeal.getServing_unit());
                        Picasso.with(getActivity().getApplicationContext()).load(selectedMeal.getImage()).into(foodImage);
                        mPopupWindow.setOutsideTouchable(false);
                        mPopupWindow.setFocusable(true);
                        mPopupWindow.showAtLocation(root.findViewById(R.id.MealsFrameLayout), Gravity.CENTER, 0, 0);

                        updateButton.setOnClickListener(v -> {
                            clearDim((ViewGroup) root);
                            //Get all fields from user input
                            String foodName = foodNameET.getText().toString();
                            String brandName = brandNameET.getText().toString();
                            String calories = caloriesET.getText().toString();
                            String servings = servingsET.getText().toString();

                            //Delete current object from list
                            Meal updatedMeal = snackArrayList.get(position);
                            updatedMeal.setFood_name(foodName);
                            updatedMeal.setBrand_name(brandName);
                            calories = calories.replaceAll("[^0-9]", "");
                            servings = servings.replaceAll("[^0-9]", "");
                            updatedMeal.setNf_calories(Integer.parseInt(calories));
                            updatedMeal.setServing_qty(Double.parseDouble(servings));

                            int pos = -1;
                            for(int i = 0; i < UserLogin.sortedMealArrayList.size(); i++) {
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
                            populateMealLog(ref);
                            mPopupWindow.dismiss();
                        });

                        removeButton.setOnClickListener(v -> {
                            clearDim((ViewGroup) root);
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
                            populateMealLog(ref);
                            mPopupWindow.dismiss();
                        });
                        exitButton.setOnClickListener(view1 -> {
                            clearDim((ViewGroup) root);
                            mPopupWindow.dismiss();
                        });
                        applyDim((ViewGroup) root, 0.5f);
                    });
                    modifyListViewSize(listViewSnack);
                }

             else {
                Log.d("Error", "Failed to get snapshot", task.getException());
            }
        });
    }

    private void selectSpinnerValue(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(myString)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    public static void applyDim(@NonNull ViewGroup parent, float dimAmount) {
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

    @Override
    public void onResume() {
        super.onResume();
        listViewSnack.requestFocus();
        listViewDinner.requestFocus();
        listViewLunch.requestFocus();
        listViewBreakfast.requestFocus();
        s.scrollTo(0,0);
    }

}