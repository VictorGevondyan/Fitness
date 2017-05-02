package com.flycode.jasonfit.model;

import android.content.Context;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * Created - Schumakher on  3/7/17.
 */

@Table(name = "Food")
public class Food extends Model implements Serializable {
    @Column(name = "food", unique = true)
    public String food;

    @Column(name = "food_de", unique = true)
    public String foodDe;

    @Column(name = "category", index = true)
    public String category;

    @Column(name = "water")
    public double water;

    @Column(name = "energy")
    public double energy;

    @Column(name = "protein")
    public double protein;

    @Column(name = "fat")
    public double fat;

    @Column(name = "carbohydrate")
    public double carbohydrate;

    @Column(name = "fiber")
    public double fiber;

    @Column(name = "sugars")
    public double sugars;

    @Column(name = "calcium")
    public double calcium;

    @Column(name = "iron")
    public double iron;

    @Column(name = "magnesium")
    public double magnesium;

    @Column(name = "phosphorus")
    public double phosphorus;

    @Column(name = "potassium")
    public double potassium;

    @Column(name = "sodium")
    public double sodium;

    @Column(name = "zinc")
    public double zinc;

    @Column(name = "vitamin_c")
    public double vitaminC;

    @Column(name = "thiamin")
    public double thiamin;

    @Column(name = "riboflavin")
    public double riboflavin;

    @Column(name = "niacin")
    public double niacin;

    @Column(name = "vitamin_b_6")
    public double vitaminB6;

    @Column(name = "folate_dfe")
    public double folateDFE;

    @Column(name = "vitamin_b_12")
    public double vitaminB12;

    @Column(name = "vitamin_a_rae")
    public double vitaminARAE;

    @Column(name = "vitamin_a_iu")
    public double vitaminAIU;

    @Column(name = "vitamin_e")
    public double vitaminE;

    @Column(name = "vitamin_d_d2_d3")
    public double vitaminDD2D3;

    @Column(name = "vitamin_d")
    public double vitaminD;

    @Column(name = "vitamin_k")
    public double vitaminK;

    @Column(name = "fatty_acids_total_saturated")
    public double fattyAcidsTotalSaturated;

    @Column(name = "fatty_acids_total_monounsaturated")
    public double fattyAcidsTotalMonounsaturated;

    @Column(name = "fatty_acids_total_polyunsaturated")
    public double fattyAcidsTotalPolyunsaturated;

    @Column(name = "fatty_acids_total_trans")
    public double fattyAcidsTotalTrans;

    @Column(name = "cholesterol")
    public double cholesterol;

    @Column(name = "caffeine")
    public double caffeine;

    public static double[][] getValues(Food food) {
        return new double[][] {
                getProximities(food),
                getMinerals(food),
                getVitamins(food),
                getLipids(food),
                getOther(food)
        };
    }

    private static double[] getProximities(Food food) {
        return new double[] {
                food.water,
                food.energy,
                food.protein,
                food.fat,
                food.carbohydrate,
                food.fiber,
                food.sugars
        };
    }

    private static double[] getMinerals(Food food) {
        return new double[] {
                food.calcium,
                food.iron,
                food.magnesium,
                food.phosphorus,
                food.potassium,
                food.sodium,
                food.zinc
        };
    }

    private static double[] getVitamins(Food food) {
        return new double[] {
                food.vitaminC,
                food.thiamin,
                food.riboflavin,
                food.niacin,
                food.vitaminB6,
                food.folateDFE,
                food.vitaminB12,
                food.vitaminARAE,
                food.vitaminAIU,
                food.vitaminE,
                food.vitaminDD2D3,
                food.vitaminD,
                food.vitaminK
        };
    }

    private static double[] getLipids(Food food) {
        return new double[] {
                food.fattyAcidsTotalSaturated,
                food.fattyAcidsTotalMonounsaturated,
                food.fattyAcidsTotalPolyunsaturated,
                food.fattyAcidsTotalTrans,
                food.cholesterol
        };
    }

    private static double[] getOther(Food food) {
        return new double[] {
                food.caffeine
        };
    }

    public String getTranslatedName(Context context) {
        if (User.sharedPreferences(context).getLanguage().equals(User.LANGUAGE.ENGLISH)) {
            return food;
        } else if (User.sharedPreferences(context).getLanguage().equals(User.LANGUAGE.DEUTSCH)) {
            return foodDe;
        }

        return "";
    }
}
