package com.flycode.jasonfit.activity.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created - Schumakher on  3/7/17.
 */

@Table(name = "Food")
public class Food extends Model {
    @Column(name = "food", unique = true)
    public String food;

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

    @Column(name = "floate_dfe")
    public double floateDFE;

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

}
