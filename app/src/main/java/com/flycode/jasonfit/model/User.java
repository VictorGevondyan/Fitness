package com.flycode.jasonfit.model;

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

    public static final class METRICS {
        public static final String CM = "cm";
        public static final String KG = "kg";
        public static final String FOOT = "foot";
        public static final String POUND = "pound";
    }

    public static final class NUTRITION {
        public static final String ALL = "all";
        public static final String VEGETARIAN = "vegetarian";
        public static final String VEGAN = "vegan";
    }

    private static UserPreferences userPreferences;

    public static UserPreferences sharedPreferences(Context context) {

        if ( userPreferences == null ) {
            userPreferences = new UserPreferences(context);
        }

        return userPreferences;
    }

    @Property(key = "height", defValue = "160.0")
    public abstract float height();

    @Property(key = "weight", defValue = "65.0")
    public abstract float weight();

    @Property(key = "birthday", defValue = "631137600000")
    public abstract long birthday();

    @Property(key = "gender", defValue = GENDER.FEMALE)
    public abstract String gender();

    @Property(key = "nutrition", defValue = NUTRITION.ALL)
    public abstract String nutrition();

    @Property(key = "language", defValue = LANGUAGE.ENGLISH)
    public abstract String language();

    @Property(key = "heightMeasurement", defValue = METRICS.CM)
    public abstract String heightMeasurement();

    @Property(key = "weightMeasurement", defValue = METRICS.KG)
    public abstract String weightMeasurement();

}