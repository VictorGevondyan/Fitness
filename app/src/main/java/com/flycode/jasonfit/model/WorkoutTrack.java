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
@Preferences("com.flycode.jasonfit.workoutTrack")
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

    @Property(key = "status", defValue = STATUS.IDLE)
    public abstract String status();

    @Property(key = "timing", converter = IntegerSetConverter.class)
    public abstract int[] timing();

    @Property(key = "subWorkoutNames", converter = StringSetConverter.class)
    public abstract String[] subWorkoutNames();

    public static class IntegerSetConverter implements Converter<int[], String> {

        @NonNull
        @Override
        public int[] toConverted(@Nullable String strings) {
            String[] splitString = strings.split(",");
            int[] integers = new int[splitString.length];

            for (int index = 0 ; index < splitString.length ; index++) {
                integers[index] = Integer.valueOf(splitString[index]);
            }

            return integers;
        }

        @NonNull
        @Override
        public String toSupported(@Nullable int[] integers) {
            StringBuilder stringBuilder = new StringBuilder();
            int index = 0;

            for (int integer : integers) {
                stringBuilder.append(integer);
                index++;

                if (index < integers.length) {
                    stringBuilder.append(",");
                }
            }

            return stringBuilder.toString();
        }
    }

    public static class StringSetConverter implements Converter<String[], String> {

        @NonNull
        @Override
        public String[] toConverted(@Nullable String strings) {
            return strings.split(",");
        }

        @NonNull
        @Override
        public String toSupported(@Nullable String[] strings) {
            StringBuilder stringBuilder = new StringBuilder();
            int index = 0;

            for (String string : strings) {
                stringBuilder.append(string);
                index++;

                if (index < strings.length) {
                    stringBuilder.append(",");
                }
            }

            return stringBuilder.toString();
        }
    }
}
