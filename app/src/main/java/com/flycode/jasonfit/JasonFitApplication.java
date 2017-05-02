package com.flycode.jasonfit;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.flycode.jasonfit.model.Food;
import com.flycode.jasonfit.model.Meal;
import com.flycode.jasonfit.model.StatsData;
import com.flycode.jasonfit.model.Translation;
import com.flycode.jasonfit.model.Workout;

/**
 * Created by acerkinght on 3/9/17.
 */

public class JasonFitApplication extends Application {
    private static JasonFitApplication application;

    public static JasonFitApplication sharedApplication() {
        return application;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate() {
        super.onCreate();

        Configuration configuration = new Configuration.Builder(this)
                .setDatabaseName("fitness")
                .setDatabaseVersion(1)
                .setModelClasses(Food.class, StatsData.class, Workout.class, Meal.class, Translation.class)
                .create();
        ActiveAndroid.initialize(configuration);

        application = this;
    }
}
