package com.flycode.jasonfit;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.flycode.jasonfit.model.Food;
import com.flycode.jasonfit.model.StatsData;

/**
 * Created by acerkinght on 3/9/17.
 */

public class JasonFitApplication extends Application {
    private static JasonFitApplication application;

    public static JasonFitApplication sharedApplication() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Configuration configuration = new Configuration.Builder(this)
                .setDatabaseName("fitness")
                .setDatabaseVersion(1)
                .setModelClasses(Food.class, StatsData.class)
                .create();
        ActiveAndroid.initialize(configuration);

        application = this;
    }
}
