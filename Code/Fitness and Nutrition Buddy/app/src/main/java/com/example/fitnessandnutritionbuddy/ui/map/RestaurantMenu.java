package com.example.fitnessandnutritionbuddy.ui.map;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.fitnessandnutritionbuddy.R;
import com.example.fitnessandnutritionbuddy.databinding.FragmentSearchBinding;
import com.example.fitnessandnutritionbuddy.ui.login.UserLogin;
import com.example.fitnessandnutritionbuddy.ui.search.Photo;
import com.example.fitnessandnutritionbuddy.ui.search.SearchViewModel;
import com.example.fitnessandnutritionbuddy.ui.search.DataContainer;
import com.example.fitnessandnutritionbuddy.ui.search.Meal;
import com.example.fitnessandnutritionbuddy.ui.search.MealAdapter;
import com.example.fitnessandnutritionbuddy.ui.profile.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantMenu extends AppCompatActivity {

    private ListView lv;
    DataContainer outerDC;
    MealAdapter arrayAdapter;
    ArrayList<Meal> concatList = new ArrayList<Meal>();
    ArrayList<Meal> nutritionFilteredList = new ArrayList<Meal>();
    Meal tempMeal;
    Photo thePhoto;
    private SearchViewModel SearchViewModel;
    private FragmentSearchBinding binding;
    private SearchView searchView;
    private String[] values;
    ActivityResultLauncher<Intent> launchSomeActivity;
    public FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static JSONArray nutritionInfoBranded;
    public static JSONArray nutritionInfoCommon;
    public List<Map<String, Object>> groups;


    public DocumentReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_menu);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            values = extras.getStringArray("key");
        }

        getSupportActionBar().setTitle(values[2] + " Menu");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        launchSomeActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //....result
                    }
                });
        // will eventually remove. Only does query based off preferences
        //doMySearch(values[2]);
        nutritionFilteredList = sendAPIRequest();


        lv = this.findViewById(R.id.menu_list);
        arrayAdapter = new MealAdapter(this, android.R.layout.simple_list_item_1, concatList);




        // argument position gives the index of item which is clicked
        lv.setOnItemClickListener((arg0, v, position, arg3) -> {

            final Meal f = concatList.get(position);
            f.time = Calendar.getInstance().getTime();
            f.mealType = "Snack";
            DocumentReference ref = db.collection("users").document(User.getUsername());
            ref.update("meals", FieldValue.arrayUnion(f));
            UserLogin.mealArrayList.add(f);
            UserLogin.sortedMealArrayList = (ArrayList<Meal>) UserLogin.mealArrayList.clone();
            UserLogin.sortedMealArrayList.sort(new Comparator<Meal>() {
                @Override
                public int compare(Meal meal1, Meal meal2) {
                    if (meal1.time == null || meal2.time == null)
                        return 0;
                    return meal2.time.compareTo(meal1.time);
                }
            });

            Toast.makeText(this, "Food added!", Toast.LENGTH_SHORT).show();
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }



    public ArrayList<Meal> sendAPIRequest() {
        ArrayList<Meal> nutritionList = new ArrayList<Meal>();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://trackapi.nutritionix.com/v2/search/instant";

        //create JSONObjects to create body of call
        JSONObject jsonBody = new JSONObject();
        JSONObject full_nutrients = new JSONObject();
        JSONObject nf_calories = new JSONObject();
        JSONObject nf_protein = new JSONObject();
        JSONObject nf_total_fat = new JSONObject();
        JSONObject nf_sugars = new JSONObject();



        DocumentReference docRef = db.collection("users").document(User.getUsername());
        Log.i("NUTRITION_INFO", "User name: " + User.getUsername() );





        try {
            // populate the body of the POST api call
            jsonBody.put("query", values[2]);
            jsonBody.put("branded", "true");
            jsonBody.put("branded_type", "1");
            jsonBody.put("detailed", "true");

            if( User.getCaloriesGte() != -1) {
                nf_calories.put("gte", String.valueOf(User.getCaloriesGte()));
            }
            if( User.getCaloriesLte() != -1) {
                nf_calories.put("lte", String.valueOf(User.getCaloriesLte()));
            }
            if( User.getProteinGte() != -1) {
                nf_protein.put("gte", String.valueOf(User.getProteinGte()));
            }
            if( User.getProteinLte() != -1) {
                nf_protein.put("lte", String.valueOf(User.getProteinLte()));
            }
            if( User.getFatGte() != -1) {
                nf_total_fat.put("gte", String.valueOf(User.getFatGte()));
            }
            if( User.getFatLte() != -1) {
                nf_total_fat.put("lte", String.valueOf(User.getFatLte()));
            }
            if( User.getSugarsGte() != -1) {
                nf_sugars.put("gte", String.valueOf(User.getSugarsGte()));
            }
            if( User.getSugarsLte() != -1) {
                nf_sugars.put("lte", String.valueOf(User.getSugarsLte()));
            }
            if( (User.getCaloriesGte() != -1) || (User.getCaloriesLte() != -1)) {
                full_nutrients.put("208", nf_calories);
            }
            if( (User.getProteinGte() != -1) || (User.getProteinLte() != -1)) {
                full_nutrients.put("203", nf_protein);
            }
            if( (User.getFatGte() != -1) || (User.getFatLte() != -1)) {
                full_nutrients.put("204", nf_total_fat);
            }
            if( (User.getSugarsGte() != -1) || (User.getSugarsLte() != -1)) {
                full_nutrients.put("269", nf_sugars);
            }

            jsonBody.put("full_nutrients",full_nutrients);
            final String requestBody = jsonBody.toString();
            Log.i("NUTRITION_INFO",requestBody);

        // make the API call
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Display the first 500 characters of the response string.
                    try {
                        JSONObject jObject = new JSONObject(response);
                        Log.i("NUTRITION_INFO", "Response: " + response);
                        nutritionInfoBranded = jObject.getJSONArray("branded");
                        nutritionInfoCommon= jObject.getJSONArray("common");
                        JSONArray fullNutrients;
                        int calories = 0;
                        int protein = 0;
                        int fat = 0;
                        int sugars = 0;
                        int carbs = 0;
                        int fiber = 0;

                        // turn api response into ArrayList of Meals
                        for (int i = 0; i < nutritionInfoBranded.length(); i++) {
                            fullNutrients = nutritionInfoBranded.getJSONObject(i).getJSONArray("full_nutrients");
                            for (int j = 0; j < fullNutrients.length(); j++) {

                                if( fullNutrients.getJSONObject(j).getInt("attr_id") == 208){
                                    calories = fullNutrients.getJSONObject(j).getInt("value");
                                    Log.i("NUTRITION_INFO", "calories: " + fullNutrients.getJSONObject(j).getInt("value"));
                                }
                                if( fullNutrients.getJSONObject(j).getInt("attr_id") == 203){
                                    protein = fullNutrients.getJSONObject(j).getInt("value");
                                    Log.i("NUTRITION_INFO", "protein: " + fullNutrients.getJSONObject(j).getInt("value"));
                                }
                                if( fullNutrients.getJSONObject(j).getInt("attr_id") == 204){
                                    fat = fullNutrients.getJSONObject(j).getInt("value");
                                    Log.i("NUTRITION_INFO", "fat: " + fullNutrients.getJSONObject(j).getInt("value"));
                                }
                                if( fullNutrients.getJSONObject(j).getInt("attr_id") == 269){
                                    sugars = fullNutrients.getJSONObject(j).getInt("value");
                                    Log.i("NUTRITION_INFO", "sugars: " + fullNutrients.getJSONObject(j).getInt("value"));
                                }
                                if( fullNutrients.getJSONObject(j).getInt("attr_id") == 205){
                                    carbs = fullNutrients.getJSONObject(j).getInt("value");
                                    Log.i("NUTRITION_INFO", "carbs: " + fullNutrients.getJSONObject(j).getInt("value"));
                                }
                                if( fullNutrients.getJSONObject(j).getInt("attr_id") == 291){
                                    fiber = fullNutrients.getJSONObject(j).getInt("value");
                                    Log.i("NUTRITION_INFO", "fiber: " + fullNutrients.getJSONObject(j).getInt("value"));
                                }
                            }
                            thePhoto = new Photo( nutritionInfoBranded.getJSONObject(i).getJSONObject("photo").getString("thumb"), null);
                            tempMeal = new Meal( nutritionInfoBranded.getJSONObject(i).getString("food_name"),
                                                    nutritionInfoBranded.getJSONObject(i).getJSONObject("photo").getString("thumb"),
                                                    thePhoto, nutritionInfoBranded.getJSONObject(i).getString("serving_unit"),
                                                    nutritionInfoBranded.getJSONObject(i).getString("nix_brand_id"),
                                                    nutritionInfoBranded.getJSONObject(i).getString("brand_name_item_name"),
                                                    nutritionInfoBranded.getJSONObject(i).getDouble("serving_qty"),
                                                    nutritionInfoBranded.getJSONObject(i).getInt("nf_calories"),
                                                    nutritionInfoBranded.getJSONObject(i).getString("brand_name") ,
                                                    nutritionInfoBranded.getJSONObject(i).getString("brand_type"),
                                                    nutritionInfoBranded.getJSONObject(i).getString("nix_item_id"),
                                                    null, null, null, null,
                                                    protein,
                                                    fat,
                                                    sugars, carbs, fiber);
                            nutritionList.add( tempMeal);

                        }



                        //Log.i("NUTRITION_INFO", nutritionInfoBranded.toString());


                        //Log.i("NUTRITION_INFO", "Food_name: " + testNutrition.getFood_name());
                        concatList.clear();
                        for( int newItem = 0; newItem < nutritionFilteredList.size(); newItem++){
                            concatList.add(nutritionFilteredList.get(newItem));
                            //Log.i( "NUTRITION_INFO", newItem + " " + nutritionFilteredList.get(newItem).getFood_name());
                        }

                        lv.setAdapter(arrayAdapter);
                        arrayAdapter.notifyDataSetChanged();
                        arrayAdapter.notifyDataSetChanged();




                    } catch (JSONException e) {
                        e.printStackTrace();
                        //Log.i("NUTRITION_INFO", "Failed: " + currentItem );
                    }

                }, error -> Log.i("Error", "That didn't work!"))
                ///HTTP Headers for v2
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("x-app-id", "31e0b725");
                params.put("x-app-key", "c23f20b92c7dc5cd841bf54f954d5118");
                return params;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    //Log.i("NUTRITION_INFO", requestBody);
                    return null;
                }
            }

        };
        queue.add(stringRequest);
        }
        catch(JSONException e){
            Log.i("NUTRITION_INFO", e.toString() );
        }
        //Log.i( "NUTRITION_INFO", "Nutrition Filtered List Size before return: " + nutritionList.size() );
        return nutritionList;

    }





}