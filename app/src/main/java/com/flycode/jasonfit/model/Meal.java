package com.flycode.jasonfit.model;

import android.content.Context;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * Created by acerkinght on 3/22/17.
 */

@Table(name = "Meal", id = "activeId")
public class Meal extends Model implements Serializable {
    @Column(name = "name")
    public String name;

    @Column(name = "name_de")
    public String nameDe;

    @Column(name = "type")
    public String type;

    @Column(name = "ingredients")
    public String ingredients;

    @Column(name = "instructions")
    public String instructions;

    public String getTranslatedName(Context context) {
        if (User.sharedPreferences(context).getLanguage().equals(User.LANGUAGE.ENGLISH)) {
            return name;
        } else if (User.sharedPreferences(context).getLanguage().equals(User.LANGUAGE.DEUTSCH)) {
            return nameDe;
        }

        return "";
    }
}
