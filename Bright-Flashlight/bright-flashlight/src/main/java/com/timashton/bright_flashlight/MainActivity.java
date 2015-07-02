package com.timashton.bright_flashlight;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;


public class MainActivity extends Activity {

    public static final String PREFS_SHOW_DIALOG = "show_dialog_boolean";

    @SuppressWarnings("deprecation")
    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {

            mCamera = Camera.open();
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
        if(showDialog){
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
