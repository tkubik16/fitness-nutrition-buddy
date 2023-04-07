package com.example.fitnessandnutritionbuddy.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.fitnessandnutritionbuddy.R;
import com.example.fitnessandnutritionbuddy.databinding.FragmentProfileBinding;
import com.example.fitnessandnutritionbuddy.ui.TextProgressBar;
import com.example.fitnessandnutritionbuddy.ui.login.UserLogin;
import com.example.fitnessandnutritionbuddy.ui.planning.MealPlan;
import com.example.fitnessandnutritionbuddy.ui.planning.WorkoutPlan;
import com.example.fitnessandnutritionbuddy.ui.search.Exercise;
import com.example.fitnessandnutritionbuddy.ui.search.Meal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment{

    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;
    TextView Name, Height, Weight, Age;
    Button nameButton;
    FloatingActionButton backButton;
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListView mealLogListView;
    private ArrayList<Meal> mealArrayList;
    private ArrayList<Exercise> exerciseArrayList;
    private ArrayList<Meal> sortedMealArrayList;
    private ArrayList<Exercise> sortedExerciseArrayList;
    private ArrayList<Preference> preferenceArrayList;
    public List<Map<String, Object>> groups;
    public DocumentReference docRef;
    public ProgressBar calorieBar;
    public int calConsumed, calBurned;
    public boolean foodSelected = true;
    public boolean isFilter = false;
    public TabLayout tabLayout;
    public ScrollView mealView;
    public ScrollView workoutView;
    public LinearLayout progressView;

    public TextView calText, carbText, proteinText, fiberText, fatText, sugarText;
    public CircularProgressIndicator calBar, carbBar, proteinBar, fiberBar, fatBar, sugarBar;
    public double calProgress, carbProgress, proteinProgress, fiberProgress, fatProgress, sugarProgress;
    public int calCount, carbCount, proteinCount, fiberCount, fatCount, sugarCount;

    public TextView caloricText, strengthText, yogaText, cardioText;
    public CircularProgressIndicator caloricBar, strengthBar, yogaBar, cardioBar;
    public double caloricProgress, strengthProgress, yogaProgress, cardioProgress;
    public int caloricCount, strengthCount, yogaCount, cardioCount;

    public TextView netText, netConsumedTitle, netBurnedTitle;
    public TextProgressBar textProgress;
    public int curCalCount, curBurnCount;

    public MealPlan mealplan;
    public WorkoutPlan workoutplan;





    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("TEST","in oncreateview");

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Name = root.findViewById(R.id.profileName);
        Height = root.findViewById(R.id.profileHeight);
        Weight = root.findViewById(R.id.profileWeight);
        Age = root.findViewById(R.id.profileAge);
        nameButton = root.findViewById(R.id.editProfile);
        mealView = root.findViewById(R.id.mealProgress);
        workoutView = root.findViewById(R.id.workoutProgress);
        mealLogListView = root.findViewById(R.id.listViewMealLog);
        progressView = root.findViewById(R.id.progress_view);

        calCount = 0;
        carbCount = 0;
        proteinCount = 0;
        fiberCount = 0;
        fatCount = 0;
        sugarCount = 0;

        caloricCount = 0;
        strengthCount = 0;
        yogaCount = 0;
        cardioCount = 0;

        curCalCount = 0;
        curBurnCount = 0;

        netText = root.findViewById(R.id.net_text);
        netConsumedTitle = root.findViewById(R.id.consume_total_text);
        netBurnedTitle = root.findViewById(R.id.burn_total_text);

        //initialize meters
        calText = root.findViewById(R.id.cals_text);
        calBar = root.findViewById(R.id.cals_bar);

        carbText = root.findViewById(R.id.carbs_text);
        carbBar = root.findViewById(R.id.carbs_bar);

        proteinText = root.findViewById(R.id.proteins_text);
        proteinBar = root.findViewById(R.id.proteins_bar);

        fiberText = root.findViewById(R.id.fibers_text);
        fiberBar = root.findViewById(R.id.fibers_bar);

        fatText = root.findViewById(R.id.fats_text);
        fatBar = root.findViewById(R.id.fats_bar);

        sugarText = root.findViewById(R.id.sugars_text);
        sugarBar = root.findViewById(R.id.sugars_bar);

        caloricText = root.findViewById(R.id.caloric_text);
        caloricBar = root.findViewById(R.id.caloric_bar);

        cardioText = root.findViewById(R.id.cardio_text);
        cardioBar = root.findViewById(R.id.cardio_bar);

        yogaText = root.findViewById(R.id.yoga_text);
        yogaBar = root.findViewById(R.id.yoga_bar);

        strengthText = root.findViewById(R.id.strength_text);
        strengthBar = root.findViewById(R.id.strength_bar);

        textProgress = root.findViewById(R.id.net_bar);

        docRef = db.collection("users").document(User.getUsername());

        populateProfileInfo();

        setHasOptionsMenu(true);

        getFireStoreData();

        nameButton.setOnClickListener(view -> {
            Intent intent=new Intent(getActivity(), profileEvent.class);
            startActivity(intent);
        });

        return root;
    }

    public static Pair<Calendar, Calendar> calculateWeeklyRange(){
        Calendar curDayLower = Calendar.getInstance();
        Calendar curDayHigher= Calendar.getInstance();

        curDayLower.setTime(Calendar.getInstance().getTime());
        curDayHigher.setTime(Calendar.getInstance().getTime());

        int lowerLimit = -(curDayLower.get(Calendar.DAY_OF_WEEK)) ;
        int upperLimit = 8 - curDayHigher.get(Calendar.DAY_OF_WEEK);

        curDayLower.add(Calendar.DATE, lowerLimit);
        curDayHigher.add(Calendar.DATE, upperLimit);

        return new Pair<>(curDayLower,curDayHigher);
    }

    public static boolean matchesDate(Calendar w, Calendar t){
        if(w.get(Calendar.YEAR) == t.get(Calendar.YEAR)){
            if(w.get(Calendar.MONTH) == t.get(Calendar.MONTH)){
                if(w.get(Calendar.DAY_OF_MONTH) == t.get(Calendar.DAY_OF_MONTH)){
                    return true;
                }
            }
        }
        return false;
    }

    public void populateNetCalorieBar(){
        int netCals = curCalCount - curBurnCount;

        netConsumedTitle.setText(curCalCount+"");
        netBurnedTitle.setText(curBurnCount+"");

        textProgress.setMax(2000);
        textProgress.setProgress(netCals);
        textProgress.setSecondaryProgress(curCalCount);

        netText.setText(netCals + "/" + "2000");
    }


    public void populateWorkoutBars(){
        ArrayList<Exercise> workoutList = UserLogin.exerciseArrayList;

        Calendar today = Calendar.getInstance();
        today.setTime(Calendar.getInstance().getTime());
        Pair<Calendar, Calendar> weeklyRange = calculateWeeklyRange();

        for(Exercise e: workoutList){
            Calendar workoutDay = Calendar.getInstance();
            workoutDay.setTime(e.time);

            if(workoutDay.after(weeklyRange.first) && workoutDay.before(weeklyRange.second)){
                caloricCount += e.nf_calories;
                if(e.exType.equals("WeightLifting")){
                    strengthCount += e.duration_min;
                }
                else if(e.exType.equals("Cardio")){
                    cardioCount += e.duration_min;
                }
                else if(e.exType.equals("Yoga")){
                    yogaCount += e.duration_min;
                }
            }

            if(matchesDate(workoutDay, today)){
                curBurnCount+= e.nf_calories;
            }

        }

        caloricProgress = (double) caloricCount/workoutplan.calorieMin;
        caloricBar.setProgress((int)(caloricProgress*100));
        caloricText.setText(caloricCount + "/" + workoutplan.calorieMin);

        strengthProgress = (double) strengthCount/workoutplan.strengthMin;
        strengthBar.setProgress((int)(strengthProgress*100));
        strengthText.setText(strengthCount + "/" + workoutplan.strengthMin + "m");

        yogaProgress = (double) yogaCount/workoutplan.yogaMin;
        yogaBar.setProgress((int)(yogaProgress*100));
        yogaText.setText(yogaCount + "/" + workoutplan.yogaMin + "m");

        cardioProgress = (double) cardioCount/workoutplan.cardioMin;
        cardioBar.setProgress((int)(cardioProgress*100));
        cardioText.setText(cardioCount + "/" + workoutplan.cardioMin + "m");
    }

    public void populateMealBars(){
        ArrayList<Meal> mealList = UserLogin.mealArrayList;

        Calendar today = Calendar.getInstance();
        today.setTime(Calendar.getInstance().getTime());
        Pair<Calendar, Calendar> weeklyRange = calculateWeeklyRange();

        for(Meal m: mealList){
            Calendar mealDay = Calendar.getInstance();
            mealDay.setTime(m.time);

            if(mealDay.after(weeklyRange.first) && mealDay.before(weeklyRange.second)){
                calCount += m.getNf_calories();
                carbCount += m.nf_carbs;
                proteinCount += m.nf_protein;
                fiberCount += m.nf_fiber;
                fatCount += m.nf_fat;
                sugarCount += m.nf_sugars;
            }

            if(matchesDate(mealDay, today)){
                curCalCount+= m.getNf_calories();
            }
        }

        calProgress = (double) calCount/mealplan.calorieLimit;
        calBar.setProgress((int)(calProgress*100));
        calText.setText(calCount + "/" + mealplan.calorieLimit);

        carbProgress = (double) carbCount/mealplan.carbMin;
        carbBar.setProgress((int)(carbProgress*100));
        carbText.setText(carbCount + "/" + mealplan.carbMin + "g");

        proteinProgress = (double) proteinCount/mealplan.proteinMin;
        proteinBar.setProgress((int)(proteinProgress*100));
        proteinText.setText(proteinCount + "/" + mealplan.proteinMin + "g");

        fiberProgress = (double) fiberCount/mealplan.fiberMin;
        fiberBar.setProgress((int)(fiberProgress*100));
        fiberText.setText(fiberCount + "/" + mealplan.fiberMin + "g");

        fatProgress = (double) fatCount/mealplan.fatLimit;
        fatBar.setProgress((int)(fatProgress*100));
        fatText.setText(fatCount + "/" + mealplan.fatLimit + "g");

        sugarProgress = (double) sugarCount/mealplan.sugarLimit;
        sugarBar.setProgress((int)(sugarProgress*100));
        sugarText.setText(sugarCount + "/" + mealplan.sugarLimit + "g");
    }

    public void getFireStoreData(){
        DocumentReference docRef = db.collection("users").document(User.getUsername());

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                Map<String, Object> group = (Map<String, Object>) document.get("mealplan");

                if(group != null){
                    mealplan = new MealPlan(
                            String.valueOf(group.get("title")),
                            String.valueOf(group.get("description")),
                            Integer.parseInt(String.valueOf(group.get("photo"))),
                            (boolean) group.get("selected"),
                            Integer.parseInt(String.valueOf(group.get("carbMin"))),
                            Integer.parseInt(String.valueOf(group.get("proteinMin"))),
                            Integer.parseInt(String.valueOf(group.get("fiberMin"))),
                            Integer.parseInt(String.valueOf(group.get("fatLimit"))),
                            Integer.parseInt(String.valueOf(group.get("sugarLimit"))),
                            Integer.parseInt(String.valueOf(group.get("calorieLimit")))
                    );
                }
                else{
                    mealplan = new MealPlan(
                            "",
                            "",
                            0,
                            false,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0
                    );
                }

                group = (Map<String, Object>) document.get("workoutplan");

                if(group != null){
                    workoutplan = new WorkoutPlan(
                            String.valueOf(group.get("title")),
                            String.valueOf(group.get("description")),
                            Integer.parseInt(String.valueOf(group.get("photo"))),
                            (boolean) group.get("selected"),
                            Integer.parseInt(String.valueOf(group.get("cardioMin"))),
                            Integer.parseInt(String.valueOf(group.get("strengthMin"))),
                            Integer.parseInt(String.valueOf(group.get("yogaMin"))),
                            Integer.parseInt(String.valueOf(group.get("calorieMin")))
                    );
                }
                else{
                    workoutplan = new WorkoutPlan(
                            "",
                            "",
                            0,
                            false,
                            0,
                            0,
                            0,
                            0
                    );
                }

                populateMealBars();
                populateWorkoutBars();
                populateNetCalorieBar();

            }
        });

    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void populateProfileInfo() {
        Name.setText(User.fullname);
        Height.setText(User.height + " cm");
        Weight.setText(User.weight + " lb");
        Age.setText(User.age + "");
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.food:
                mealLogListView.setVisibility(View.GONE);
                progressView.setVisibility(View.VISIBLE);
                return true;
            case R.id.settings:
                Intent intent=new Intent(getActivity(), profileEvent.class);
                startActivity(intent);
//                isFilter = true;
//                preferenceArrayList = new ArrayList<>();
//
//                docRef.get().addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        DocumentSnapshot document = task.getResult();
//                        //sugarAmt.setText(String.valueOf(document.get("preferences")));
//                        groups = (List<Map<String, Object>>) document.get("preferences");
//                        if(groups != null){
//
//                            for (Map<String, Object> group : groups) {
//                                Preference p = new Preference(
//                                        String.valueOf(group.get("name")),
//                                        String.valueOf(group.get("less_than")),
//                                        Integer.parseInt(String.valueOf(group.get("num")))
//                                );
//                                //Toast.makeText(getActivity(), "hey there", Toast.LENGTH_SHORT).show();
//                                preferenceArrayList.add(p);
//                            }
//                            Toast.makeText(getActivity(), "Presaved", Toast.LENGTH_SHORT).show();
//                            PreferenceAdapter adapter = new PreferenceAdapter(getActivity(), android.R.layout.simple_list_item_1, preferenceArrayList);
//                            mealLogListView.setVisibility(View.VISIBLE);
//                            progressView.setVisibility(View.GONE);
//                            mealLogListView.setAdapter(adapter);
//
//
//                            //setFieldsOnScreen(docRef);
//                        }
//                        //Map<String, Object> n = (Map<String, Object>)document.get("name");
//                    }
//                    else{
//                        preferenceArrayList.add(new Preference("Sugar", "gt", 0));
//                        preferenceArrayList.add(new Preference("Calorie", "gt", 0));
//                        preferenceArrayList.add(new Preference("Protein", "gt", 0));
//                        preferenceArrayList.add(new Preference("Total Fat", "gt", 0));
//                        //Toast.makeText(this, "Newly Saved", Toast.LENGTH_SHORT).show();
//                        Toast.makeText(getActivity(), "Presaved", Toast.LENGTH_SHORT).show();
//                        PreferenceAdapter adapter = new PreferenceAdapter(getActivity(), android.R.layout.simple_list_item_1, preferenceArrayList);
//                        mealLogListView.setVisibility(View.VISIBLE);
//                        progressView.setVisibility(View.GONE);
//                        mealLogListView.setAdapter(adapter);
//                    }
//                });


                return true;
            case R.id.logout:
                Toast.makeText(getActivity(), "Signing Out", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getActivity(), UserLogin.class);
                startActivity(i);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals("Meals")) {
                    mealView.setVisibility(View.VISIBLE);
                    workoutView.setVisibility(View.GONE);
                }
                else {
                    mealView.setVisibility(View.GONE);
                    workoutView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        populateProfileInfo();
    }
}