package com.example.fitnessandnutritionbuddy.ui.planning;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

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
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitnessandnutritionbuddy.MainActivity;
import com.example.fitnessandnutritionbuddy.R;
import com.example.fitnessandnutritionbuddy.databinding.FragmentPlanningBinding;
import com.example.fitnessandnutritionbuddy.ui.profile.User;
import com.example.fitnessandnutritionbuddy.ui.search.Exercise;
import com.example.fitnessandnutritionbuddy.ui.search.Meal;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlanningFragment extends Fragment {

    private PlanningViewModel mViewModel;
    private FragmentPlanningBinding binding;
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public TabLayout tabLayout;
    private ListView planListView;
    public ArrayList<MealPlan> arrayOfMealPlans;
    public ArrayList<WorkoutPlan> arrayOfWorkoutPlans;
    public boolean selectedMealTab = true;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(PlanningViewModel.class);
        binding = FragmentPlanningBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        planListView = root.findViewById(R.id.planListView);
        loadMealPlans();
        refreshMealPlans();

        planListView.setOnItemClickListener((adapterView, view, position, l) -> {

            //Layout stuff
            LayoutInflater inflater1 = (LayoutInflater) getActivity().getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            final View popupView = inflater1.inflate(R.layout.editable_popup_window2, null);
            final PopupWindow mPopupWindow = new PopupWindow(popupView, 900, ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopupWindow.setElevation(5.0f);
            mPopupWindow.setOutsideTouchable(false);
            mPopupWindow.setFocusable(true);
            mPopupWindow.showAtLocation(root.findViewById(R.id.planningFrameLayout), Gravity.CENTER, 0, -40);

            ImageView exitButton = popupView.findViewById(R.id.epwExitButton);
            ImageView planImage = popupView.findViewById(R.id.planImage);
            Button selectButton = popupView.findViewById(R.id.select_btn);
            TextView title = popupView.findViewById(R.id.title);
            TextView description = popupView.findViewById(R.id.description);
            TextView header1 = popupView.findViewById(R.id.header1);
            TextView header2 = popupView.findViewById(R.id.header2);
            TextView header3 = popupView.findViewById(R.id.header3);
            TextView goal1 = popupView.findViewById(R.id.goal1);
            TextView goal2 = popupView.findViewById(R.id.goal2);
            TextView goal3 = popupView.findViewById(R.id.goal3);
            TextView goal4 = popupView.findViewById(R.id.goal4);
            TextView goal5 = popupView.findViewById(R.id.goal5);
            TextView goal6 = popupView.findViewById(R.id.goal6);

            if(selectedMealTab){
                MealPlan selectedPlan = arrayOfMealPlans.get(position);

                title.setText(selectedPlan.title);
                description.setText(selectedPlan.description);
                planImage.setImageResource(selectedPlan.photo);
                exitButton.setColorFilter(Color.WHITE);
                header1.setText("Weekly Nutrition Goals");
                header2.setText("Weekly Caloric Goal:");
                goal1.setText("Carbs: " + selectedPlan.carbMin);
                goal2.setText("Protein: " + selectedPlan.proteinMin);
                goal3.setText("Fiber: " + selectedPlan.fiberMin);
                goal4.setText("Fats: " + selectedPlan.fatLimit);
                goal5.setText("Sugars: " + selectedPlan.sugarLimit);
                goal6.setText("Calories: " + selectedPlan.calorieLimit);

                exitButton.setOnClickListener(view1 -> {
                    clearDim((ViewGroup)root);
                    mPopupWindow.dismiss();
                });

                selectButton.setOnClickListener(view1 -> {
                    clearDim((ViewGroup)root);
                    mPopupWindow.dismiss();
                    updateMealPlans(arrayOfMealPlans.get(position));
                    refreshMealPlans();
                });
            }
            else{
                WorkoutPlan selectedPlan = arrayOfWorkoutPlans.get(position);

                title.setText(selectedPlan.title);
                description.setText(selectedPlan.description);
                planImage.setImageResource(selectedPlan.photo);
                exitButton.setColorFilter(Color.WHITE);
                header1.setText("Weekly Workout Goals");
                header2.setText("Weekly Calorie Burn Goal:");
                goal1.setText("Cardio: " + selectedPlan.cardioMin);
                goal2.setText("Strength: " + selectedPlan.strengthMin);
                goal3.setText("Yoga: " + selectedPlan.yogaMin);
                goal6.setText("Calories: " + selectedPlan.calorieMin);
                header3.setVisibility(View.GONE);
                goal4.setVisibility(View.GONE);
                goal5.setVisibility(View.GONE);

                exitButton.setOnClickListener(view1 -> {
                    clearDim((ViewGroup)root);
                    mPopupWindow.dismiss();
                });

                selectButton.setOnClickListener(view1 -> {
                    clearDim((ViewGroup)root);
                    mPopupWindow.dismiss();
                    updateWorkoutPlans(arrayOfWorkoutPlans.get(position));
                    refreshWorkoutPlans();
                });
            }



            applyDim((ViewGroup) root, 0.5f);
        });

        return root;
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals("Meals")) {
                    selectedMealTab = true;
                    loadMealPlans();
                    refreshMealPlans();
                }
                else {
                    selectedMealTab = false;
                    loadWorkoutPlans();
                    refreshWorkoutPlans();
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

    public void loadMealPlans(){
        arrayOfMealPlans = new ArrayList<>();
        arrayOfMealPlans.add(new MealPlan("Protein Plan", "This plan focuses on protein intake",R.drawable.protein_plan, false, 0,1050,0,0, 0, 18000));
        arrayOfMealPlans.add(new MealPlan("Carbohydrates Plan", "This plan focuses on carbohydrates intake",R.drawable.carbs_plan, false, 3400,0,0,0, 0, 17000));
        arrayOfMealPlans.add(new MealPlan("Healthy Plan", "This plan focuses on caloric intake",R.drawable.healthy_plan, false, 700,600,1500,350, 150, 10000));
        arrayOfMealPlans.add(new MealPlan("Fiber Plan", "This plan focuses on fiber intake",R.drawable.fiber_plan, false, 0,0,2000,0, 0, 11000));

        MealPlanAdapter adapter = new MealPlanAdapter(getActivity(), android.R.layout.simple_list_item_1, arrayOfMealPlans);
        planListView.setAdapter(adapter);
    }

    public void updateMealPlans(MealPlan plan){
        DocumentReference ref = db.collection("users").document(User.getUsername());
        ref.update("mealplan", plan);
    }

    public void refreshMealPlans(){
        DocumentReference docRef = db.collection("users").document(User.getUsername());

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                Map<String, Object> group = (Map<String, Object>) document.get("mealplan");
                if(group != null){
                    for(int i = 0; i < arrayOfMealPlans.size(); i++){
                        if(arrayOfMealPlans.get(i).title.equals(group.get("title"))){
                            arrayOfMealPlans.get(i).selected = true;
                            Log.i("test",(String) group.get("title"));
                        }
                        else{
                            arrayOfMealPlans.get(i).selected = false;
                        }

                    }
                }
                MealPlanAdapter adapter = new MealPlanAdapter(getActivity(), android.R.layout.simple_list_item_1, arrayOfMealPlans);
                planListView.setAdapter(adapter);
            }
        });

    }

    public void loadWorkoutPlans(){
        arrayOfWorkoutPlans = new ArrayList<>();
        arrayOfWorkoutPlans.add(new WorkoutPlan("Cardio Plan", "This plan focuses on cardio workouts",R.drawable.running_plan, false, 500,100,100,14000));
        arrayOfWorkoutPlans.add(new WorkoutPlan("Strength Plan", "This plan focuses on building strength",R.drawable.strength_plan, false, 0,500,0,13000));
        arrayOfWorkoutPlans.add(new WorkoutPlan("Yoga Plan", "This plan focuses on yoga",R.drawable.yoga_plan, false, 0,0,500,12000));
        arrayOfWorkoutPlans.add(new WorkoutPlan("Weight Loss Plan", "This plan focuses on weight loss",R.drawable.weight_plan, false, 300,200,100,11000));

        WorkoutPlanAdapter adapter = new WorkoutPlanAdapter(getActivity(), android.R.layout.simple_list_item_1, arrayOfWorkoutPlans);
        planListView.setAdapter(adapter);
    }

    public void updateWorkoutPlans(WorkoutPlan plan){
        DocumentReference ref = db.collection("users").document(User.getUsername());
        ref.update("workoutplan", plan);
    }

    public void refreshWorkoutPlans(){
        DocumentReference docRef = db.collection("users").document(User.getUsername());

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                Map<String, Object> group = (Map<String, Object>) document.get("workoutplan");

                if(group != null){
                    for(int i = 0; i < arrayOfWorkoutPlans.size(); i++){
                        if(arrayOfWorkoutPlans.get(i).title.equals(group.get("title"))){
                            arrayOfWorkoutPlans.get(i).selected = true;
                            Log.i("test",(String) group.get("title"));
                        }
                        else{
                            arrayOfWorkoutPlans.get(i).selected = false;
                        }

                    }
                }

                WorkoutPlanAdapter adapter = new WorkoutPlanAdapter(getActivity(), android.R.layout.simple_list_item_1, arrayOfWorkoutPlans);
                planListView.setAdapter(adapter);
            }
        });

    }
}