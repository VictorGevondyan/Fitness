package com.flycode.jasonfit.activity.model;

import io.t28.shade.annotation.Preferences;
import io.t28.shade.annotation.Property;

/**
 * Created by victor on 3/9/17.
 */

@Preferences("com.flycode.jasonfit.user")
public abstract class User {

    @Property(key = "height", defValue = "-1")
    public abstract int height();

    @Property(key = "width", defValue = "-1")
    public abstract int width();

}