package com.flycode.jasonfit.util;

/**
 * Created - Schumakher on  3/11/17.
 */

public class StringUtil {
    public static String test (int timeHours, int timeMins, int timeSecs) {
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
}
