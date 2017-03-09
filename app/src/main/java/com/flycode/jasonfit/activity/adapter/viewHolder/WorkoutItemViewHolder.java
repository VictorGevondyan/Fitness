package com.flycode.jasonfit.activity.adapter.viewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flycode.jasonfit.R;
import com.flycode.jasonfit.activity.adapter.OnItemClickListener;
import com.flycode.jasonfit.activity.model.Workout;

import org.zakariya.stickyheaders.SectioningAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created - Schumakher on  3/9/17.
 */

public class WorkoutItemViewHolder extends SectioningAdapter.ItemViewHolder {
    @BindView(R.id.workout_title) TextView titleTextView;

    private OnItemClickListener listener;

    public static WorkoutItemViewHolder initialize(ViewGroup parent, OnItemClickListener listener) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout, parent, false);
        return new WorkoutItemViewHolder(view, listener);
    }

    private WorkoutItemViewHolder(View itemView, OnItemClickListener listener) {
        super(itemView);

        ButterKnife.bind(this, itemView);

        this.listener = listener;
    }

    @OnClick(R.id.workout_card_view)
    public void onClick() {
        listener.onItemClick(this);
    }

    public void setupItem(Workout workout) {
        titleTextView.setText(workout.getName());
    }
}
