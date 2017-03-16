package com.flycode.jasonfit.util;

import android.util.Log;

import com.flycode.jasonfit.model.User;

/**
 * Created by acerkinght on 3/16/17.
 */

public class MetricConverter {
    private static int inchToCM(int inch) {
        return (int) (inch * 2.54);
    }

    private static int cmToInch(int cm) {
        return (int) (cm * 0.393700787);
    }

    private static int lbToKG(int lb) {
        return (int) (lb * 0.45359237);
    }

    private static int kgToLB(int kg) {
        return (int) (kg * 2.20462262);
    }

    public static int convertWeight(int weight, String metric, boolean isInput) {
        if (metric.equals(User.METRICS.KG)) {
            return weight;
        } else {
            if (isInput) {
                return lbToKG(weight);
            } else {
                return kgToLB(weight);
            }
        }
    }

    public static int convertHeight(int height, String metric, boolean isInput) {
        if (metric.equals(User.METRICS.CM)) {
            return height;
        } else {
            if (isInput) {
                return inchToCM(height);
            } else {
                return cmToInch(height);
            }
        }
    }
}
