package com.example.fitnessandnutritionbuddy;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.navigation.Navigation;
//import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.*;

import static java.lang.Thread.sleep;

import com.example.fitnessandnutritionbuddy.ui.login.UserLogin;
import com.example.fitnessandnutritionbuddy.ui.login.UserRegister;

import java.time.LocalDate;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    int time = 200; //milliseconds

    @Rule
    public ActivityScenarioRule<UserLogin> loginRule = new ActivityScenarioRule<>(UserLogin.class);

    //Needed before launching intent, runs before each test
    @Before
    public void setUp() throws Exception{
        Intents.init();
    }

    /*************************************************Tests********************************************/

    @Test
    public void LaunchRegisterActivityTest() {
        onView(withId(R.id.registerButton)).perform(click());
        intended(hasComponent(UserRegister.class.getName()));
    }

    @Test
    public void HomeFragmentTest() throws InterruptedException{
        onView(withId(R.id.loginButton)).perform(click());
        //Need to add random sleeps because the code sometimes runs before the activity finishes rendering
        sleep(time);
        LocalDate today = LocalDate.now();
        onView(withId(R.id.calendarButton)).check(matches(withText(today.toString())));
    }

    @Test
    public void ProfileFragmentTest() throws InterruptedException{
        onView(withId(R.id.loginButton)).perform(click());
        sleep(time);
        onView(withId(R.id.navigation_search)).perform(click());
        onView(withId(R.id.search_src_text)).perform(typeText("Pizza"));
        onView(withId(R.id.search_src_text)).check(matches(withText("Pizza")));
    }

    @Test
    public void SearchFragmentSearchingTest() throws InterruptedException{
        onView(withId(R.id.loginButton)).perform(click());
        sleep(time);
        onView(withId(R.id.navigation_profile)).perform(click());
        onView(withId(R.id.navigation_profile)).check(matches(isDisplayed()));
    }

    /*************************************************Tests********************************************/

    //Cleans intents, runs after every test
    @After
    public void tearDown() throws Exception{
        Intents.release();
    }



}