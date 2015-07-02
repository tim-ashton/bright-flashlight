package com.timashton.bright_flashlight;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

/*
 * Created by Tim Ashton on 1/07/15.
 *
 * This dialog fragment will request a user
 * rate the app in the gooogle play.
 *
 */
public class RatingDialogFragment extends DialogFragment implements View.OnClickListener {

    public static String TAG = RatingDialogFragment.class.getName();

    @NonNull
    public static RatingDialogFragment newInstance() {
        return new RatingDialogFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_dialog_rating, container, false);

        Dialog ratingDialog = getDialog();

        ratingDialog.setTitle(R.string.fragment_rating_dialog_title);


        Window window = ratingDialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.y = dpToPx(100);
        window.setAttributes(params);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button rateNowButton = (Button) view.findViewById(R.id.rate_now_button);
        Button rateLaterButton = (Button) view.findViewById(R.id.rate_later_button);
        Button neverShowButton = (Button) view.findViewById(R.id.never_show_again_button);

        rateNowButton.setOnClickListener(this);
        rateLaterButton.setOnClickListener(this);
        neverShowButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rate_now_button:
                // TODO
                break;
            case R.id.rate_later_button:

                // Dismiss the dialog
                this.getDialog().dismiss();
                break;
            case R.id.never_show_again_button:

                // Save the never show flag to shared preferences
                SharedPreferences settings = getActivity()
                        .getPreferences(MainActivity.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(MainActivity.PREFS_SHOW_DIALOG, false);

                // Commit the edits!
                editor.apply();

                // now dismiss the dialog
                this.getDialog().dismiss();

                break;
        }
    }


    /*
    Converts the value entered into pixels for programmatic sizing and placement
    of view items.
    Allows specification of dp similar to xml declaration.
     */

    private int dpToPx(int dp) {
        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }
}
