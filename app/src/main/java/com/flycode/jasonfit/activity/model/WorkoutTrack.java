package com.flycode.jasonfit.activity.model;

import android.content.Context;

import io.t28.shade.annotation.Preferences;

/**
 * Created - Schumakher on  3/10/17.
 */
@Preferences("com.flycode.jasonfit.workouttrack")
public abstract class WorkoutTrack {
    public static final class WorkoutInfo {
        public static final int workoutId = 0;
        public static final int Number = 0;
    }

    public static final class workoutTime {
        public static final long totalWorkoutTime = 0;
        public static final long currentWorkoutTime = 0;
    }

    public static final class state {
        public static final String state = "state";
    }

    private static UserPreferences userPreferences;

//    public static UserPreferences sharedPreferences(Context context) {
//        if (userPreferences == null) {
//            userPreferences = new UserPreferences(context);
//        }
//    }
}
