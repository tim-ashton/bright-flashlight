package com.timashton.bright_flashlight;

import android.app.Fragment;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static android.os.BatteryManager.EXTRA_ICON_SMALL;

/*
 * Created by Tim Ashton on 3/07/15.
 *
 * This fragment will display text to warn the user about low battery
 * and display the battery icon.
 */
public class LowBatteryFragment extends Fragment {

    public static String TAG = LowBatteryFragment.class.getName();

    private static float ICON_SCALE_FACTOR = 2.0f;

    @NonNull
    public static LowBatteryFragment newInstance() {
        return new LowBatteryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);


        return inflater.inflate(R.layout.fragment_battery_low, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getActivity().registerReceiver(null, intentFilter);

        // Resize the icon
        // if batteryStatus is unavailable the icon cannot be retrieved
        if (batteryStatus != null) {

            TextView lowBatteryTV = (TextView) view.findViewById(R.id.fragment_low_battery_text_view);

            // As this application targets api 15+ this version of getDrawable is the only method available
            Drawable systemBatteryDrawable = (Drawable) getActivity()
                    .getResources()
                    .getDrawable(batteryStatus.getIntExtra(EXTRA_ICON_SMALL, -1));

            if (systemBatteryDrawable != null) {

                Drawable resized = Helpers.scaleDrawable(
                        systemBatteryDrawable
                        , ICON_SCALE_FACTOR
                        , getActivity());

                lowBatteryTV.setCompoundDrawablesWithIntrinsicBounds(resized, null, null, null);
            }

        }
    }



}
