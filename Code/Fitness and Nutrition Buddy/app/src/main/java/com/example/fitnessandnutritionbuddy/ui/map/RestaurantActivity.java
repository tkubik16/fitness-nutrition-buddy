package com.example.fitnessandnutritionbuddy.ui.map;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.fitnessandnutritionbuddy.MainActivity;
import com.example.fitnessandnutritionbuddy.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RestaurantActivity extends AppCompatActivity implements OnMapReadyCallback {

    public String[] coordinates;
    public String[] extras = new String[3];
    public ActivityResultLauncher<Intent> launchSomeActivity;
    public static JSONArray restaurantList;
    private MapView mapview;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    public GoogleMap myMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        getSupportActionBar().setTitle("Nearby Restaurants");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            coordinates = extras.getStringArray("location");
        }

        launchSomeActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //....result
                    }
                });

        sendAPIRequest("1000m");

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapview = findViewById(R.id.mapView);
        mapview.onCreate(mapViewBundle);
        mapview.getMapAsync(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.restaurant_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Filter Distance (ex. 100m)");

        int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
        ImageView v = searchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.ic_baseline_filter_alt_24);

        final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                sendAPIRequest(query);
                return false;
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        launchSomeActivity.launch(myIntent);
        return true;

    }

    /////////////////////////////////////This code requests from NutritionIX API to list nearby restaurants
    public void sendAPIRequest(String distance) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://trackapi.nutritionix.com/v2/locations?ll=" + coordinates[0] + "%2C" + coordinates[1] + "&distance=" + distance + "&limit=50";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // Display the first 500 characters of the response string.
                    try {
                        JSONObject jObject = new JSONObject(response);
                        restaurantList = jObject.getJSONArray("locations");
                        ArrayList<Restaurant> arrayOfUsers = new ArrayList<>();
                        arrayOfUsers.clear();

                        for (int i = 0; i < restaurantList.length(); i++) {
                            DecimalFormat f = new DecimalFormat("0.00");
                            arrayOfUsers.add(new Restaurant(restaurantList.getJSONObject(i).getString("name"),
                                    f.format(restaurantList.getJSONObject(i).getDouble("distance_km") / 1.609344) + " mi",
                                    restaurantList.getJSONObject(i).getString("brand_id")));
                        }

                        if(myMap != null){
                            myMap.clear();
                            onMapReady(myMap);
                        }

                        //Populates listview via adapter
                        final ListView simpleList = this.findViewById(R.id.list_view);
                        ListAdapter customAdapter = new ListAdapter(this, arrayOfUsers);
                        customAdapter.notifyDataSetChanged();
                        simpleList.setAdapter(customAdapter);
                        customAdapter.notifyDataSetChanged();

                        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                extras[0] = coordinates[0];
                                extras[1] = coordinates[1];
                                extras[2] = arrayOfUsers.get(position).name;

                                // Create new fragment and transaction
                                Intent i = new Intent(view.getContext(), RestaurantMenu.class);
                                i.putExtra("key", extras);
                                startActivity(i);

                            }

                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, error -> Log.i("Error", "That didn't work!"))
                ///HTTP Headers for v2
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("x-app-id", "31e0b725");
                params.put("x-app-key", "c23f20b92c7dc5cd841bf54f954d5118");
                return params;
            }
        };
        queue.add(stringRequest);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapview.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapview.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapview.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapview.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        myMap = map;
        if(restaurantList != null){
            for(int i = 0; i < restaurantList.length(); i++){
                try {
                    myMap.addMarker(new MarkerOptions().position(new LatLng(restaurantList.getJSONObject(i).getDouble("lat"), restaurantList.getJSONObject(i).getDouble("lng"))).title(restaurantList.getJSONObject(i).getString("name")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        CameraUpdate point = CameraUpdateFactory.newLatLng(new LatLng(Double.valueOf(coordinates[0]), Double.valueOf(coordinates[1])));
        myMap.setMyLocationEnabled(true);
        myMap.setMinZoomPreference(14);
        myMap.moveCamera(point);
        UiSettings uiSettings = myMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
    }

    @Override
    public void onPause(){
        mapview.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy(){
        mapview.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();
        mapview.onLowMemory();
    }


}