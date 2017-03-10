package com.flycode.jasonfit.activity.model;

import android.content.Context;

import io.t28.shade.annotation.Preferences;
import io.t28.shade.annotation.Property;

/**
 * Created - Schumakher on  3/10/17.
 */
@Preferences("com.flycode.jasonfit.workouttrack")
public abstract class WorkoutTrack {
    @Property(key = "workoutId")
    public abstract int workoutId();

    @Property(key = "workoutNumber")
    public abstract int workoutNumber();

    @Property(key = "totalWorkoutTime")
    public abstract long totalWorkoutTime();
}
