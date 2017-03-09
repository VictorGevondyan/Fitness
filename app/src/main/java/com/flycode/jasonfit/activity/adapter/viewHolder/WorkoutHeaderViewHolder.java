package com.flycode.jasonfit.activity.adapter.viewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flycode.jasonfit.R;

import org.zakariya.stickyheaders.SectioningAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created - Schumakher on  3/9/17.
 */

public class WorkoutHeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
    @BindView(R.id.workout_header_title) TextView titleTextView;

    public static WorkoutHeaderViewHolder initialize (ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_layout, parent, false);
        return new WorkoutHeaderViewHolder(view);
    }

    private WorkoutHeaderViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

    public void setupHeader(int sectionIndex) {
        if (sectionIndex == 0) {
            titleTextView.setText("Reccomended for today");
        } else if (sectionIndex == 1) {
            titleTextView.setText("All other workouts");
        }
    }
}
