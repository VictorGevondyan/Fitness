package com.flycode.jasonfit.activity;

import android.app.Application;

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

        application = this;
    }
}
