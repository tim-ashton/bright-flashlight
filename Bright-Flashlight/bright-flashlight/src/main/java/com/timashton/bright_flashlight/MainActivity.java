package com.timashton.bright_flashlight;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import java.io.IOException;


public class MainActivity extends Activity {

    public static final String TAG = MainActivity.class.getName();
    public static final String PREFS_SHOW_DIALOG = "show_dialog_boolean";

    @SuppressWarnings("deprecation")
    private Camera mCamera;
    private int mSavedBrightness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            try{
                // need to set a SurfaceTexture for nexus and maybe other new devices
                mCamera.setPreviewTexture(new SurfaceTexture(0));
            }
            catch (IOException ioe){
                Log.e(TAG, ioe.toString());
            }

            Camera.Parameters p = mCamera.getParameters();
            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(p);
            mCamera.startPreview();
        }


        // Retrieve the shared preferences and show the ratings
        // dialog if the user is allowing it.
        // Restore preferences
        SharedPreferences settings = getPreferences(MainActivity.MODE_PRIVATE);
        boolean showDialog = settings.getBoolean(PREFS_SHOW_DIALOG, true);
        if (showDialog) {
            RatingDialogFragment ratingDialogFragment = RatingDialogFragment.newInstance();
            ratingDialogFragment.show(
                    getFragmentManager().beginTransaction(), RatingDialogFragment.TAG);
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
