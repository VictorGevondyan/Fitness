package com.flycode.jasonfit.activity.model;

import android.content.Context;

import io.t28.shade.annotation.Preferences;
import io.t28.shade.annotation.Property;

/**
 * Created by victor on 3/9/17.
 */

@Preferences("com.flycode.jasonfit.user")
public abstract class User {
    public static final class GENDER {
        public static final String MALE = "male";
        public static final String FEMALE = "female";
    }

    public static final class LANGUAGE {
        public static final String ENGLISH = "english";
        public static final String DEUTSCH = "deutsch";
    }

    public static final class MEASUREMENTS {
        public static final String CM = "cm";
        public static final String KG = "kg";
        public static final String FOOT = "foor";
        public static final String POUND = "pount";
    }

    private static UserPreferences userPreferences;

    public static UserPreferences sharedPreferences(Context context) {
        if (userPreferences == null) {
            userPreferences = new UserPreferences(context);
        }

        return userPreferences;
    }

    @Property(key = "height", defValue = "-1")
    public abstract int height();

    @Property(key = "weight", defValue = "-1")
    public abstract int weight();

    @Property(key = "birthday", defValue = "768510000000")
    public abstract long birthday();

    @Property(key = "gender", defValue = GENDER.MALE)
    public abstract String gender();

    @Property(key = "nutrition", defValue = "vegan")
    public abstract String nutrition();

    @Property(key = "language", defValue = LANGUAGE.ENGLISH)
    public abstract String language();

    @Property(key = "heightMeasurement", defValue = MEASUREMENTS.CM)
    public abstract String heightMeasurement();

    @Property(key = "weightMeasurement", defValue = MEASUREMENTS.KG)
    public abstract String weightMeasurement();
}