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
import com.flycode.jasonfit.model.MealType;

import java.util.ArrayList;

/**
 * Created by victor on 3/23/17.
 */


public class MealTypesListAdapter extends ExpandableRecyclerAdapter<MealType, Meal, MealTypeViewHolder, MealViewHolder>
        implements OnItemClickListener{

    private ArrayList<MealType> mealsTypeList;

    private LayoutInflater layoutInflater;

    private MealTypesListAdapter.OnMealItemClickListener listener;

    public MealTypesListAdapter(Context context, @NonNull ArrayList<MealType> mealsTypeList, OnMealItemClickListener listener) {
        super(mealsTypeList);
        this.mealsTypeList = mealsTypeList;
        this.listener = listener;
        layoutInflater = LayoutInflater.from(context);
    }

    // onCreate ...
    @NonNull
    @Override
    public MealTypeViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View mealTypeView = layoutInflater.inflate(R.layout.item_meals_type, parentViewGroup, false);
        return new MealTypeViewHolder(mealTypeView);
    }

    @NonNull
    @Override
    public MealViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View mealView = layoutInflater.inflate(R.layout.item_meal, childViewGroup, false);
        return new MealViewHolder(mealView);
    }

    // onBind ...
    @Override
    public void onBindParentViewHolder(@NonNull MealTypeViewHolder mealTypeViewHolder, int parentPosition, @NonNull MealType mealsType) {
        mealTypeViewHolder.bind(mealsType);
    }

    @Override
    public void onBindChildViewHolder(@NonNull MealViewHolder mealViewHolder, int parentPosition, int childPosition, @NonNull Meal meal) {
        mealViewHolder.bind(meal);
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder) {
        int positionOfParent = ( (MealViewHolder)viewHolder ).getParentAdapterPosition();
        MealType parentMealsType = mealsTypeList.get( positionOfParent );

        int positionOfChildInSublist = ( (MealViewHolder)viewHolder ).getChildAdapterPosition();
        Meal correspondingMeal = parentMealsType.getChildList().get(positionOfChildInSublist);
        listener.onMealItemClick(correspondingMeal);
    }

    public interface OnMealItemClickListener {
        void onMealItemClick(Meal meal);
    }

}





