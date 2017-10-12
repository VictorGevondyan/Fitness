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
 * Created - acerkinght on  3/11/17.
 */

public class FoodItemViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.title) TextView titleTextView;
    @BindView(R.id.count_text_view) TextView countTextView;

    private OnItemClickListener listener;
    private OnAddItemClickListener onAddItemClickListener;

    public static FoodItemViewHolder initialize(ViewGroup parent, OnItemClickListener listener, OnAddItemClickListener onAddItemClickListener) {
        View itemView = View.inflate(parent.getContext(), R.layout.item_food, null);
        return new FoodItemViewHolder(itemView, listener, onAddItemClickListener);
    }

    private FoodItemViewHolder(View itemView, OnItemClickListener listener, OnAddItemClickListener onAddItemClickListener) {
        super(itemView);

        ButterKnife.bind(this, itemView);

        this.listener = listener;
        this.onAddItemClickListener = onAddItemClickListener;
    }

    @OnClick(R.id.container)
    public void onClick() {
        listener.onItemClick(this);
    }

    @OnClick(R.id.add_food_button)
    public void onAddButtonClickListener() {
        countTextView.setText(String.valueOf(Integer.valueOf(countTextView.getText().toString()) + 1));
        onAddItemClickListener.onAddFoodClick(this);
    }

    @OnClick(R.id.remove_food_button)
    public void onRemoveFoodButtonClickListener() {
        if (countTextView.getText().toString().equals("0")) {
            return;
        }

        countTextView.setText(String.valueOf(Integer.valueOf(countTextView.getText().toString()) - 1));
        onAddItemClickListener.onRemoveFoodClick(this);
    }

    public void setup(Food food) {
        titleTextView.setText(food.food);
    }

    public interface OnAddItemClickListener {
        void onAddFoodClick(RecyclerView.ViewHolder viewHolder);
        void onRemoveFoodClick(RecyclerView.ViewHolder viewHolder);
    }
}