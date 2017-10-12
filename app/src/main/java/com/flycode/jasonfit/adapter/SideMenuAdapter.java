package com.flycode.jasonfit.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.flycode.jasonfit.adapter.viewHolder.SideMenuItemViewHolder;

/**
 * Created - Schumakher on  3/7/17.
 */

public class SideMenuAdapter extends RecyclerView.Adapter<SideMenuItemViewHolder> implements OnItemClickListener {

    public interface OnSideMenuClickListener {
        void onSideMenuItemClick(SideMenuItem sideMenuItem);
    }

    public enum SideMenuItem {
        WORKOUTS, MEALS, STATS, FOODS, COACHING, SETTINGS, INFO
    }

    private OnSideMenuClickListener listener;

    public SideMenuAdapter(OnSideMenuClickListener listener) {
        this.listener = listener;
    }

    @Override
    public SideMenuItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return SideMenuItemViewHolder.initialize(viewGroup, this);
    }

    public void onBindViewHolder(SideMenuItemViewHolder sideMenuItemViewHolder, int position) {
        sideMenuItemViewHolder.setup(SideMenuItem.values()[position]);
    }

    @Override
    public int getItemCount() {
        return SideMenuItem.values().length;
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder) {
        listener.onSideMenuItemClick(SideMenuItem.values()[viewHolder.getAdapterPosition()]);
    }
}
