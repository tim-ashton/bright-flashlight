package com.timashton.brightflashlight;

import android.app.Fragment;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.BatteryManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

/*
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class MainTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mTestActivity;

    public MainTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Starts the activity under test using the default Intent with:
        // action = {@link Intent#ACTION_MAIN}
        // flags = {@link Intent#FLAG_ACTIVITY_NEW_TASK}
        // All other fields are null or empty.
        mTestActivity = getActivity();

        // Save the never show flag to shared preferences
        SharedPreferences settings = mTestActivity
                .getPreferences(MainActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(MainActivity.PREFS_REQUEST_RATING, true);

        // Commit the edits, use commit so they happen straight away
        editor.commit();
    }

    /**
     * Test if the test fixture has been set up correctly.
     */
    public void testPreconditions() {
        assertNotNull("mTestActivity is null", mTestActivity);
    }


    /*
    Test the static scaleDrawable method from the Helpers class.

    Test that the size of a drawable can be doubled and x10
     */
    @SuppressWarnings("deprecation")
    public void testScaleDrawable() {

        // ensure that the activity is gone
        mTestActivity.finish();

        // get a fresh activity
        mTestActivity = getActivity();

        LevelListDrawable currentIcon = null;
        Drawable result = null;
        int level;
        int batteryIconId;

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = mTestActivity.registerReceiver(null, intentFilter);

        // Check batteryStatus is available
        assertNotNull("batteryStatus Intent is null. Unable to continue this test.", batteryStatus);


        level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        batteryIconId = batteryStatus.getIntExtra(BatteryManager.EXTRA_ICON_SMALL, 0);

        currentIcon = (LevelListDrawable) mTestActivity.getResources().getDrawable(batteryIconId);

        // Check that currentIcon is available
        assertNotNull("currentIcon LevelListDrawableis null. Unable to continue this test."
                , currentIcon);


        float currentHeight = currentIcon.getIntrinsicHeight();
        float currentWidth = currentIcon.getIntrinsicWidth();

        float doubleSize = 2.0f;
        Drawable doubledDrawable = Helpers.scaleDrawable(currentIcon, doubleSize, mTestActivity);


        assertEquals(currentHeight * doubleSize, (float) doubledDrawable.getIntrinsicHeight());
        assertEquals(currentWidth * doubleSize, (float) doubledDrawable.getIntrinsicWidth());

        float tenTimes = 10.0f;
        Drawable tenTimesDrawable = Helpers.scaleDrawable(currentIcon, tenTimes, mTestActivity);

        assertEquals(currentHeight * tenTimes, (float) tenTimesDrawable.getIntrinsicHeight());
        assertEquals(currentWidth * tenTimes, (float) tenTimesDrawable.getIntrinsicWidth());

    }

    @UiThreadTest
    public void testRatingDialogIsShown(){

        // ensure that the activity is gone
        mTestActivity.finish();

        // get a fresh activity
        mTestActivity = getActivity();

        // Get a reference to existing fragment
        // This fragment should never exist on resume
        Fragment existingFragment =
                mTestActivity
                        .getFragmentManager()
                        .findFragmentByTag(RatingFragment.TAG);

        assertNotNull("The ratings fragment is null", existingFragment);
    }

    @UiThreadTest
    public void testNeverShowAgainButton(){

        // ensure that the activity is gone
        mTestActivity.finish();

        // Get a fresh one ..
        mTestActivity = getActivity();


        // Get a reference to existing fragment
        // This fragment should never exist on resume
        RatingFragment ratingFragment =
                (RatingFragment)mTestActivity
                        .getFragmentManager()
                        .findFragmentByTag(RatingFragment.TAG);

        // Can't dismiss a null fragment..
        assertNotNull("The ratings fragment is null", ratingFragment);

        // Call the onclick on the rate later button
        ratingFragment.onClick(mTestActivity.findViewById(R.id.never_show_again_button));

        SharedPreferences settings = mTestActivity.getPreferences(MainActivity.MODE_PRIVATE);

        assertFalse("Shared prefs RequestRating is true, should be false",
                settings.getBoolean(mTestActivity.PREFS_REQUEST_RATING, true));


        // Reset the preferences
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(MainActivity.PREFS_REQUEST_RATING, true);

        // Commit the edits!
        editor.commit();

    }

}