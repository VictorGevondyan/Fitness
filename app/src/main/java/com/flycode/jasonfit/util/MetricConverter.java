package com.flycode.jasonfit.util;

import android.icu.math.BigDecimal;
import android.util.Log;

import com.flycode.jasonfit.model.User;

/**
 * Created by acerkinght on 3/16/17.
 */

public class MetricConverter {
    private static float inchToCM(float inch) {
        return inch * 2.54f;
    }

    private static float cmToInch(float cm) {
        return cm * 0.393700787f;
    }

    private static float lbToKG(float lb) {
        return lb * 0.45359237f;
    }

    private static float kgToLB(float kg) {
        return kg * 2.20462262f;
    }

    public static float convertWeight(float weight, String metric, boolean isInput) {
        if (metric.equals(User.METRICS.KG)) {
            return Math.round(weight);
        } else {
            if (isInput) {
                return lbToKG(weight);
            } else {
                return kgToLB(weight);
            }
        }
    }

    public static float convertHeight(float height, String metric, boolean isInput) {
        if (metric.equals(User.METRICS.CM)) {
            return Math.round(height);
        } else {
            if (isInput) {
                return inchToCM(height);
            } else {
                return cmToInch(height);

            }
        }
    }
}
