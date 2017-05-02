package com.flycode.jasonfit.adapter.viewHolder;

import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.flycode.jasonfit.R;
import com.flycode.jasonfit.model.MealType;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by victor on 3/23/17.
 */


public class MealHeaderViewHolder extends ParentViewHolder {

    @BindView(R.id.meal_type) TextView mealTypeTextView;

    public MealHeaderViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

    public void bind(MealType mealsType) {
        mealTypeTextView.setText(mealsType.getName().toUpperCase(Locale.US));
    }
}



