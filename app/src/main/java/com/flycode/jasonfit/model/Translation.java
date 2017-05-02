package com.flycode.jasonfit.model;

import android.content.Context;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by acerkinght on 4/14/17.
 */

@Table(name = "Translations")
public class Translation extends Model {
    @Column(name = "name")
    public String name;

    @Column(name = "name_de")
    public String nameDe;

    public String getTranslatedName(Context context) {
        if (User.sharedPreferences(context).getLanguage().equals(User.LANGUAGE.ENGLISH)) {
            return name;
        } else if (User.sharedPreferences(context).getLanguage().equals(User.LANGUAGE.DEUTSCH)) {
            return nameDe;
        }

        return "";
    }
}
