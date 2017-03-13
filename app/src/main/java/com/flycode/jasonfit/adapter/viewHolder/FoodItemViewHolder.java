package com.flycode.jasonfit.adapter.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flycode.jasonfit.R;
import com.flycode.jasonfit.adapter.OnItemClickListener;
import com.flycode.jasonfit.model.Food;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by acerkinght on 3/11/17.
 */

public class FoodItemViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.title) TextView titleTextView;

    private OnItemClickListener listener;

    public static FoodItemViewHolder initialize(ViewGroup parent, OnItemClickListener listener) {
        View itemView = View.inflate(parent.getContext(), R.layout.item_food, null);
        return new FoodItemViewHolder(itemView, listener);
    }

    private FoodItemViewHolder(View itemView, OnItemClickListener listener) {
        super(itemView);

        ButterKnife.bind(this, itemView);

        this.listener = listener;
    }

    @OnClick(R.id.container)
    public void onClick() {
        listener.onItemClick(this);
    }

    public void setup(Food food) {
        titleTextView.setText(food.food);
    }
}
