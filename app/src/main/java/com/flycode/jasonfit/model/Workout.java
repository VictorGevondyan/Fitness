package com.flycode.jasonfit.model;

import android.graphics.Color;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created - Schumakher on  3/9/17.
 */

@Table(name = "Workout", id = "activeId")
public class Workout extends Model implements Serializable {
    public static final String REST = "rest";

    @Column(name = "id")
    public int id;

    @Column(name = "name")
    public String name;

    @Column(name = "pic_id")
    public String picture;

    @Column(name = "weekday")
    public String weekday;

    @Column(name = "color")
    public String color;

    @Column(name = "set_name")
    public String subNames;

    @Column(name = "set_pic_id")
    public String subPictures;

    @Column(name = "set_timing")
    public String subTiming;

    public String[] getSubNames() {
        int[] set = getSet();
        String[] subNamesRawArray = subNames.split(",");
        String[] subNamesFinalArray = new String[set.length];

        for (int index = 0 ; index < set.length ; index++) {
            if (set[index] < 0) {
                subNamesFinalArray[index] = REST;
            } else {
                subNamesFinalArray[index] = subNamesRawArray[set[index]];
            }
        }

        return subNamesFinalArray;
    }

    public String[] getSubPictures() {
        int[] set = getSet();
        String[] subPicturesRawArray = subPictures.split(",");
        String[] subPicturesFinalArray = new String[set.length];

        for (int index = 0 ; index < set.length ; index++) {
            if (set[index] < 0) {
                subPicturesFinalArray[index] = REST;
            } else {
                subPicturesFinalArray[index] = subPicturesRawArray[set[index]];
            }
        }

        return subPicturesFinalArray;
    }

    public int[] getSubTiming() {
        String[] subTimingArray = subTiming.split(",");
        int[] set = new int[subTimingArray.length];

        for (int index = 0 ; index < subTimingArray.length ; index++) {
            String timing = subTimingArray[index];
            String[] components = timing.split(":");
            String duration = components[1];

            set[index] = Integer.valueOf(duration);
        }

        return set;
    }

    private int[] getSet() {
        String[] subTimingArray = subTiming.split(",");
        int[] set = new int[subTimingArray.length];

        for (int index = 0 ; index < subTimingArray.length ; index++) {
            String timing = subTimingArray[index];
            String[] components = timing.split(":");
            String id = components[0];

            if (id.isEmpty() || id.equals("0")) {
                set[index] = -1;
            } else {
                set[index] = Integer.valueOf(id) - 1;
            }
        }

        return set;
    }
}
