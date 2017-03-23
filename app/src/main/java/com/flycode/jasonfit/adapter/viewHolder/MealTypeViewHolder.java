package com.flycode.jasonfit.adapter.viewHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.flycode.jasonfit.R;
import com.flycode.jasonfit.adapter.OnItemClickListener;
import com.flycode.jasonfit.model.MealsType;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by victor on 3/23/17.
 */


public class MealTypeViewHolder extends ParentViewHolder {

    @BindView(R.id.meal_type) TextView mealTypeTextView;

    public MealTypeViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

    public void bind(MealsType mealsType) {
        mealTypeTextView.setText(mealsType.getName());
    }

}



