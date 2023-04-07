package com.example.fitnessandnutritionbuddy.ui.search;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.fitnessandnutritionbuddy.R;
import com.example.fitnessandnutritionbuddy.databinding.FragmentSearchBinding;
import com.example.fitnessandnutritionbuddy.ui.login.UserLogin;
import com.example.fitnessandnutritionbuddy.ui.profile.User;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class SearchFragment extends Fragment {

    private ListView listView;
    DataContainer outerDC;
    MealAdapter arrayAdapter;
    ExerciseAdapter exerciseAdapter;
    ArrayList<Meal> concatList = new ArrayList<>();
    ArrayList<Exercise> exList = new ArrayList<>();
    private SearchViewModel SearchViewModel;
    private FragmentSearchBinding binding;
    private SearchView searchView;
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static boolean foodSelected = true;
    public static JSONArray exerciseList;

    //varibales needed for sendMealAPIRequest( String query)
    ArrayList<Meal> nutritionList = new ArrayList<Meal>();
    public static JSONArray nutritionInfoBranded;
    public static JSONArray nutritionInfoCommon;
    Meal tempMeal;
    Photo thePhoto;

    DemoCollectionAdapter demoCollectionAdapter;
    ViewPager2 viewPager;
    TabLayout tabLayout;
    public static String searchType;

    ArrayList<String> breakfastSuggestions = new ArrayList<>(Arrays.asList("Eggs", "Bacon", "Pancakes", "Waffles", "Cereal"));
    ArrayList<String> lunchSuggestions = new ArrayList<>(Arrays.asList("Sandwich", "Pizza", "Chicken", "Burger", "Pasta"));
    ArrayList<String> dinnerSuggestions = new ArrayList<>(Arrays.asList("Soup", "Grilled Cheese", "Pizza", "Macaroni and Cheese", "Beans"));
    ArrayList<String> snackSuggestions = new ArrayList<>(Arrays.asList("Apple", "Salad", "Chips", "Nutrition Bar", "Cookies"));
    ArrayList<String> cardioSuggestions = new ArrayList<>(Arrays.asList("Running for 30 min ", "Swimming for 30 min", "Biking for 30 Min", "Soccer for 30 Min", "Dancing for 30 Min"));
    ArrayList<String> weightLiftingSuggestions = new ArrayList<>(Arrays.asList("Weightlifting for 30 min", "Weightlifting for 60 min", "Weightlifting for 90 min ", "Weightlifting for 120 min"));
    ArrayList<String> yogaSuggestions = new ArrayList<>(Arrays.asList("Yoga for 15 min", "Yoga for 30 min ", "Yoga for 45 min", "Yoga for 60 min"));

    ArrayAdapter breakfastAdapter;
    ArrayAdapter lunchAdapter;
    ArrayAdapter dinnerAdapter;
    ArrayAdapter snackAdapter;
    ArrayAdapter cardioAdapter;
    ArrayAdapter weightLiftingAdapter;
    ArrayAdapter yogaAdapter;

    public static LocalDate localDateFromMealLog = null;
    public static String searchFromMealLog = null;

    public class DemoCollectionAdapter extends FragmentStateAdapter {
        public DemoCollectionAdapter(Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Return a NEW fragment instance in createFragment(int)
            Fragment fragment;
            if (position == 0)
                fragment = new MealsTabFragment();
            else
                fragment = new WorkoutsTabFragment();
            return fragment;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals("Meals")) {
                    foodSelected = true;
                    searchView.setQueryHint("Search for food item");
                } else {
                    foodSelected = false;
                    searchView.setQueryHint("Search for workout");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0)
                tab.setText("Meals");
            else
                tab.setText("Workouts");
        }).attach();
    }

    @SuppressLint("RestrictedApi")
    public void updateSearchHint(String hint) {
        searchView.requestFocus();
        searchView.setQueryHint("Search for " + hint);
        searchView.setQuery("", false);
        SearchView.SearchAutoComplete mSearchSrcTextView = searchView.findViewById(R.id.search_src_text);

        if (hint.equals("breakfast"))
            mSearchSrcTextView.setAdapter(breakfastAdapter);
        else if (hint.equals("lunch"))
            mSearchSrcTextView.setAdapter(lunchAdapter);
        else if (hint.equals("dinner"))
            mSearchSrcTextView.setAdapter(dinnerAdapter);
        else if (hint.equals("snack"))
            mSearchSrcTextView.setAdapter(snackAdapter);
        else if (hint.equals("cardio"))
            mSearchSrcTextView.setAdapter(cardioAdapter);
        else if (hint.equals("weightlifting"))
            mSearchSrcTextView.setAdapter(weightLiftingAdapter);
        else
            mSearchSrcTextView.setAdapter(yogaAdapter);

        mSearchSrcTextView.setOnItemClickListener((adapterView, view, position, id) -> {
            String queryString = (String) adapterView.getItemAtPosition(position);
            searchView.setQuery(queryString, true);
        });

        mSearchSrcTextView.setThreshold(0);
        mSearchSrcTextView.performClick();

    }

    @SuppressLint("RestrictedApi")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        breakfastAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, breakfastSuggestions);
        lunchAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, lunchSuggestions);
        dinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dinnerSuggestions);
        snackAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, snackSuggestions);
        cardioAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, cardioSuggestions);
        weightLiftingAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, weightLiftingSuggestions);
        yogaAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, yogaSuggestions);

        SearchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        demoCollectionAdapter = new DemoCollectionAdapter(this);
        viewPager = root.findViewById(R.id.pager);
        viewPager.setAdapter(demoCollectionAdapter);
        if(!foodSelected)
            viewPager.setCurrentItem(1);

        SearchViewModel.getText().observe(getViewLifecycleOwner(), s -> {
        });

        searchView = root.findViewById(R.id.home_search_bar);
        searchView.requestFocus();
        searchView.setQueryHint("Search for food item");
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tabLayout.setVisibility(View.GONE);
                viewPager.setVisibility(View.GONE);
                if (foodSelected)
                    doMySearch(query);
                else
                    sendAPIRequest(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                return false;
            }
        });

        SearchView.SearchAutoComplete mSearchSrcTextView = searchView.findViewById(R.id.search_src_text);
        mSearchSrcTextView.setDropDownWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mSearchSrcTextView.setDropDownBackgroundResource(R.color.white);

        if(searchFromMealLog != null) {
            updateSearchHint(searchFromMealLog);
        }

        ImageView clearButton = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        clearButton.setOnClickListener(v -> {
            tabLayout.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.VISIBLE);
            concatList.clear();
            exList.clear();
            searchView.setQuery("", false);
            mSearchSrcTextView.setThreshold(1);
            if (foodSelected)
                searchView.setQueryHint("Search for food item");
            else
                searchView.setQueryHint("Search for workout");
            clearButton.setVisibility(View.GONE);
        });

        listView = (ListView) root.findViewById(R.id.queryDisplayListView);
        arrayAdapter = new MealAdapter(getContext(), android.R.layout.simple_list_item_1, concatList);

        // argument position gives the index of item which is clicked
        listView.setOnItemClickListener((arg0, v, position, arg3) -> {

            //Layout stuff
            LayoutInflater inflater1 = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            final View popupView = inflater1.inflate(R.layout.popup_window, null);
            final PopupWindow mPopupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, 1300);
            mPopupWindow.setElevation(5.0f);

            //View Declarations
            ImageButton exitButton = popupView.findViewById(R.id.popup_exit_button);
            TextView foodName = popupView.findViewById(R.id.popup_food_name);
            TextView calories = popupView.findViewById(R.id.popup_food_cal);
            TextView brandName = popupView.findViewById(R.id.popup_brand_name);
            TextView servingText = popupView.findViewById(R.id.popup_food_serving);
            ImageView foodImage = popupView.findViewById(R.id.popup_food_image);
            Button addFood = popupView.findViewById(R.id.popup_add_food_button);
            Spinner mealChooser = popupView.findViewById(R.id.popup_meal_type_spinner);
            ArrayAdapter<CharSequence> mealAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.meal_type_array,
                    android.R.layout.simple_spinner_dropdown_item);
            mealChooser.setAdapter(mealAdapter);


            if (foodSelected) {
                final Meal selectedMeal = concatList.get(position);

                mealChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedMeal.mealType = adapterView.getItemAtPosition(i).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });

                selectSpinnerValue(mealChooser, searchType);

                //Set texts
                foodName.setText(selectedMeal.getFood_name().substring(0, 1).toUpperCase() + selectedMeal.getFood_name().substring(1));
                calories.setText(Double.toString(selectedMeal.getNf_calories()) + " cal");
                if (selectedMeal.getBrand_name() != null) {
                    brandName.setText(selectedMeal.getBrand_name());
                } else {
                    brandName.setText(R.string.common);
                }
                servingText.setText(Double.toString(selectedMeal.getServing_qty()) + " " + selectedMeal.getServing_unit());
                Picasso.with(getContext()).load(selectedMeal.getImage()).into(foodImage);
                mPopupWindow.setOutsideTouchable(false);
                mPopupWindow.setFocusable(true);
                mPopupWindow.showAtLocation(root.findViewById(R.id.search_activity_layout), Gravity.CENTER, 0, 0);
                addFood.setOnClickListener(v12 -> {
                    Toast.makeText(getContext(), "Food added!", Toast.LENGTH_SHORT).show();
                    mPopupWindow.dismiss();
                    DocumentReference ref = db.collection("users").document(User.getUsername());
                    if(localDateFromMealLog == null)
                        selectedMeal.time = Calendar.getInstance().getTime();
                    else {
                        selectedMeal.time = Date.from(localDateFromMealLog.atStartOfDay(ZoneId.systemDefault()).toInstant());
                        localDateFromMealLog=null;
                    }
                    ref.update("meals", FieldValue.arrayUnion(selectedMeal));
                    UserLogin.mealArrayList.add(selectedMeal);
                    UserLogin.sortedMealArrayList = (ArrayList<Meal>) UserLogin.mealArrayList.clone();
                    UserLogin.sortedMealArrayList.sort(new Comparator<Meal>() {
                        @Override
                        public int compare(Meal meal1, Meal meal2) {
                            if (meal1.time == null || meal2.time == null)
                                return 0;
                            return meal2.time.compareTo(meal1.time);
                        }
                    });
                    clearDim((ViewGroup) root);
                });
            } else {

                mealAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.exercise_type_array,
                        android.R.layout.simple_spinner_dropdown_item);
                mealChooser.setAdapter(mealAdapter);

                final Exercise selectedExercise = exList.get(position);

                mealChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedExercise.exType = adapterView.getItemAtPosition(i).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });

                selectSpinnerValue(mealChooser, searchType);

                //Set texts
                servingText.setText("");
                foodName.setText(selectedExercise.name.substring(0, 1).toUpperCase() + selectedExercise.name.substring(1));
                calories.setText(selectedExercise.nf_calories + " cal");
                brandName.setText(selectedExercise.duration_min + " min");
                Picasso.with(getContext()).load(selectedExercise.getImage()).into(foodImage);
                mPopupWindow.setOutsideTouchable(false);
                mPopupWindow.setFocusable(true);
                addFood.setText("Add exercise");
                mPopupWindow.showAtLocation(root.findViewById(R.id.search_activity_layout), Gravity.CENTER, 0, 0);
                addFood.setOnClickListener(v1 -> {
                    Toast.makeText(getContext(), "Exercise added!", Toast.LENGTH_SHORT).show();
                    mPopupWindow.dismiss();
                    DocumentReference ref = db.collection("users").document(User.getUsername());
//                    selectedExercise.time = Calendar.getInstance().getTime();
                    if(localDateFromMealLog == null)
                        selectedExercise.time = Calendar.getInstance().getTime();
                    else {
                        selectedExercise.time = Date.from(localDateFromMealLog.atStartOfDay(ZoneId.systemDefault()).toInstant());
                        localDateFromMealLog = null;
                    }
                    ref.update("exercises", FieldValue.arrayUnion(selectedExercise));
                    UserLogin.exerciseArrayList.add(selectedExercise);
                    UserLogin.sortedExerciseArrayList = (ArrayList<Exercise>) UserLogin.exerciseArrayList.clone();
                    UserLogin.sortedExerciseArrayList.sort(new Comparator<Exercise>() {
                        @Override
                        public int compare(Exercise ex1, Exercise ex2) {
                            if (ex1.time == null || ex2.time == null)
                                return 0;
                            return ex2.time.compareTo(ex1.time);
                        }
                    });
                    clearDim((ViewGroup) root);
                });
            }
            exitButton.setOnClickListener(view -> {
                mPopupWindow.dismiss();
                clearDim((ViewGroup) root);
            });
            applyDim((ViewGroup) root, 0.5f);
        });
        handleIntent(getActivity().getIntent());
        // Get the intent, verify the action and get the query
        return root;
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


    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            System.out.println(query);
            System.out.print("searching for");
            doMySearch(query);
        }
    }

    //private void doMySearch(String query) { new HttpAsyncTask().execute(query); }
    // this search calls sendMealAPIRequest which does the POST version of the GET the above doMySearch would do
    private void doMySearch(String query) {
        sendMealAPIRequest( query);
    }

    private void updateList(DataContainer result) {
        concatList.clear();
        if (result != null && result.getBranded() != null) {
            concatList.addAll(result.getBranded());
            concatList.addAll(result.getCommon());
        } else {
            Toast.makeText(getActivity(), "No results found!", Toast.LENGTH_SHORT).show();
        }
        listView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        outerDC = result;
        arrayAdapter.notifyDataSetChanged();
    }

    private DataContainer GET(String query) {

        DataContainer dataContainer;
        try {
            URL myUrl = new URL("https://trackapi.nutritionix.com/v2/search/instant?query=" + query);
            // create HttpClient
            HttpURLConnection http = (HttpURLConnection) myUrl.openConnection();

            http.setRequestMethod("GET");
            http.setRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("x-app-id", "31e0b725");
            http.setRequestProperty("x-app-key", "c23f20b92c7dc5cd841bf54f954d5118");

            BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));

            Gson gson = new Gson();
            dataContainer = gson.fromJson(in, DataContainer.class);

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
            dataContainer = null;
        }
        return dataContainer;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, DataContainer> {
        @Override
        protected DataContainer doInBackground(String... query) {
            return GET(query[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(DataContainer result) {
            updateList(result);
        }
    }

    /////////////////////////////////////This code requests from NutritionIX API to list nearby restaurants
    public void sendAPIRequest(String query) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://trackapi.nutritionix.com/v2/natural/exercise";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Display the first 500 characters of the response string.
                    try {
                        JSONObject jObject = new JSONObject(response);
                        exList = new ArrayList<>();
                        exerciseList = jObject.getJSONArray("exercises");
                        for (int i = 0; i < exerciseList.length(); i++) {
                            exList.add(new Exercise(
                                    exerciseList.getJSONObject(i).getString("name"),
                                    exerciseList.getJSONObject(i).getDouble("nf_calories"),
                                    new Photo(exerciseList.getJSONObject(i).getJSONObject("photo").getString("thumb"), exerciseList.getJSONObject(i).getJSONObject("photo").getString("highres")),
                                    exerciseList.getJSONObject(i).getString("tag_id"),
                                    exerciseList.getJSONObject(i).getDouble("duration_min"),
                                    Calendar.getInstance().getTime()
                            ));
                            Log.i("name", exerciseList.getJSONObject(i).getString("name"));
                            Log.i("calories", exerciseList.getJSONObject(i).getString("nf_calories"));
                            Log.i("photo", exerciseList.getJSONObject(i).getJSONObject("photo").getString("thumb"));
                            Log.i("id", exerciseList.getJSONObject(i).getString("tag_id"));
                            Log.i("duration", exerciseList.getJSONObject(i).getString("duration_min"));
                        }

                        exerciseAdapter = new ExerciseAdapter(getContext(), android.R.layout.simple_list_item_1, exList);
                        listView.setAdapter(exerciseAdapter);
                        exerciseAdapter.notifyDataSetChanged();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.i("Error", "That didn't work!"))
                ///HTTP Headers for v2
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("x-app-id", "31e0b725");
                params.put("x-app-key", "c23f20b92c7dc5cd841bf54f954d5118");
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("query", query);
                params.put("gender", User.gender);
                params.put("weight_kg", String.valueOf(User.weight * 0.45));
                params.put("height_cm", String.valueOf(User.height * 2.54));
                params.put("age", String.valueOf(User.age));

                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void selectSpinnerValue(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(myString)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    public ArrayList<Meal> sendMealAPIRequest( String query) {
        ArrayList<Meal> nutritionList = new ArrayList<Meal>();
        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String url = "https://trackapi.nutritionix.com/v2/search/instant";
        JSONObject jsonBody = new JSONObject();


        try {
            jsonBody.put("query", query);
            //jsonBody.put("detailed", "true");
            jsonBody.put("branded", "true");
            jsonBody.put("branded_type", "1");
            jsonBody.put("common", "true");
            jsonBody.put("detailed", "true");


            final String requestBody = jsonBody.toString();
            Log.i("NUTRITION_INFO",requestBody);


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

                            Nutrients nutrients = new Nutrients();

                            for (int i = 0; i < nutritionInfoBranded.length(); i++) {
                                fullNutrients = nutritionInfoBranded.getJSONObject(i).getJSONArray("full_nutrients");
                                nutrients.setAllNutrients( fullNutrients);
                                /*
                                for (int j = 0; j < fullNutrients.length(); j++) {

                                    if( fullNutrients.getJSONObject(j).getInt("attr_id") == 208){
                                        calories = fullNutrients.getJSONObject(j).getInt("value");
                                    }
                                    if( fullNutrients.getJSONObject(j).getInt("attr_id") == 203){
                                        protein = fullNutrients.getJSONObject(j).getInt("value");
                                    }
                                    if( fullNutrients.getJSONObject(j).getInt("attr_id") == 204){
                                        fat = fullNutrients.getJSONObject(j).getInt("value");
                                    }
                                    if( fullNutrients.getJSONObject(j).getInt("attr_id") == 269){
                                        sugars = fullNutrients.getJSONObject(j).getInt("value");
                                    }
                                    if( fullNutrients.getJSONObject(j).getInt("attr_id") == 205){
                                        carbs = fullNutrients.getJSONObject(j).getInt("value");
                                    }
                                    if( fullNutrients.getJSONObject(j).getInt("attr_id") == 291){
                                        fiber = fullNutrients.getJSONObject(j).getInt("value");
                                    }
                                }

                                 */
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
                                        nutrients.protein,
                                        nutrients.fat,
                                        nutrients.sugars, nutrients.carbs, nutrients.fiber);
                                nutritionList.add( tempMeal);
                                //Log.i( "NUTRITION_INFO", "Nutrition Filtered List Size after each add: " + nutritionList.size() );
                                //Log.i( "NUTRITION_INFO", i + " " + nutritionInfoBranded.getJSONObject(i).getString("food_name") + " " + nutritionInfoBranded.getJSONObject(i).getString("nf_calories"));
                                //Log.i( "NUTRITION_INFO", i + " " + nutritionList.get(i).getFood_name());
                            }

                            for (int i = 0; i < nutritionInfoCommon.length(); i++) {
                                fullNutrients = nutritionInfoCommon.getJSONObject(i).getJSONArray("full_nutrients");
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
                                thePhoto = new Photo( nutritionInfoCommon.getJSONObject(i).getJSONObject("photo").getString("thumb"), null);
                                tempMeal = new Meal( nutritionInfoCommon.getJSONObject(i).getString("food_name"),
                                        nutritionInfoCommon.getJSONObject(i).getJSONObject("photo").getString("thumb"),
                                        thePhoto, nutritionInfoCommon.getJSONObject(i).getString("serving_unit"),
                                        null,
                                        null,
                                        nutritionInfoCommon.getJSONObject(i).getDouble("serving_qty"),
                                        calories,
                                        null ,
                                        null,
                                        null,
                                        null, null, null, null,
                                        protein,
                                        fat,
                                        sugars, carbs, fiber);
                                nutritionList.add( tempMeal);
                                //Log.i( "NUTRITION_INFO", (i + nutritionInfoBranded.length()) + " " + nutritionInfoCommon.getJSONObject(i).getString("food_name"));
                            }

                            //JSONObject branded = nutritionInfo.getJSONObject();
                            //Log.i("NUTRITION_INFO", nutritionInfoBranded.toString());
                            //NutritionInfo testNutrition = new NutritionInfo( nutritionInfo);

                            //Log.i("NUTRITION_INFO", "Food_name: " + testNutrition.getFood_name());
                            concatList.clear();
                            for( int newItem = 0; newItem < nutritionList.size(); newItem++){
                                concatList.add(nutritionList.get(newItem));
                                //Log.i( "NUTRITION_INFO", newItem + " " + nutritionFilteredList.get(newItem).getFood_name());
                            }

                            listView.setAdapter(arrayAdapter);
                            arrayAdapter.notifyDataSetChanged();
                            arrayAdapter.notifyDataSetChanged();

                            // this is how you get what user is currently logged in
                            Log.i("NUTRITION_INFO", "User name: " + User.getUsername() );



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
