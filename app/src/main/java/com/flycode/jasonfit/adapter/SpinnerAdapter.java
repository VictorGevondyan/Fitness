package com.flycode.jasonfit.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.flycode.jasonfit.R;
import com.flycode.jasonfit.adapter.viewHolder.FoodItemViewHolder;
import com.flycode.jasonfit.adapter.viewHolder.SpinnerViewHolder;
import com.flycode.jasonfit.model.Food;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victor on 3/23/17.
 */


public class SpinnerAdapter extends ArrayAdapter<String> implements OnItemClickListener {
    
    private List<String> correspondMealsList;
    private SpinnerAdapter.OnSpinnerItemClickListener listener;

    public SpinnerAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> correspondMealsList) {
        super(context, resource, correspondMealsList);
        this.correspondMealsList = correspondMealsList;
    }

    public int getItemCount() {
        return correspondMealsList.size();
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder) {
        listener.onSpinnerItemClick(correspondMealsList.get(viewHolder.getAdapterPosition()));
    }

    public interface OnSpinnerItemClickListener {
        void onSpinnerItemClick(String correspondingMeal);
    }

    public void setItems(ArrayList<String> itemsList) {
        this.correspondMealsList = itemsList;
        notifyDataSetChanged();
    }

}


