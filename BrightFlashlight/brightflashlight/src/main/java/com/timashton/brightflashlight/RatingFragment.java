package com.timashton.brightflashlight;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
    private static String PLAY_STORE_CONNECTION = "market://details?id=com.timashton.brightflashlight";

    @NonNull
    public static RatingFragment newInstance() {
        return new RatingFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_rating, container, false);
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

                // Remove this fragment
                fm.popBackStack(TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                // Start the Google Play intent and send user to the bright flashlight listing
                Intent intent = new Intent(
                        Intent.ACTION_VIEW
                        , Uri.parse(PLAY_STORE_CONNECTION));
                startActivity(intent);

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
}
