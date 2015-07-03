package com.timashton.bright_flashlight;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.IOException;


public class MainActivity extends Activity {

    public static final String TAG = MainActivity.class.getName();
    public static final String PREFS_SHOW_DIALOG = "show_dialog_boolean";
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";

    @SuppressWarnings("deprecation")
    private Camera mCamera;
    private int mSavedBrightness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        // Retrieve the shared preferences and show the ratings
        // fragment if the user is allowing it.
        // Restore preferences
        SharedPreferences settings = getPreferences(MainActivity.MODE_PRIVATE);
        boolean showDialog = settings.getBoolean(PREFS_SHOW_DIALOG, true);
        if (showDialog) {
            FragmentManager fm = getFragmentManager();

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

    @Override
    protected void onStart() {
        super.onStart();

        // Dim the screen. The user is probably in a dark place
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.screenBrightness = 0.0f;
        this.getWindow().setAttributes(params);

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
