package com.timashton.bright_flashlight;

import android.app.Fragment;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
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
    public static String TAG = RatingFragment.class.getName();

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

                Drawable resized = scaleDrawable(systemBatteryDrawable, ICON_SCALE_FACTOR);
                lowBatteryTV.setCompoundDrawablesWithIntrinsicBounds(resized, null, null, null);
            }

        }
    }


    /*
    Scale a drawable image
     */
    private Drawable scaleDrawable(@NonNull Drawable image, float scaleFactor) {

        Bitmap b;

        // convert image over to bitmap b
        if (image instanceof BitmapDrawable) {
            b = ((BitmapDrawable) image).getBitmap();
        } else {
            Bitmap bitmap = Bitmap.createBitmap(
                    image.getIntrinsicWidth(), image.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            image.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            image.draw(canvas);
            b = bitmap;
        }

        // if b is null the conversion has failed
        if (b == null) {
            return image;
        }

        // apply the scale to each side
        int sizeX = Math.round(image.getIntrinsicWidth() * scaleFactor);
        int sizeY = Math.round(image.getIntrinsicHeight() * scaleFactor);

        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, sizeX, sizeY, false);

        return new BitmapDrawable(getResources(), bitmapResized);

    }
}
