package com.flycode.jasonfit.activity.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArraySet;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import io.t28.shade.annotation.Preferences;
import io.t28.shade.annotation.Property;
import io.t28.shade.converter.Converter;

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

    @Property(key = "currentWorkoutTimeArray", converter = IntegerSetConverter.class)
    public abstract ArrayList<Integer> currentWorkoutTimeArray();

    @Property(key = "status")
    public abstract String status();

    public static class IntegerSetConverter implements Converter<ArrayList<Integer>, String> {

        @NonNull
        @Override
        public ArrayList<Integer> toConverted(@Nullable String strings) {
            ArrayList<Integer> integers = new ArrayList<>();

            for (String string : strings.split(",")) {
                integers.add(Integer.valueOf(string));
            }

            return integers;
        }

        @NonNull
        @Override
        public String toSupported(@Nullable ArrayList<Integer> integers) {
            StringBuilder stringBuilder = new StringBuilder();
            int index = 0;

            for (int integer : integers) {
                stringBuilder.append(integer);
                index++;

                if (index < integers.size()) {
                    stringBuilder.append(",");
                }
            }
            return stringBuilder.toString();
        }
    }
}
