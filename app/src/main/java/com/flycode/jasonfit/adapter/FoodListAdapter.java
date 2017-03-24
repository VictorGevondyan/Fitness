package com.flycode.jasonfit.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.flycode.jasonfit.adapter.viewHolder.FoodItemViewHolder;
import com.flycode.jasonfit.model.Food;

import java.util.List;

/**
 * Created by acerkinght on 3/11/17.
 */

public class FoodListAdapter extends RecyclerView.Adapter<FoodItemViewHolder> implements OnItemClickListener {

    private List<Food> foodList;

    private FoodListAdapter.OnFoodItemClickListener listener;

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

    public void setItems( List<Food> itemsList ){
        this.foodList = itemsList;
        notifyDataSetChanged();
    }

}
