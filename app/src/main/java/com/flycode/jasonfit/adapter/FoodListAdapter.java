package com.flycode.jasonfit.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.flycode.jasonfit.adapter.viewHolder.FoodItemViewHolder;
import com.flycode.jasonfit.model.Food;

import java.util.List;

/**
 * Created - acerkinght on  3/11/17.
 */

public class FoodListAdapter extends RecyclerView.Adapter<FoodItemViewHolder> implements OnItemClickListener, FoodItemViewHolder.OnAddItemClickListener {

    private List<Food> foodList;

    private FoodListAdapter.OnFoodItemClickListener listener;
    private OnAddFoodItemClickListener onAddFoodItemClickListener;

    public FoodListAdapter(List<Food> foodList, OnFoodItemClickListener listener, OnAddFoodItemClickListener onAddFoodItemClickListener) {
        this.foodList = foodList;
        this.listener = listener;
        this.onAddFoodItemClickListener = onAddFoodItemClickListener;
    }

    @Override
    public FoodItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return FoodItemViewHolder.initialize(parent, this, this);
    }

    @Override
    public void onBindViewHolder(FoodItemViewHolder holder, int position) {
        holder.setup(foodList.get(position));
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public void setItems( List<Food> itemsList ){
        this.foodList = itemsList;
        notifyDataSetChanged();
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder) {
        listener.onFoodItemClick(foodList.get(viewHolder.getAdapterPosition()));
    }

    @Override
    public void onAddFoodClick(RecyclerView.ViewHolder viewHolder) {
//        foodList.get(viewHolder.getAdapterPosition()).count++;
//        notifyItemChanged(viewHolder.getAdapterPosition());
        onAddFoodItemClickListener.onAddFoodItemClickListener(viewHolder);
    }

    @Override
    public void onRemoveFoodClick(RecyclerView.ViewHolder viewHolder) {

//        Food food = foodList.get(viewHolder.getAdapterPosition());
//
//        if (food.count == 0) {
//            return;
//        }
//
//        food.count--;
//        notifyItemChanged(viewHolder.getAdapterPosition());
//        onAddFoodItemClickListener.onRemoveFoodItemClickListener(food);
    }

    public interface OnFoodItemClickListener {
        void onFoodItemClick(Food food);
    }

    public interface OnAddFoodItemClickListener {
        void onAddFoodItemClickListener(RecyclerView.ViewHolder viewHolder);
        void onRemoveFoodItemClickListener(Food food);
    }
}