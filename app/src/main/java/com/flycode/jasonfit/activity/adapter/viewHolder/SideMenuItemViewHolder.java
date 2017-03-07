package com.flycode.jasonfit.activity.adapter.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flycode.jasonfit.R;
import com.flycode.jasonfit.activity.adapter.OnItemClickListener;
import com.flycode.jasonfit.activity.adapter.SideMenuAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created - Schumakher on     3/7/17.
 */

public class SideMenuItemViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.menu_item_title) TextView titleTextView;

    private OnItemClickListener listener;

    public static SideMenuItemViewHolder initialize(ViewGroup parent, OnItemClickListener listener) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_side_menu, parent, false);
        return new SideMenuItemViewHolder(view, listener);
    }

    private SideMenuItemViewHolder(View itemView, OnItemClickListener listener) {
        super(itemView);

        ButterKnife.bind(this, itemView);

        this.listener = listener;
    }

    @OnClick(R.id.container)
    public void onClick() {
        listener.onItemClick(this);
    }

    public void setup(SideMenuAdapter.SideMenuItem sideMenuItem) {
        switch (sideMenuItem) {
            case WORKOUTS:
                titleTextView.setText(R.string.workouts);
                break;

            case MEALS:
                titleTextView.setText(R.string.meals);
                break;

            case STATS:
                titleTextView.setText(R.string.stats);
                break;

            case FOODS:
                titleTextView.setText(R.string.foods);
                break;

            case SETTINGS:
                titleTextView.setText(R.string.settings);
                break;

            case INFO:
                titleTextView.setText(R.string.info);
                break;
        }
    }
}
