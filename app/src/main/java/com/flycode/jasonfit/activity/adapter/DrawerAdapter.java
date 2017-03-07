package com.flycode.jasonfit.activity.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flycode.jasonfit.R;

/**
 * Created on 2/2/17 __ makher .
 */

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.DrawerViewHolder> {

    public interface OnDrawerItemClickListener {
        void onDrawerItemClick(DrawerItems drawerItems);
    }

    public enum DrawerItems {
        WORKOUTS, MEALS, STATS, FOODS, SETTINGS, INFO
    }

    private static DrawerItems[] DRAWER_ITEMS = {
            DrawerItems.WORKOUTS,
            DrawerItems.MEALS,
            DrawerItems.STATS,
            DrawerItems.FOODS,
            DrawerItems.SETTINGS,
            DrawerItems.INFO
    };

    private DrawerItemData[] drawerItemData;
    private OnDrawerItemClickListener listener;

    public DrawerAdapter(DrawerItemData[] drawerItemData, OnDrawerItemClickListener listener) {
        this.drawerItemData = drawerItemData;
        this.listener = listener;
    }

    @Override
    public DrawerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_drawer, viewGroup, false);
        return new DrawerViewHolder(view, listener);
    }

    public void onBindViewHolder(DrawerViewHolder drawerViewHolder, int position) {
        drawerViewHolder.menuItemTitle.setText(drawerItemData[position].getMenuItemTitle());
    }

    @Override
    public int getItemCount() {
        return drawerItemData.length;
    }

    public static class DrawerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView menuItemTitle;
        private OnDrawerItemClickListener listener;

        public DrawerViewHolder(View itemView, OnDrawerItemClickListener listener) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.listener = listener;

            menuItemTitle = (TextView) itemView.findViewById(R.id.menu_item_title);
        }

        @Override
        public void onClick(View view) {
            listener.onDrawerItemClick(DRAWER_ITEMS[getAdapterPosition()]);
        }
    }

    public static class DrawerItemData {
        private int menuItemTitle;

        public DrawerItemData(int menuItemTitle) {
            this.menuItemTitle = menuItemTitle;
        }

        public int getMenuItemTitle() {
            return menuItemTitle;
        }
    }
}
