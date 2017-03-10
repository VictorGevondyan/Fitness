package com.flycode.jasonfit.activity.model;

import android.content.Context;

import io.t28.shade.annotation.Preferences;
import io.t28.shade.annotation.Property;

/**
 * Created - Schumakher on  3/10/17.
 */
@Preferences("com.flycode.jasonfit.workouttrack")
public abstract class WorkoutTrack {
    private static WorkoutTrackPreferences workoutTrackPreferences;

    public static WorkoutTrackPreferences sharedPreferences(Context context) {
        if (workoutTrackPreferences == null) {
            workoutTrackPreferences = new WorkoutTrackPreferences(context);
        }
        return workoutTrackPreferences;
    }

    @Property(key = "workoutId")
    public abstract int workoutId();

    @Property(key = "workoutNumber")
    public abstract int workoutNumber();

    @Property(key = "totalWorkoutTime")
    public abstract long totalWorkoutTime();

    @Property(key = "currentWorkoutTime")
    public abstract long currentWorkoutTime();

    @Property(key = "status")
    public abstract String status();
}
