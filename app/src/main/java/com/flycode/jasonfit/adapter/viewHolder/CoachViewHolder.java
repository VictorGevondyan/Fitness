package com.flycode.jasonfit.adapter.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flycode.jasonfit.R;
import com.flycode.jasonfit.adapter.OnItemClickListener;
import com.flycode.jasonfit.model.Coach;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created - Schumakher on  8/31/17.
 */

public class CoachViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.coach_name_text_view) TextView nameTextView;
    @BindView(R.id.coach_email_text_view) TextView emailTextView;

    private OnItemClickListener listener;

    public static CoachViewHolder initialize(ViewGroup parent, OnItemClickListener listener) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coach, parent, false);

        return new CoachViewHolder(view, listener);
    }

    private CoachViewHolder(View itemView, OnItemClickListener listener) {
        super(itemView);

        ButterKnife.bind(this, itemView);

        this.listener = listener;
    }

    @OnClick(R.id.container)
    public void onContainerClickListener() {
        listener.onItemClick(this);
    }

    public void setup(Coach coach) {

        emailTextView.setVisibility(View.GONE);

        nameTextView.setText(coach.getName());
        emailTextView.setText(coach.getEmail());
    }
}
