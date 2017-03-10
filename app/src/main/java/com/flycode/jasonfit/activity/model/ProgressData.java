package com.flycode.jasonfit.activity.model;

import java.util.Date;

/**
 * Created by victor on 3/10/17.
 */

public class ProgressData {

    // TODO: REPLACE BY DATE OBJECT IN FUTURE
    int date;
    int weight;

    public ProgressData(){}

    public ProgressData( int date, int weight ){
        this.date = date;
        this.weight = weight;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
