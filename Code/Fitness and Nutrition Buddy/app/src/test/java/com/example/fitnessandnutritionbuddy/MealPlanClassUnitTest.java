package com.example.fitnessandnutritionbuddy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.example.fitnessandnutritionbuddy.ui.planning.MealPlan;

import org.junit.Before;
import org.junit.Test;

public class MealPlanClassUnitTest {
    public MealPlan meal;

    @Before
    public void setup(){
        meal = new MealPlan("carb plan",
                "test",
                1,
                false,
                2,
                3,
                4,
                5,
                6,
                7
        );

    }

    @Test
    public void titleIsCorrect() {
        assertEquals("carb plan", meal.title);
    }

    @Test
    public void descriptionIsCorrect() {
        assertEquals("test", meal.description);
    }

    @Test
    public void selectedIsCorrect() {
        assertFalse(meal.selected);
    }

    @Test
    public void carbIsCorrect() {
        assertEquals(2, meal.carbMin);
    }

    @Test
    public void proteinIsCorrect() {
        assertEquals(3, meal.proteinMin);
    }

    @Test
    public void fiberIsCorrect() {
        assertEquals(4, meal.fiberMin);
    }

    @Test
    public void fatIsCorrect() {
        assertEquals(5, meal.fatLimit);
    }

    @Test
    public void sugarIsCorrect() {
        assertEquals(6, meal.sugarLimit);
    }

    @Test
    public void calorieIsCorrect() {
        assertEquals(7, meal.calorieLimit);
    }



}
