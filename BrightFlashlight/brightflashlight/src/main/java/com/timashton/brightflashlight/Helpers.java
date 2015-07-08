package com.timashton.brightflashlight;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/*
 * Created by Tim Ashton on 4/07/15.
 *
 * Class with static helper methods for this application
 */
public class Helpers {


    /*
   Scale a drawable image
    */
    public static Drawable scaleDrawable(
            @NonNull Drawable image
            , float scaleFactor
            , @NonNull Activity activity) {

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

        // Return the resized bitmap as a drawable
        return new BitmapDrawable(activity.getResources(), bitmapResized);

    }

        /*
    Converts the value entered into pixels for programmatic sizing and placement
    of view items.
    Allows specification of dp similar to xml declaration.
     */
    public static int dpToPx(int dp, Activity activity) {
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }
}
