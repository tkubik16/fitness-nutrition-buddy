package com.example.fitnessandnutritionbuddy;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import android.content.Intent;
import android.widget.Toast;

import com.example.fitnessandnutritionbuddy.ui.profile.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UserClassUnitTest {
    public User user1;

    @Before
    public void setup(){
        user1 = new User("user1","password");
        user1.age = 25;
        user1.height = 123;
        user1.weight = 130;
        user1.calories_lte = 10;
        user1.calories_gte = 12;
        user1.protein_lte = 7;
        user1.protein_gte = 8;
    }

    @Test
    public void user_usernameIsCorrect() {
        assertEquals("user1", user1.getUsername());
    }

    @Test
    public void user_passwordIsCorrect() {
        assertEquals("password", user1.password);
    }

    @Test
    public void user_ageIsCorrect() {
        assertEquals(25, user1.age);
    }

    @Test
    public void user_heightIsCorrect() {
        assertEquals(123, user1.height);
    }

    @Test
    public void user_weightIsCorrect() {
        assertEquals(130, user1.weight);
    }

    @Test
    public void user_calories_lteIsCorrect() {
        assertEquals(10, user1.calories_lte);
    }

    @Test
    public void user_calories_gteIsCorrect() {
        assertEquals(12, user1.calories_gte);
    }

    @Test
    public void user_protein_lteIsCorrect() {
        assertEquals(7, user1.protein_lte);
    }

    @Test
    public void user_protein_gteIsCorrect() {
        assertEquals(8, user1.protein_gte);
    }

}