package com.timashton.bright_flashlight;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.hardware.Camera;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.IOException;


public class MainActivity extends Activity {

    public static final String TAG = MainActivity.class.getName();
    public static final String PREFS_REQUEST_RATING = "show_dialog_boolean";
    private static final int BATTERY_MINIMUM = 10;
    private static float ICON_SCALE_FACTOR = 1.7f;

    @SuppressWarnings("deprecation")
    private Camera mCamera;
    private boolean mBatteryLow = false;
    private int mBatteryPercent = 100;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);

        MenuItem item = menu.findItem(R.id.action_bar_battery_percent);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.action_bar_battery_percent);
        TextView actionBarTV = (TextView) item.getActionView().findViewById(R.id.action_button_battery_status);

        // Set the text to the current percent
        actionBarTV.setText(mBatteryPercent + "%");

        // Get the current system battery drawable
        Drawable currentIcon = getScaledCurrentBatteryIcon(ICON_SCALE_FACTOR);

        PorterDuffColorFilter colorFilter;
        if (mBatteryPercent > 39) {
            colorFilter = new PorterDuffColorFilter(
                    getResources().getColor(R.color.battery_green), PorterDuff.Mode.SRC_ATOP);
        } else if (mBatteryPercent > 14) {
            colorFilter = new PorterDuffColorFilter(
                    getResources().getColor(R.color.battery_yellow), PorterDuff.Mode.SRC_ATOP);
        } else {
            colorFilter = new PorterDuffColorFilter(
                    getResources().getColor(R.color.battery_red), PorterDuff.Mode.SRC_ATOP);
        }


        if (currentIcon != null) {

            currentIcon.setColorFilter(colorFilter);

            actionBarTV.setCompoundDrawablesWithIntrinsicBounds(currentIcon, null, null, null);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Dim the screen. The user is probably in a dark place
        WindowManager.LayoutParams windowParams = this.getWindow().getAttributes();
        windowParams.screenBrightness = 0.0f;
        this.getWindow().setAttributes(windowParams);


        // Get access to the fragment manager
        FragmentManager fm = getFragmentManager();

        // Battery level below 10 %
        if ((mBatteryPercent = getBatteryPercent()) < BATTERY_MINIMUM) {
            mBatteryLow = true;
        }


        if (mBatteryLow) {

            // Remove the spacer view to put the low battery fragment
            // in the middle of the screen
            View spacerView = (View) findViewById(R.id.activity_main_spacer_view);
            spacerView.setVisibility(View.GONE);

            AdView adView = (AdView) findViewById(R.id.adView);
            adView.setVisibility(View.GONE);

            fm.beginTransaction()
                    .add(R.id.activity_main
                            , LowBatteryFragment.newInstance()
                            , LowBatteryFragment.TAG)
                    .commit();
        } else {

            // register the broadcast receiver if the flashlight actually starts.
            registerReceiver(new BatteryLevelReceiver(), new IntentFilter(
                    Intent.ACTION_BATTERY_CHANGED));

            // Just register these to update the battery percent when plugged/unplugged
            registerReceiver(new BatteryLevelReceiver(), new IntentFilter(
                    Intent.ACTION_POWER_CONNECTED));
            registerReceiver(new BatteryLevelReceiver(), new IntentFilter(
                    Intent.ACTION_POWER_DISCONNECTED));

            // kep the activity on while the flashlight is on
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            // Create and load the banner ad
            AdView mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);


            // Retrieve the shared preferences and show the ratings
            // fragment if the user is allowing it.
            // Restore preferences
            SharedPreferences settings = getPreferences(MainActivity.MODE_PRIVATE);
            if (settings.getBoolean(PREFS_REQUEST_RATING, true)) {
                if (savedInstanceState == null) {
                    fm.beginTransaction()
                            .add(R.id.activity_main
                                    , RatingFragment.newInstance()
                                    , RatingFragment.TAG)
                            .addToBackStack(RatingFragment.TAG)
                            .commit();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // If the battery is not low show the torch
        if (!mBatteryLow) {

            // Get the camera and start the LED light
            if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {

                mCamera = Camera.open();
                try {
                    // need to set a SurfaceTexture for nexus and maybe other new devices
                    mCamera.setPreviewTexture(new SurfaceTexture(0));
                } catch (IOException ioe) {
                    Log.e(TAG, ioe.toString());
                }

                Camera.Parameters p = mCamera.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(p);
                mCamera.startPreview();
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.release();
        }

        // Finish the activity
        finish();
    }

    /*
    Get the status of the battery and return a percent value from 0 - 100
     */
    private int getBatteryPercent() {
        int batteryPercent = 100;

        // Get the status of the battery.
        // Don't want to start the flashlight if in low power
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, intentFilter);

        // If the battery status can be determined, use it to stop users from opening the flashlight
        // when their device has low battery.
        if (batteryStatus != null) {
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            // calculate the percentage
            batteryPercent = (int) ((level / (float) scale) * 100);
        }
        return batteryPercent;
    }

    /*
    Get the current battery status icon for this device.
     */
    @Nullable
    private Drawable getScaledCurrentBatteryIcon(float scale) {

        LevelListDrawable currentIcon = null;
        Drawable result = null;
        int level = 0;
        int batteryIconId = 0;

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, intentFilter);

        // if access to battery status is granted set the icon to the current level
        if (batteryStatus != null) {
            level = batteryStatus.getIntExtra(android.os.BatteryManager.EXTRA_LEVEL, 0);
            batteryIconId = batteryStatus.getIntExtra(android.os.BatteryManager.EXTRA_ICON_SMALL, 0);

            currentIcon = (LevelListDrawable) getResources().getDrawable(batteryIconId);

            if (currentIcon != null) {
                currentIcon.setLevel(level);
            }
        }

        // Cast to a drawable and scale
        if (currentIcon != null) {
            result = Helpers.scaleDrawable((Drawable) currentIcon, scale, this);
        }

        // Return as a regular Drawable
        return result;
    }

    private class BatteryLevelReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Get current battery power
            mBatteryPercent = getBatteryPercent();

            // update the Action Bar
            invalidateOptionsMenu();

            // If Battery is below the threshold
            if (mBatteryPercent < BATTERY_MINIMUM) {

                // remove the keep screen on flag
                getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                // switch off the flashlight
                if (mCamera != null) {
                    mCamera.release();
                }

                // Remove the advertising
                AdView adView = (AdView) findViewById(R.id.adView);
                if (adView != null) {
                    adView.setVisibility(View.GONE);
                }

                // Remove the spacer view
                View spacerView = (View) findViewById(R.id.activity_main_spacer_view);
                if (spacerView != null) {
                    spacerView.setVisibility(View.GONE);
                }

                // Remove the ratings fragment if it is visible
                getFragmentManager().popBackStack(
                        RatingFragment.TAG
                        , FragmentManager.POP_BACK_STACK_INCLUSIVE);

                // Get a reference to existing fragment
                Fragment existingFragment =
                        getFragmentManager()
                                .findFragmentByTag(LowBatteryFragment.TAG);

                // if existing fragment does noe exist.. show low battery fragment
                if (existingFragment == null) {
                    // Show the low battery fragment
                    getFragmentManager().beginTransaction()
                            .add(R.id.activity_main
                                    , LowBatteryFragment.newInstance()
                                    , LowBatteryFragment.TAG)
                            .commit();
                }
            }
        }
    }

}
