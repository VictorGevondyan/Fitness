package com.flycode.jasonfit.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.flycode.jasonfit.model.WorkoutTrackPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import io.t28.shade.annotation.Preferences;
import io.t28.shade.annotation.Property;
import io.t28.shade.converter.Converter;

/**
 * Created - Schumakher on  3/10/17.
 */
@Preferences("com.flycode.jasonfit.workouttrack")
public abstract class WorkoutTrack {

    public static class STATUS {
        public final static String PREPARING_TO_FINISH = "preparingFinish";
        public final static String FINISHED = "finished";
        public final static String RUNNING = "running";
        public final static String PAUSED = "paused";
        public final static String IDLE = "idle";
    }

    private static WorkoutTrackPreferences workoutTrackPreferences;

    public static WorkoutTrackPreferences sharedPreferences(Context context) {
        if (workoutTrackPreferences == null) {
            workoutTrackPreferences = new WorkoutTrackPreferences(context);
        }
        return workoutTrackPreferences;
    }

    @Property(key = "workoutId")
    public abstract int workoutId();

    @Property(key = "subWorkoutNumber")
    public abstract int subWorkoutNumber();

    @Property(key = "totalWorkoutTime")
    public abstract long totalWorkoutTime();

    @Property(key = "subWorkoutTime")
    public abstract long subWorkoutTime();

    @Property(key = "totalWorkoutStatus", defValue = STATUS.IDLE)
    public abstract String totalWorkoutStatus();

    @Property(key = "currentWorkoutTimeArray", converter = IntegerSetConverter.class)
    public abstract ArrayList<Integer> currentWorkoutTimeArray();

    @Property(key = "currentWorkoutNameArray", converter = StringSetConverter.class)
    public abstract ArrayList<String> currentWorkoutNameArray();

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

    public static class StringSetConverter implements Converter<ArrayList<String>, String> {

        @NonNull
        @Override
        public ArrayList<String> toConverted(@Nullable String strings) {
            ArrayList<String> stringsArray = new ArrayList<>();

            Collections.addAll(stringsArray, strings.split(","));

            return stringsArray;
        }

        @NonNull
        @Override
        public String toSupported(@Nullable ArrayList<String> strings) {
            StringBuilder stringBuilder = new StringBuilder();
            int index = 0;

            for (String string : strings) {
                stringBuilder.append(string);
                index++;

                if (index < strings.size()) {
                    stringBuilder.append(",");
                }
            }

            return stringBuilder.toString();
        }

    }

}
