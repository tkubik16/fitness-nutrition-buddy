package com.example.fitnessandnutritionbuddy;

import static org.junit.Assert.assertEquals;

import com.example.fitnessandnutritionbuddy.ui.planning.MealPlan;
import com.example.fitnessandnutritionbuddy.ui.search.Nutrients;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

public class NutrientsClassUnitTest {
    Nutrients nutrients;
    JSONArray nutrientArray;
    JSONObject calories;
    JSONObject protein;
    JSONObject fat;
    JSONObject sugars;
    JSONObject carbs;
    JSONObject fiber;

    @Before
    public void setup(){
        nutrients = new Nutrients();

        nutrientArray = new JSONArray();
        calories = new JSONObject();
        protein = new JSONObject();
        fat = new JSONObject();
        sugars = new JSONObject();
        carbs = new JSONObject();
        fiber = new JSONObject();

        try {
            calories.put("value","10");
            calories.put("attr_id","208");

            protein.put("value","20");
            protein.put("attr_id","203");

            fat.put("value","30");
            fat.put("attr_id","204");

            sugars.put("value","40");
            sugars.put("attr_id","269");

            carbs.put("value","50");
            carbs.put("attr_id","205");

            fiber.put("value","60");
            fiber.put("attr_id","291");

            nutrientArray.put( calories);
            nutrientArray.put( protein);
            nutrientArray.put( fat);
            nutrientArray.put( sugars);
            nutrientArray.put( carbs);
            nutrientArray.put( fiber);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    @Test
    public void nutrientsSetCorrectly() {

        nutrients.setAllNutrients( nutrientArray);

        assertEquals( 10, nutrients.calories);
        assertEquals( 20, nutrients.protein);
        assertEquals( 30, nutrients.fat);
        assertEquals( 40, nutrients.sugars);
        assertEquals( 50, nutrients.carbs);
        assertEquals( 60, nutrients.fiber);

    }
}
