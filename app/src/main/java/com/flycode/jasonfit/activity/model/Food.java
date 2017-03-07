package com.flycode.jasonfit.activity.model;

/**
 * Created - Schumakher on  3/7/17.
 */

public class Food {
    public int id;
    private String name;
    private Float water;
    private int energy;
    private Float protein;
    private Float fat;
    private Float carbohydrates;
    private Float fiber;
    private Float sugars;

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

    public Float getWater() {
        return water;
    }

    public void setWater(Float water) {
        this.water = water;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public Float getProtein() {
        return protein;
    }

    public void setProtein(Float protein) {
        this.protein = protein;
    }

    public Float getFat() {
        return fat;
    }

    public void setFat(Float fat) {
        this.fat = fat;
    }

    public Float getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(Float carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public Float getFiber() {
        return fiber;
    }

    public void setFiber(Float fiber) {
        this.fiber = fiber;
    }

    public Float getSugars() {
        return sugars;
    }

    public void setSugars(Float sugars) {
        this.sugars = sugars;
    }
}
