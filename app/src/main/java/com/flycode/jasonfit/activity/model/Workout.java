package com.flycode.jasonfit.activity.model;

import android.graphics.Color;

import java.util.ArrayList;

/**
 * Created - Schumakher on  3/9/17.
 */

public class Workout {
    private int id;
    private String name;
    private String picture;
    private ArrayList<String> weekday;
    private Color color;
    private ArrayList<String> setName;
    private ArrayList<String> setPicture;
    private ArrayList<String> setTiming;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public ArrayList<String> getWeekday() {
        return weekday;
    }

    public void setWeekday(ArrayList<String> weekday) {
        this.weekday = weekday;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public ArrayList<String> getSetName() {
        return setName;
    }

    public void setSetName(ArrayList<String> setName) {
        this.setName = setName;
    }

    public ArrayList<String> getSetPicture() {
        return setPicture;
    }

    public void setSetPicture(ArrayList<String> setPicture) {
        this.setPicture = setPicture;
    }

    public ArrayList<String> getSetTiming() {
        return setTiming;
    }

    public void setSetTiming(ArrayList<String> setTiming) {
        this.setTiming = setTiming;
    }
}
