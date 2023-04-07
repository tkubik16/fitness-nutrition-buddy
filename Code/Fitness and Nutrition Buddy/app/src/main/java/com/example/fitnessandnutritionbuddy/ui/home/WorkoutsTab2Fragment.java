package com.example.fitnessandnutritionbuddy.ui.home;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import com.example.fitnessandnutritionbuddy.R;
import com.example.fitnessandnutritionbuddy.databinding.FragmentWorkoutsTab2Binding;
import com.example.fitnessandnutritionbuddy.ui.login.UserLogin;
import com.example.fitnessandnutritionbuddy.ui.profile.User;
import com.example.fitnessandnutritionbuddy.ui.search.Exercise;
import com.example.fitnessandnutritionbuddy.ui.search.ExerciseAdapter;
import com.example.fitnessandnutritionbuddy.ui.search.Meal;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class WorkoutsTab2Fragment extends Fragment {

    private FragmentWorkoutsTab2Binding binding;


    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public DocumentReference docRef;
    public List<Map<String, Object>> groups;
    View root;

    ListView listViewCardio;
    ListView listViewWeightLifting;
    ListView listViewYoga;
    ListView listViewExerciseLog;

    private ArrayList<Exercise> cardioArrayList = new ArrayList<>();
    private ArrayList<Exercise> weightLiftingArrayList = new ArrayList<>();
    private ArrayList<Exercise> yogaArrayList = new ArrayList<>();
    ScrollView s;


    private ArrayList<Exercise> todayExerciseArrayList = new ArrayList<>();

    public LocalDate currentFragmentLocalDate;

    public WorkoutsTab2Fragment(){}

    public WorkoutsTab2Fragment(LocalDate currentFragmentLocalDate){
        this.currentFragmentLocalDate=currentFragmentLocalDate;

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentWorkoutsTab2Binding.inflate(inflater, container, false);
        root = binding.getRoot();

        Button b1 = root.findViewById(R.id.addCardioButton2);
        Button b2 = root.findViewById(R.id.addweightLiftingButton2);
        Button b3 = root.findViewById(R.id.addYogaButton2);

        b1.setOnClickListener(view -> {
            SearchFragment.searchFromMealLog = "cardio";
            this.getParentFragment().getActivity().findViewById(R.id.navigation_search).performClick();
            SearchFragment.localDateFromMealLog = currentFragmentLocalDate;
            SearchFragment.foodSelected = false;
        });
        b2.setOnClickListener(view -> {
            SearchFragment.searchFromMealLog = "weightlifting";
            this.getParentFragment().getActivity().findViewById(R.id.navigation_search).performClick();
            SearchFragment.localDateFromMealLog = currentFragmentLocalDate;
            SearchFragment.foodSelected = false;
        });
        b3.setOnClickListener(view -> {
            SearchFragment.searchFromMealLog = "yoga";
            this.getParentFragment().getActivity().findViewById(R.id.navigation_search).performClick();
            SearchFragment.localDateFromMealLog = currentFragmentLocalDate;
            SearchFragment.foodSelected = false;
        });
        s = root.findViewById(R.id.workoutsScrollView);

        listViewCardio = root.findViewById(R.id.cardioListView);
        listViewWeightLifting = root.findViewById(R.id.weightLiftingListView);
        listViewYoga = root.findViewById(R.id.yogaListView);
        listViewExerciseLog = root.findViewById(R.id.exerciseLogListView2);

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
                todayExerciseArrayList = new ArrayList<>();
                cardioArrayList = new ArrayList<>();
                weightLiftingArrayList = new ArrayList<>();
                yogaArrayList = new ArrayList<>();
                groups = (List<Map<String, Object>>) document.get("exercises");
                if (groups != null) {
                    //Getting today's exercises
                    DayFragment d = (DayFragment)getParentFragment();
                    Date currentFragmentDate = Date.from(d.currentFragmentLocalDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                    todayExerciseArrayList.clear();
                    for (int i = 0; i < UserLogin.sortedExerciseArrayList.size(); i++) {
                        if (UserLogin.sortedExerciseArrayList.get(i).time.getDate() == currentFragmentDate.getDate() &&
                                UserLogin.sortedExerciseArrayList.get(i).time.getYear() == currentFragmentDate.getYear() &&
                                UserLogin.sortedExerciseArrayList.get(i).time.getMonth() == currentFragmentDate.getMonth()) {
                            todayExerciseArrayList.add(UserLogin.sortedExerciseArrayList.get(i));
                        }
                    }
                    for (int i = 0; i < todayExerciseArrayList.size(); i++) {
                        if (todayExerciseArrayList.get(i).exType.equals("Cardio"))
                            cardioArrayList.add(todayExerciseArrayList.get(i));
                        else if (todayExerciseArrayList.get(i).exType.equals("WeightLifting"))
                            weightLiftingArrayList.add(todayExerciseArrayList.get(i));
                        else
                            yogaArrayList.add(todayExerciseArrayList.get(i));
                    }
                    ExerciseAdapter adapter2 = new ExerciseAdapter(getContext(), android.R.layout.simple_list_item_1, cardioArrayList);
                    listViewCardio.setAdapter(adapter2);
                    modifyListViewSize(listViewCardio);
                    listViewCardio.setOnItemClickListener((adapterView, view, position, l) -> {
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
                        removeButton.setBackgroundColor(Color.rgb(189,26,26));

                        mealAdapter = ArrayAdapter.createFromResource(getContext(),
                                R.array.exercise_type_array,
                                android.R.layout.simple_spinner_dropdown_item);
                        mealChooser.setAdapter(mealAdapter);

                        final Exercise selectedExercise = cardioArrayList.get(position);

                        mealChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                selectedExercise.exType = adapterView.getItemAtPosition(i).toString();
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });

                        selectSpinnerValue(mealChooser, selectedExercise.exType);

                        //Set texts
                        servingsET.setVisibility(View.GONE);
                        foodNameET.setText(selectedExercise.name.substring(0, 1).toUpperCase() + selectedExercise.name.substring(1));
                        caloriesET.setText(selectedExercise.nf_calories + " cal");
                        brandNameET.setText(selectedExercise.duration_min + " min");
                        Picasso.with(getActivity().getApplicationContext()).load(selectedExercise.getImage()).into(foodImage);
                        mPopupWindow.setOutsideTouchable(false);
                        mPopupWindow.setFocusable(true);
                        mPopupWindow.showAtLocation(root.findViewById(R.id.WorkoutsFrameLayout), Gravity.CENTER, 0, 0);

                        updateButton.setOnClickListener(v -> {
                            clearDim((ViewGroup) root);
                            //Get all fields from user input
                            String foodName = foodNameET.getText().toString();
                            String brandName = brandNameET.getText().toString();
                            String calories = caloriesET.getText().toString();
                            String servings = servingsET.getText().toString();

                            //Delete current object from list
                            Exercise updatedExercise = cardioArrayList.get(position);
                            updatedExercise.name = foodName;
                            updatedExercise.duration_min = Double.parseDouble(brandName.replaceAll("[^0-9.]",""));
                            calories = calories.replaceAll("[^0-9.]","");
                            servings = servings.replaceAll("[^0-9.]","");
                            updatedExercise.nf_calories = Double.parseDouble(calories);

                            int pos = 0;
                            for(int i = 0; i < UserLogin.sortedExerciseArrayList.size(); i++) {
                                if(UserLogin.sortedExerciseArrayList.get(i).time==selectedExercise.time) {
                                    pos = i;
                                    break;
                                }
                            }

                            groups.remove(UserLogin.exerciseArrayList.indexOf(UserLogin.sortedExerciseArrayList.get(pos)));
                            DocumentReference ref = db.collection("users").document(User.getUsername());
                            ref.update("exercises", groups);
                            ref.update("exercises", FieldValue.arrayUnion(selectedExercise));
                            UserLogin.exerciseArrayList.remove(selectedExercise);
                            UserLogin.sortedExerciseArrayList.remove(selectedExercise);
                            UserLogin.exerciseArrayList.add(updatedExercise);
                            UserLogin.sortedExerciseArrayList = (ArrayList<Exercise>) UserLogin.exerciseArrayList.clone();
                            UserLogin.sortedExerciseArrayList.sort(new Comparator<Exercise>() {
                                @Override
                                public int compare(Exercise ex1, Exercise ex2) {
                                    if (ex1.time == null || ex2.time == null)
                                        return 0;
                                    return ex2.time.compareTo(ex1.time);
                                }
                            });
                            populateMealLog(ref);
                            mPopupWindow.dismiss();
                        });

                        removeButton.setOnClickListener(v -> {
                            clearDim((ViewGroup) root);
                            DocumentReference ref = db.collection("users").document(User.getUsername());
                            int pos = -1;
                            for(int i = 0; i < UserLogin.sortedExerciseArrayList.size(); i++) {
                                if(UserLogin.sortedExerciseArrayList.get(i).time==selectedExercise.time) {
                                    pos = i;
                                    break;
                                }
                            }
                            groups.remove(UserLogin.exerciseArrayList.indexOf(UserLogin.sortedExerciseArrayList.get(pos)));
                            UserLogin.exerciseArrayList.remove(selectedExercise);
                            UserLogin.sortedExerciseArrayList = (ArrayList<Exercise>) UserLogin.exerciseArrayList.clone();
                            UserLogin.sortedExerciseArrayList.sort(new Comparator<Exercise>() {
                                @Override
                                public int compare(Exercise ex1, Exercise ex2) {
                                    if (ex1.time == null || ex2.time == null)
                                        return 0;
                                    return ex2.time.compareTo(ex1.time);
                                }
                            });
                            ref.update("exercises", groups);
                            populateMealLog(ref);
                            mPopupWindow.dismiss();
                        });
                        exitButton.setOnClickListener(view1 -> {
                            clearDim((ViewGroup) root);
                            mPopupWindow.dismiss();
                        });

                        applyDim((ViewGroup) root, 0.5f);
                    });
                    ExerciseAdapter adapter3 = new ExerciseAdapter(getContext(), android.R.layout.simple_list_item_1, weightLiftingArrayList);
                    listViewWeightLifting.setAdapter(adapter3);
                    modifyListViewSize(listViewWeightLifting);
                    listViewWeightLifting.setOnItemClickListener((adapterView, view, position, l) -> {
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
                        removeButton.setBackgroundColor(Color.rgb(189,26,26));

                        mealAdapter = ArrayAdapter.createFromResource(getContext(),
                                R.array.exercise_type_array,
                                android.R.layout.simple_spinner_dropdown_item);
                        mealChooser.setAdapter(mealAdapter);

                        final Exercise selectedExercise = weightLiftingArrayList.get(position);

                        mealChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                selectedExercise.exType = adapterView.getItemAtPosition(i).toString();
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });

                        selectSpinnerValue(mealChooser, selectedExercise.exType);

                        //Set texts
                        servingsET.setVisibility(View.GONE);
                        foodNameET.setText(selectedExercise.name.substring(0, 1).toUpperCase() + selectedExercise.name.substring(1));
                        caloriesET.setText(selectedExercise.nf_calories + " cal");
                        brandNameET.setText(selectedExercise.duration_min + " min");
                        Picasso.with(getActivity().getApplicationContext()).load(selectedExercise.getImage()).into(foodImage);
                        mPopupWindow.setOutsideTouchable(false);
                        mPopupWindow.setFocusable(true);
                        mPopupWindow.showAtLocation(root.findViewById(R.id.WorkoutsFrameLayout), Gravity.CENTER, 0, 0);

                        updateButton.setOnClickListener(v -> {
                            clearDim((ViewGroup) root);
                            //Get all fields from user input
                            String foodName = foodNameET.getText().toString();
                            String brandName = brandNameET.getText().toString();
                            String calories = caloriesET.getText().toString();
                            String servings = servingsET.getText().toString();

                            //Delete current object from list
                            Exercise updatedExercise = weightLiftingArrayList.get(position);
                            updatedExercise.name = foodName;
                            updatedExercise.duration_min = Double.parseDouble(brandName.replaceAll("[^0-9.]",""));
                            calories = calories.replaceAll("[^0-9.]","");
                            servings = servings.replaceAll("[^0-9.]","");
                            updatedExercise.nf_calories = Double.parseDouble(calories);

                            int pos = 0;
                            for(int i = 0; i < UserLogin.sortedExerciseArrayList.size(); i++) {
                                if(UserLogin.sortedExerciseArrayList.get(i).time==selectedExercise.time) {
                                    pos = i;
                                    break;
                                }
                            }

                            groups.remove(UserLogin.exerciseArrayList.indexOf(UserLogin.sortedExerciseArrayList.get(pos)));
                            DocumentReference ref = db.collection("users").document(User.getUsername());
                            ref.update("exercises", groups);
                            ref.update("exercises", FieldValue.arrayUnion(selectedExercise));
                            UserLogin.exerciseArrayList.remove(selectedExercise);
                            UserLogin.sortedExerciseArrayList.remove(selectedExercise);
                            UserLogin.exerciseArrayList.add(updatedExercise);
                            UserLogin.sortedExerciseArrayList = (ArrayList<Exercise>) UserLogin.exerciseArrayList.clone();
                            UserLogin.sortedExerciseArrayList.sort(new Comparator<Exercise>() {
                                @Override
                                public int compare(Exercise ex1, Exercise ex2) {
                                    if (ex1.time == null || ex2.time == null)
                                        return 0;
                                    return ex2.time.compareTo(ex1.time);
                                }
                            });
                            populateMealLog(ref);
                            mPopupWindow.dismiss();
                        });

                        removeButton.setOnClickListener(v -> {
                            clearDim((ViewGroup) root);
                            DocumentReference ref = db.collection("users").document(User.getUsername());
                            int pos = 0;
                            for(int i = 0; i < UserLogin.sortedExerciseArrayList.size(); i++) {
                                if(UserLogin.sortedExerciseArrayList.get(i).time==selectedExercise.time) {
                                    pos = i;
                                    break;
                                }
                            }
                            groups.remove(UserLogin.exerciseArrayList.indexOf(UserLogin.sortedExerciseArrayList.get(pos)));
                            UserLogin.exerciseArrayList.remove(selectedExercise);
                            UserLogin.sortedExerciseArrayList = (ArrayList<Exercise>) UserLogin.exerciseArrayList.clone();
                            UserLogin.sortedExerciseArrayList.sort(new Comparator<Exercise>() {
                                @Override
                                public int compare(Exercise ex1, Exercise ex2) {
                                    if (ex1.time == null || ex2.time == null)
                                        return 0;
                                    return ex2.time.compareTo(ex1.time);
                                }
                            });
                            ref.update("exercises", groups);
                            populateMealLog(ref);
                            mPopupWindow.dismiss();
                        });
                        exitButton.setOnClickListener(view1 -> {
                            clearDim((ViewGroup) root);
                            mPopupWindow.dismiss();
                        });

                        applyDim((ViewGroup) root, 0.5f);
                    });
                    ExerciseAdapter adapter4 = new ExerciseAdapter(getContext(), android.R.layout.simple_list_item_1, yogaArrayList);
                    listViewYoga.setAdapter(adapter4);
                    modifyListViewSize(listViewYoga);
                    listViewYoga.setOnItemClickListener((adapterView, view, position, l) -> {
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
                        removeButton.setBackgroundColor(Color.rgb(189,26,26));

                        mealAdapter = ArrayAdapter.createFromResource(getContext(),
                                R.array.exercise_type_array,
                                android.R.layout.simple_spinner_dropdown_item);
                        mealChooser.setAdapter(mealAdapter);

                        final Exercise selectedExercise = yogaArrayList.get(position);

                        mealChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                selectedExercise.exType = adapterView.getItemAtPosition(i).toString();
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });

                        selectSpinnerValue(mealChooser, selectedExercise.exType);

                        //Set texts
                        servingsET.setVisibility(View.GONE);
                        foodNameET.setText(selectedExercise.name.substring(0, 1) + selectedExercise.name.substring(1));
                        caloriesET.setText(selectedExercise.nf_calories + " cal");
                        brandNameET.setText(selectedExercise.duration_min + " min");
                        Picasso.with(getActivity().getApplicationContext()).load(selectedExercise.getImage()).into(foodImage);
                        mPopupWindow.setOutsideTouchable(false);
                        mPopupWindow.setFocusable(true);
                        mPopupWindow.showAtLocation(root.findViewById(R.id.WorkoutsFrameLayout), Gravity.CENTER, 0, 0);

                        updateButton.setOnClickListener(v -> {
                            clearDim((ViewGroup) root);
                            //Get all fields from user input
                            String foodName = foodNameET.getText().toString();
                            String brandName = brandNameET.getText().toString();
                            String calories = caloriesET.getText().toString();
                            String servings = servingsET.getText().toString();

                            //Delete current object from list
                            Exercise updatedExercise = yogaArrayList.get(position);
                            updatedExercise.name = foodName;
                            updatedExercise.duration_min = Double.parseDouble(brandName.replaceAll("[^0-9.]",""));
                            calories = calories.replaceAll("[^0-9.]","");
                            updatedExercise.nf_calories = Double.parseDouble(calories);

                            int pos = -1;
                            for(int i = 0; i < UserLogin.sortedExerciseArrayList.size(); i++) {
                                if(UserLogin.sortedExerciseArrayList.get(i).time==selectedExercise.time) {
                                    pos = i;
                                    break;
                                }
                            }

                            groups.remove(UserLogin.exerciseArrayList.indexOf(UserLogin.sortedExerciseArrayList.get(pos)));
                            DocumentReference ref = db.collection("users").document(User.getUsername());
                            ref.update("exercises", groups);
                            ref.update("exercises", FieldValue.arrayUnion(selectedExercise));
                            UserLogin.exerciseArrayList.remove(selectedExercise);
                            UserLogin.sortedExerciseArrayList.remove(selectedExercise);
                            UserLogin.exerciseArrayList.add(updatedExercise);
                            UserLogin.sortedExerciseArrayList = (ArrayList<Exercise>) UserLogin.exerciseArrayList.clone();
                            UserLogin.sortedExerciseArrayList.sort(new Comparator<Exercise>() {
                                @Override
                                public int compare(Exercise ex1, Exercise ex2) {
                                    if (ex1.time == null || ex2.time == null)
                                        return 0;
                                    return ex2.time.compareTo(ex1.time);
                                }
                            });
                            populateMealLog(ref);
                            mPopupWindow.dismiss();
                        });

                        removeButton.setOnClickListener(v -> {
                            clearDim((ViewGroup) root);
                            DocumentReference ref = db.collection("users").document(User.getUsername());
                            int pos = -1;
                            for(int i = 0; i < UserLogin.sortedExerciseArrayList.size(); i++) {
                                if(UserLogin.sortedExerciseArrayList.get(i).time==selectedExercise.time) {
                                    pos = i;
                                    break;
                                }
                            }
                            groups.remove(UserLogin.exerciseArrayList.indexOf(UserLogin.sortedExerciseArrayList.get(pos)));
                            UserLogin.exerciseArrayList.remove(selectedExercise);
                            UserLogin.sortedExerciseArrayList = (ArrayList<Exercise>) UserLogin.exerciseArrayList.clone();
                            UserLogin.sortedExerciseArrayList.sort(new Comparator<Exercise>() {
                                @Override
                                public int compare(Exercise ex1, Exercise ex2) {
                                    if (ex1.time == null || ex2.time == null)
                                        return 0;
                                    return ex2.time.compareTo(ex1.time);
                                }
                            });
                            ref.update("exercises", groups);
                            populateMealLog(ref);
                            mPopupWindow.dismiss();
                        });
                        exitButton.setOnClickListener(view1 -> {
                            clearDim((ViewGroup) root);
                            mPopupWindow.dismiss();
                        });
                        applyDim((ViewGroup) root, 0.5f);
                    });
                }

            } else {
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

    @Override
    public void onResume() {
        super.onResume();
        listViewYoga.requestFocus();
        listViewWeightLifting.requestFocus();
        listViewCardio.requestFocus();
        s.scrollTo(0,0);
    }

}
