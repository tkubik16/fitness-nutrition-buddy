package com.example.fitnessandnutritionbuddy.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.fitnessandnutritionbuddy.R;

import com.example.fitnessandnutritionbuddy.databinding.ActivityUserRegisterBinding;
import com.example.fitnessandnutritionbuddy.ui.profile.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterInfo extends AppCompatActivity {

    private Button registerButton;
    private LinearLayout myLayout;
    private EditText age, height, weight, gender, fullname;
    public static User user;
    public FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register_info);
        getSupportActionBar().hide();

        age = findViewById(R.id.age_input);
        height = findViewById(R.id.height_input);
        weight = findViewById(R.id.weight_input);
        gender = findViewById(R.id.gender_input);
        fullname = findViewById(R.id.fullname_input);


        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> {
            registerUser();
        });
    }

    public void registerUser(){
        Map<String, Object> info = new HashMap<>();
        info.put("age", Integer.valueOf(String.valueOf(age.getText())));
        info.put("height", Integer.valueOf(String.valueOf(height.getText())));
        info.put("meals", null);
        info.put("name", String.valueOf(fullname.getText()));
        info.put("weight", Integer.valueOf(String.valueOf(weight.getText())));
        info.put("gender", "female");
        info.put("preferences", null);
        info.put("mealplan", null);
        info.put("workoutplan", null);
        db.collection("users").document(User.username).set(info);
        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, UserLogin.class);
        startActivity(i);
    }
}