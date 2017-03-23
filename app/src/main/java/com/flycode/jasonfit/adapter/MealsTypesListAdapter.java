package com.flycode.jasonfit.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.flycode.jasonfit.R;
import com.flycode.jasonfit.adapter.viewHolder.MealTypeViewHolder;
import com.flycode.jasonfit.adapter.viewHolder.MealViewHolder;
import com.flycode.jasonfit.model.Meal;
import com.flycode.jasonfit.model.MealsType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victor on 3/23/17.
 */


public class MealsTypesListAdapter extends ExpandableRecyclerAdapter<MealsType, Meal, MealTypeViewHolder, MealViewHolder> {

    private LayoutInflater mInflater;

    public MealsTypesListAdapter(Context context, @NonNull ArrayList<MealsType> mealsTypeList) {
        super(mealsTypeList);
        mInflater = LayoutInflater.from(context);
    }

    // onCreate ...
    @Override
    public MealTypeViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View recipeView = mInflater.inflate(R.layout.item_meals_type, parentViewGroup, false);
        return new MealTypeViewHolder(recipeView);
    }

    @Override
    public MealViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View ingredientView = mInflater.inflate(R.layout.item_meal, childViewGroup, false);
        return new MealViewHolder(ingredientView);
    }

    // onBind ...
    @Override
    public void onBindParentViewHolder(@NonNull MealTypeViewHolder mealTypeViewHolder, int parentPosition, @NonNull MealsType mealsType) {
        mealTypeViewHolder.bind(mealsType);
    }

    @Override
    public void onBindChildViewHolder(@NonNull MealViewHolder mealViewHolder, int parentPosition, int childPosition, @NonNull Meal meal) {
        mealViewHolder.bind(meal);
    }

}





