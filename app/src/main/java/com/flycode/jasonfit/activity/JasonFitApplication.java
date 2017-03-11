package com.flycode.jasonfit.activity;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;

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
                .create();
        ActiveAndroid.initialize(configuration);

        application = this;
    }
}
