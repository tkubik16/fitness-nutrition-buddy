package com.example.fitnessandnutritionbuddy.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.fitnessandnutritionbuddy.R;
import com.example.fitnessandnutritionbuddy.ui.login.UserLogin;
import com.example.fitnessandnutritionbuddy.ui.profile.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserRegister extends AppCompatActivity {

    private Button registerButton;
    private LinearLayout myLayout;
    private EditText username;
    public static User user;
    public FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        getSupportActionBar().hide();

        myLayout = findViewById(R.id.layout);
        myLayout.getBackground().setAlpha(30);

        username = findViewById(R.id.username);

        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> {
            User.username = String.valueOf(username.getText());
            verifyUser(String.valueOf(username.getText()));
        });


    }

    public void verifyUser(String name){
        DocumentReference docRef = db.collection("users").document(name);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Toast.makeText(this, "Username Already Exists", Toast.LENGTH_SHORT).show();

                } else {
                    registerUser(name);
                }
            }
        });
    }

    public void registerUser(String name){
        Map<String, Object> info = new HashMap<>();
        info.put("age", 0);
        info.put("height", 0);
        info.put("meals", null);
        info.put("name", "Empty");
        info.put("weight", 0);
        info.put("gender", "female");
        info.put("preferences", null);
        info.put("mealplan", null);
        info.put("workoutplan", null);
        db.collection("users").document(name).set(info);
        Intent i = new Intent(this, UserRegisterInfo.class);
        startActivity(i);
    }
}