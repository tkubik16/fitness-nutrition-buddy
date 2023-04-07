package com.example.fitnessandnutritionbuddy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.provider.ContactsContract;
import android.util.Log;
import android.util.Pair;

import com.example.fitnessandnutritionbuddy.ui.profile.ProfileFragment;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

public class ProfileFragmentUnitTest {

    public Calendar c;
    public Pair <Calendar, Calendar> range;

    @Before
    public void setup(){
        c = Calendar.getInstance();
        c.setTime(Calendar.getInstance().getTime());
    }

    @Test
    public void matchingDateIsTrue() {
        assertTrue(ProfileFragment.matchesDate(c,c));
    }


}
