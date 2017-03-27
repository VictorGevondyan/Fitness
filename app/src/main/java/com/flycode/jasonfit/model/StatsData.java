package com.flycode.jasonfit.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created - Schumakher on  3/14/17.
 */

@Table(name = "StatsData")
public class StatsData extends Model {

    @Column(name = "dayOfYear")
    public int dayOfYear;

    @Column(name = "year")
    public int year;

    @Column(name = "weight")
    public float weight;

    @Column(name = "burntCalories")
    public Double burntCalories;

    @Column(name = "multiplier")
    public int multiplier;


    private  static int[] getDate(StatsData statsData) {
        return  new int[] {
                statsData.dayOfYear,
                statsData.year
        };
    }
    private static double[] getBurntCalories(StatsData statsData) {
        return new double[] {
                statsData.burntCalories
        };
    }

    private static float[] getWeight(StatsData statsData) {
        return new float[] {
                statsData.weight
        };
    }
}
