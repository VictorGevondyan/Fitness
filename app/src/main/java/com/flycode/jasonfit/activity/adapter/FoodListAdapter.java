package com.flycode.jasonfit.activity.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.flycode.jasonfit.activity.adapter.viewHolder.FoodItemViewHolder;
import com.flycode.jasonfit.activity.model.Food;

import java.util.List;

/**
 * Created by acerkinght on 3/11/17.
 */

public class FoodListAdapter extends RecyclerView.Adapter<FoodItemViewHolder> implements OnItemClickListener {
    private List<Food> foodList;
    private OnFoodItemClickListener listener;

    public FoodListAdapter(List<Food> foodList, OnFoodItemClickListener listener) {
        this.foodList = foodList;
        this.listener = listener;
    }

    @Override
    public FoodItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return FoodItemViewHolder.initialize(parent, this);
    }

    @Override
    public void onBindViewHolder(FoodItemViewHolder holder, int position) {
        holder.setup(foodList.get(position));
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder) {
        listener.onFoodItemClick(foodList.get(viewHolder.getAdapterPosition()));
    }

    public interface OnFoodItemClickListener {
        void onFoodItemClick(Food food);
    }
}
