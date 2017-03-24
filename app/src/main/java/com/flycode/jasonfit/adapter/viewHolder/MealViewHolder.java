package com.flycode.jasonfit.adapter.viewHolder;

import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.flycode.jasonfit.R;
import com.flycode.jasonfit.adapter.OnItemClickListener;
import com.flycode.jasonfit.model.Meal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by victor on 3/23/17.
 */


public class MealViewHolder extends ChildViewHolder {

    @BindView(R.id.meal) TextView mealTextView;

    private OnItemClickListener listener;

    public MealViewHolder(View itemView, OnItemClickListener listener) {
        super(itemView);

        ButterKnife.bind(this, itemView);

        this.listener = listener;

    }

    public void bind(Meal meal) {
        mealTextView.setText(meal.name);
    }

    @OnClick(R.id.meal_container)
    public void onClick() {
        listener.onItemClick(this);
    }

}

