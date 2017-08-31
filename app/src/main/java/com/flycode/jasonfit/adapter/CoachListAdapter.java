package com.flycode.jasonfit.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.flycode.jasonfit.adapter.viewHolder.CoachViewHolder;
import com.flycode.jasonfit.model.Coach;

import java.util.ArrayList;

/**
 * Created - Schumakher on  8/31/17.
 */

public class CoachListAdapter extends RecyclerView.Adapter<CoachViewHolder> implements OnItemClickListener {

    private ArrayList<Coach> coaches;
    private OnCoachItemClickListener listener;

    public CoachListAdapter(ArrayList<Coach> coaches, OnCoachItemClickListener listener) {
        this.coaches = coaches;
        this.listener = listener;
    }
    @Override
    public CoachViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return CoachViewHolder.initialize(parent, this);
    }

    @Override
    public void onBindViewHolder(CoachViewHolder holder, int position) {
        holder.setup(coaches.get(position));
    }

    @Override
    public int getItemCount() {
        return coaches.size();
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder) {
        listener.onCoachItemClick(coaches.get(viewHolder.getAdapterPosition()));
    }

    public interface OnCoachItemClickListener {
        void onCoachItemClick(Coach coach);
    }


}
