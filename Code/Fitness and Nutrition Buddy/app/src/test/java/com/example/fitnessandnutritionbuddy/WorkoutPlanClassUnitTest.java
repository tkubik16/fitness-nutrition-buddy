package com.example.fitnessandnutritionbuddy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.fitnessandnutritionbuddy.ui.planning.MealPlan;
import com.example.fitnessandnutritionbuddy.ui.planning.WorkoutPlan;

import org.junit.Before;
import org.junit.Test;

public class WorkoutPlanClassUnitTest {
    public WorkoutPlan workout;

    @Before
    public void setup(){
        workout = new WorkoutPlan("yoga plan",
                "test",
                1,
                true,
                2,
                3,
                4,
                5
        );

    }

    @Test
    public void titleIsCorrect() {
        assertEquals("yoga plan", workout.title);
    }

    @Test
    public void descriptionIsCorrect() {
        assertEquals("test", workout.description);
    }

    @Test
    public void selectedIsCorrect() {
        assertTrue(workout.selected);
    }

    @Test
    public void photoIsCorrect() {
        assertEquals(1, workout.photo);
    }

    @Test
    public void cardioIsCorrect() {
        assertEquals(2, workout.cardioMin);
    }

    @Test
    public void strengthIsCorrect() {
        assertEquals(3, workout.strengthMin);
    }

    @Test
    public void yogaIsCorrect() {
        assertEquals(4, workout.yogaMin);
    }

    @Test
    public void calorieIsCorrect() {
        assertEquals(5, workout.calorieMin);
    }

}
