package com.flycode.jasonfit.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.flycode.jasonfit.model.Workout;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created - Schumakher on  3/11/17.
 */

public class ImageUtil {
    public static Bitmap getImageBitmap(Context context, Workout workout) {

        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(workout.getPicture());
        } catch (IOException e) {
            e.printStackTrace();
        }


        return BitmapFactory.decodeStream(inputStream);

    }
}
