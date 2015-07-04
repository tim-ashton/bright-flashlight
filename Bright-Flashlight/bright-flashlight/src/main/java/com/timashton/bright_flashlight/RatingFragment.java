package com.timashton.bright_flashlight;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/*
 * Created by Tim Ashton on 1/07/15.
 *
 * This dialog fragment will request a user
 * rate the app in the gooogle play.
 *
 */
public class RatingFragment extends Fragment implements View.OnClickListener {

    public static String TAG = RatingFragment.class.getName();

    @NonNull
    public static RatingFragment newInstance() {
        return new RatingFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_rating, container, false);

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

        // Get a reference to the FragmentManager
        FragmentManager fm = getActivity().getFragmentManager();

        switch (v.getId()) {
            case R.id.rate_now_button:
                // TODO
                break;
            case R.id.rate_later_button:

                // Remove this fragment
                fm.popBackStack(TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                break;
            case R.id.never_show_again_button:

                // Save the never show flag to shared preferences
                SharedPreferences settings = getActivity()
                        .getPreferences(MainActivity.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(MainActivity.PREFS_REQUEST_RATING, false);

                // Commit the edits!
                editor.apply();

                // Remove this fragment
                fm.popBackStack(TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
