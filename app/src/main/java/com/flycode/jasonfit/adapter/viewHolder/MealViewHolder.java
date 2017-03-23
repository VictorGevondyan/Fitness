package com.flycode.jasonfit.adapter.viewHolder;

import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.flycode.jasonfit.R;
import com.flycode.jasonfit.model.Meal;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by victor on 3/23/17.
 */


public class MealViewHolder extends ChildViewHolder {

    @BindView(R.id.meal) TextView mealTextView;


    public MealViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);

    }

    public void bind(Meal meal) {
        mealTextView.setText(meal.name);
    }

}

