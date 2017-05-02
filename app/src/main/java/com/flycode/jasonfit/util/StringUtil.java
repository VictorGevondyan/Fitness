package com.flycode.jasonfit.util;

import java.util.HashMap;

/**
 * Created - Schumakher on  3/11/17.
 */

public class StringUtil {
    private static HashMap<String, String> translationMap = new HashMap<>();

    public static String getFormattedTime (int timeHours, int timeMins, int timeSecs) {
        String estimatedTimeString;

        String timeHoursString;
        String timeMinsString;
        String timeSecsString;

        if (timeHours < 10) {
            timeHoursString = "0" + String.valueOf(timeHours);
        } else {
            timeHoursString = String.valueOf(timeHours);
        }

        if (timeMins < 10) {
            timeMinsString = "0" + String.valueOf(timeMins);
        } else {
            timeMinsString = String.valueOf(timeMins);
        }

        if (timeSecs < 10) {
            timeSecsString = "0" + String.valueOf(timeSecs);
        } else {
            timeSecsString = String.valueOf(timeSecs);
        }

        if (timeHours != 0) {
            estimatedTimeString = timeHoursString + ":" + timeMinsString + ":" + timeSecsString;
        } else if (timeMins != 0) {
            estimatedTimeString = timeMinsString + ":" + timeSecsString;
        } else {
            estimatedTimeString = "00:" + timeSecsString;
        }

        return estimatedTimeString;
    }

    public static String formattedDigitValue(float value) {
        double temp = value;

        if( Math.floor(temp) == temp ){
            int tempInt = (int)temp;
            return "" + tempInt;
        }

        return "" + formatToLastDigit((float)temp);
    }

    public static float formatToLastDigit(float value) {
        value = value * 10;
        value = Math.round(value);
        value = value / 10;

        return value;
    }
}
