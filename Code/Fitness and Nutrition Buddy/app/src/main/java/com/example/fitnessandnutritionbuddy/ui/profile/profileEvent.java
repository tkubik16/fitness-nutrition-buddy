package com.example.fitnessandnutritionbuddy.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitnessandnutritionbuddy.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class profileEvent extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    EditText inputName, inputWeight, inputHeight, inputAge, inputGender;
    Button nameButton;
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button popupTester,  popupTester2, popupTester3, popupTester4;
    EditText sugarAmt, calAmt, protAmt, fatAmt;
    ArrayList<Preference> prefList = new ArrayList<>();
    ArrayList<Preference> prefList2 = new ArrayList<>();
    public List<Map<String, Object>> groups;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inputName = findViewById(R.id.fullname_input);
        inputWeight = findViewById(R.id.weight_input);
        inputHeight = findViewById(R.id.height_input);
        inputAge = findViewById(R.id.age_input);
        inputGender = findViewById(R.id.gender_input);

        nameButton = findViewById(R.id.saveProfile);
        popupTester = findViewById(R.id.popup_button);
        popupTester2 = findViewById(R.id.popup_button2);
        popupTester3 = findViewById(R.id.popup_button3);
        popupTester4 = findViewById(R.id.popup_button4);
        sugarAmt = findViewById(R.id.pref_sugar_amt);
        calAmt = findViewById(R.id.pref_calorie_amt);
        protAmt = findViewById(R.id.pref_protein_amt);
        fatAmt = findViewById(R.id.pref_fat_amt);
        DocumentReference ref = db.collection("users").document(User.getUsername());
        createPrefList();
        createPrefList2(ref);

        popupTester.setOnClickListener(view -> {
            PopupMenu popUp = new PopupMenu(this, view);
            popUp.setOnMenuItemClickListener( this);
            popUp.inflate(R.menu.popup_menu1);
            popUp.show();
        });
        popupTester2.setOnClickListener(view -> {
            PopupMenu popUp = new PopupMenu(this, view);
            popUp.setOnMenuItemClickListener( this);
            popUp.inflate(R.menu.popup_menu2);
            popUp.show();
        });
        popupTester3.setOnClickListener(view -> {
            PopupMenu popUp = new PopupMenu(this, view);
            popUp.setOnMenuItemClickListener(this);
            popUp.inflate(R.menu.popup_menu3);
            popUp.show();
        });
        popupTester4.setOnClickListener(view -> {
            PopupMenu popUp = new PopupMenu(this, view);
            popUp.setOnMenuItemClickListener( this);
            popUp.inflate(R.menu.popup_menu4);
            popUp.show();
        });

        nameButton.setOnClickListener(view -> {

            String name = inputName.getText().toString();
            String weight = inputWeight.getText().toString();
            String height = inputHeight.getText().toString();
            String age = inputAge.getText().toString();
            String gender = inputGender.getText().toString();

            if(name != null){
                ref.update("name", name);
                User.fullname = name;
            }

            if(weight != null){
                ref.update("weight", Integer.valueOf(weight));
                User.weight = Integer.parseInt(weight);
            }

            if(height!= null){
                ref.update("height", Integer.valueOf(height));
                User.height = Integer.parseInt(height);
            }

            if(age!= null){
                ref.update("age", Integer.valueOf(age));
                User.age = Integer.parseInt(age);
            }

            if(gender!= null){
                ref.update("gender", gender);
                User.gender = gender;
            }

            updateSugar();
            updateCal();
            updateProt();
            updateFat();

            ref.update("preferences", FieldValue.delete());
            ref.update("preferences", FieldValue.arrayUnion(prefList.get(0)));
            ref.update("preferences", FieldValue.arrayUnion(prefList.get(1)));
            ref.update("preferences", FieldValue.arrayUnion(prefList.get(2)));
            ref.update("preferences", FieldValue.arrayUnion(prefList.get(3)));


            populatePreferences();
            finish();
        });
    }
    public void updateSugar(){
        String newLTSugar = popupTester.getText().toString();
        int newLimSugar = Integer.parseInt(sugarAmt.getText().toString());
        prefList.get(0).num = newLimSugar;
        prefList.get(0).less_than = newLTSugar;
        //Log.i("Error", "In sugar!");
    }
    public void updateCal(){
        String newLTCal = popupTester2.getText().toString();
        int newLimCal = Integer.parseInt(calAmt.getText().toString());
        prefList.get(1).num = newLimCal;
        prefList.get(1).less_than = newLTCal;
    }
    public void updateProt(){
        String newLTProt = popupTester3.getText().toString();
        int newLimProt = Integer.parseInt(protAmt.getText().toString());
        prefList.get(2).num = newLimProt;
        prefList.get(2).less_than = newLTProt;
    }
    public void updateFat(){
        String newLTFat = popupTester4.getText().toString();
        int newLimFat = Integer.parseInt(protAmt.getText().toString());
        prefList.get(3).num = newLimFat;
        prefList.get(3).less_than = newLTFat;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.popup_pref_item1:
                popupTester.setText("gt");
                Toast.makeText(this, "item 1 clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.popup_pref_item3:
                popupTester.setText("lt");
                Toast.makeText(this, "item 3 clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.popup_pref_item4:
                popupTester2.setText("gt");
                Toast.makeText(this, "item 4 clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.popup_pref_item6:
                popupTester2.setText("lt");
                Toast.makeText(this, "item 6 clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.popup_pref_item7:
                popupTester3.setText("gt");
                Toast.makeText(this, "item 7 clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.popup_pref_item9:
                popupTester3.setText("lt");
                Toast.makeText(this, "item 9 clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.popup_pref_item10:
                popupTester4.setText("gt");
                Toast.makeText(this, "item 10 clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.popup_pref_item12:
                popupTester4.setText("lt");
                Toast.makeText(this, "item 12 clicked", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;

        }
    }


    public void createPrefList2(DocumentReference docRef){
        prefList.add(new Preference("Sugar", "gt", 0));
        prefList.add(new Preference("Calorie", "gt", 0));
        prefList.add(new Preference("Protein", "gt", 0));
        prefList.add(new Preference("Total Fat", "gt", 0));

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                //sugarAmt.setText(String.valueOf(document.get("preferences")));
                groups = (List<Map<String, Object>>) document.get("preferences");
                if(groups != null){

                    for (Map<String, Object> group : groups) {
                        Preference p = new Preference(
                                String.valueOf(group.get("name")),
                                String.valueOf(group.get("less_than")),
                                Integer.parseInt(String.valueOf(group.get("num")))
                        );
                        prefList2.add(p);
                    }

                    Toast.makeText(this, "Presaved", Toast.LENGTH_SHORT).show();
                    setFieldsOnScreen(docRef);
                }
                //Map<String, Object> n = (Map<String, Object>)document.get("name");
            }
            else{
                prefList.add(new Preference("Sugar", "gt", 0));
                prefList.add(new Preference("Calorie", "gt", 0));
                prefList.add(new Preference("Protein", "gt", 0));
                prefList.add(new Preference("Total Fat", "gt", 0));
                Toast.makeText(this, "Newly Saved", Toast.LENGTH_SHORT).show();
            }
        });// task
    }// docReg

    public void createPrefList(){
        prefList.add(new Preference("Sugar", "gt", 0));
        prefList.add(new Preference("Calorie", "gt", 0));
        prefList.add(new Preference("Protein", "gt", 0));
        prefList.add(new Preference("Total Fat", "gt", 0));
    }

    public void setFieldsOnScreen(DocumentReference docRef){
        popupTester.setText(prefList2.get(0).less_than);
        popupTester2.setText(prefList2.get(1).less_than);
        popupTester3.setText(prefList2.get(2).less_than);
        popupTester4.setText(prefList2.get(3).less_than);

        sugarAmt.setText(String.valueOf(prefList2.get(0).num));
        calAmt.setText(String.valueOf(prefList2.get(1).num));
        protAmt.setText(String.valueOf(prefList2.get(2).num));
        fatAmt.setText(String.valueOf(prefList2.get(3).num));

        inputName.setText(User.fullname);
        inputWeight.setText(String.valueOf(User.weight));
        inputAge.setText(String.valueOf(User.age));
        inputHeight.setText(String.valueOf(User.height));
        inputGender.setText(User.gender);

    }
    //TODO: make this work to update User preferences using the pref list
    public void populatePreferences() {
        String name = "";
        String less_than = "";
        int num = -1;

        User.setCaloriesGte(-1);
        User.setCaloriesLte(-1);
        User.setProteinGte(-1);
        User.setProteinLte(-1);
        User.setFatGte(-1);
        User.setFatLte(-1);
        User.setSugarsGte(-1);
        User.setSugarsLte(-1);


        for (int i = 0; i < 3; i++) {
            name = prefList.get(i).name;
            less_than = prefList.get(i).less_than;
            num = prefList.get(i).num;

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
}
