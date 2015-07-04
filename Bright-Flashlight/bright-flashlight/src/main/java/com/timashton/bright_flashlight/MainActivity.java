package com.timashton.bright_flashlight;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.IOException;


public class MainActivity extends Activity {

    public static final String TAG = MainActivity.class.getName();
    public static final String PREFS_REQUEST_RATING = "show_dialog_boolean";
    private static final float BATTERY_MINIMUM = 0.60F;

    @SuppressWarnings("deprecation")
    private Camera mCamera;
    private boolean mBatteryLow = false;

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

        // Get the status of the battery.
        // Don't want to start the flashlight if in low power
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, intentFilter);

        // If the battery status can be determined, use it to stop users from opening the flashlight
        // when their device has low battery.
        if (batteryStatus != null) {
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            // Battery level below 10 %
            if ((level / (float) scale) < BATTERY_MINIMUM) {
                mBatteryLow = true;
            }
        }


        if (mBatteryLow) {

            // Remove the spacer view to put the low battery fragment
            // in the middle of the screen
            View spacerView = (View) findViewById(R.id.activity_main_spacer_view);
            spacerView.setVisibility(View.GONE);

            fm.beginTransaction()
                    .add(R.id.activity_main
                            , LowBatteryFragment.newInstance()
                            , LowBatteryFragment.TAG)
                    .commit();
        } else {

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
}
