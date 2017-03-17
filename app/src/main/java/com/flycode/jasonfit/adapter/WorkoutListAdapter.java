package com.flycode.jasonfit.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.flycode.jasonfit.adapter.viewHolder.WorkoutHeaderViewHolder;
import com.flycode.jasonfit.adapter.viewHolder.WorkoutItemViewHolder;
import com.flycode.jasonfit.model.Workout;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created - Schumakher on  3/9/17.
 */

public class WorkoutListAdapter extends SectioningAdapter implements OnItemClickListener {
    private List<Workout> todayWorkouts;
    private List<Workout> otherWorkouts;

    public interface OnWorkoutItemClickListener {
        void onWorkoutItemClick(Workout workout);
    }

    private OnWorkoutItemClickListener listener;

    public WorkoutListAdapter(List<Workout> todayWorkouts, List<Workout> otherWorkouts, OnWorkoutItemClickListener listener) {
        this.todayWorkouts = todayWorkouts;
        this.otherWorkouts = otherWorkouts;
        this.listener = listener;
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return WorkoutItemViewHolder.initialize(parent, this);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerUserType) {
        return WorkoutHeaderViewHolder.initialize(parent);
    }

    @Override
    public void onBindItemViewHolder(ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemUserType) {
        Workout workout = null;

        if (sectionIndex == 0) {
            workout = todayWorkouts.get(itemIndex);
        } else if (sectionIndex == 1) {
            workout = otherWorkouts.get(itemIndex);
        }

        WorkoutItemViewHolder workoutItemViewHolder = (WorkoutItemViewHolder) viewHolder;
        workoutItemViewHolder.setupItem(workout);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder viewHolder, int sectionIndex, int headerUserType) {
        WorkoutHeaderViewHolder workoutHeaderViewHolder = (WorkoutHeaderViewHolder) viewHolder;
        workoutHeaderViewHolder.setupHeader(sectionIndex);
    }

    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        return true;
    }

    @Override
    public int getNumberOfSections() {
        return 2;
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        if (sectionIndex == 0) {
            return todayWorkouts.size();
        } else if (sectionIndex == 1) {
            return otherWorkouts.size();
        }

        return 0;
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
        if (itemViewHolder.getSection() == 0) {
            listener.onWorkoutItemClick(todayWorkouts.get(itemViewHolder.getPositionInSection()));
        } else if (itemViewHolder.getSection() == 1) {
            listener.onWorkoutItemClick(otherWorkouts.get(itemViewHolder.getPositionInSection()));
        }

    }
}