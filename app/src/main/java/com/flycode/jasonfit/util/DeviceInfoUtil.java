package com.flycode.jasonfit.util;


import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created - Schumakher on  4/6/17.
 */

public class DeviceInfoUtil {

    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(
                context.getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
    }

    public static String getOsVersion() {

        return String.valueOf(Build.VERSION.SDK_INT);
    }

    public static String getDeviceInfo() {

        return Build.MODEL + Build.MANUFACTURER;
    }

    public static Point getScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size;
    }

    public static float getPxForDp(Context context, float dp) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics() ;
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
    }
}
