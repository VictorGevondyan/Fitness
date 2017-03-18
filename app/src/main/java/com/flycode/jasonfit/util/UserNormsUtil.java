package com.flycode.jasonfit.util;

import android.content.Context;

import com.flycode.jasonfit.R;

/**
 * Created - Schumakher on  3/15/17.
 */

public class UserNormsUtil {

    public static double getBMI ( float weight, double height ) {
        double heightInMetrs = height / 100;
        double bMI = weight / Math.pow(heightInMetrs, 2);

        return Math.floor(bMI * 10) / 10;
    }

    public static String getOverweightType(Context context, double bMI) {

        if (bMI <= 15) {
            return getString(context, R.string.bmi_very_underweight);
        } else if (bMI <= 16) {
            return getString(context, R.string.bmi_severely_underweight);
        } else if (bMI <= 18.5) {
            return  getString(context, R.string.bmi_underweight);
        } else if (bMI <= 25) {
            return getString(context, R.string.bmi_normal);
        } else if (bMI <= 30) {
            return getString(context, R.string.bmi_overweight);
        } else if (bMI <= 35) {
            return getString(context, R.string.bmi_obese1);
        } else if (bMI <= 40) {
            return getString(context, R.string.bmi_obese2);
        } else if (bMI > 40) {
            return getString(context, R.string.bmi_obese3);
        }

        return getString(context, R.string.bmi_human);
    }


    private static String getString ( Context context, int string) {
        return context.getResources().getString(string);
    }
}
