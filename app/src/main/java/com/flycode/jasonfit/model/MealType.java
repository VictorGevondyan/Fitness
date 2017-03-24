package com.flycode.jasonfit.model;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.List;

/**
 * Created by victor on 3/23/17.
 */

public class MealType implements Parent<Meal> {

    // Several Meals can be of the same type
    private List<Meal> meals;
    private String name;

    public MealType(String name, List<Meal> meals) {
        this.name = name;
        this.meals = meals;
    }

    @Override
    public List<Meal> getChildList() {
        return meals;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public String getName() {
        return name;
    }
}
