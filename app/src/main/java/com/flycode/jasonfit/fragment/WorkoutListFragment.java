package com.flycode.jasonfit.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activeandroid.query.Select;
import com.flycode.jasonfit.Constants;
import com.flycode.jasonfit.R;
import com.flycode.jasonfit.activity.WorkoutActivity;
import com.flycode.jasonfit.adapter.WorkoutListAdapter;
import com.flycode.jasonfit.model.Workout;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created - Schumakher on  3/7/17.
 */

public class WorkoutListFragment extends Fragment implements WorkoutListAdapter.OnWorkoutItemClickListener {


    @BindView(R.id.workouts_recycler) RecyclerView workoutsRecycler;

    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View workoutsView = inflater.inflate(R.layout.fragment_workout_list, container, false);

        unbinder = ButterKnife.bind(this, workoutsView);

        fillWorkoutSetAdapter();

        return workoutsView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
    }

    @Override
    public void onWorkoutItemClick(Workout workout) {
        Intent intent = new Intent(getActivity(), WorkoutActivity.class);
        intent.putExtra(Constants.EXTRAS.CURRENT_WORKOUT, workout );

        startActivity(intent);
    }

    private void fillWorkoutSetAdapter() {
        List<Workout> todayWorkouts = new ArrayList<>();
        List<Workout> otherWorkouts = new ArrayList<>();
        List<Workout> allWorkouts = new Select()
                .from(Workout.class)
                .orderBy("id ASC")
                .execute();

        String todayString = null;
        int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        switch (today) {
            case Calendar.MONDAY:
                todayString = "MO";
                break;
            case Calendar.THURSDAY:
                todayString = "TH";
                break;
            case Calendar.WEDNESDAY:
                todayString = "WE";
                break;
            case Calendar.TUESDAY:
                todayString = "TU";
                break;
            case Calendar.FRIDAY:
                todayString = "FR";
                break;
            case Calendar.SATURDAY:
                todayString = "SA";
                break;
            case Calendar.SUNDAY:
                todayString = "SU";
                break;
            default:
                todayString = "NON_OF_THIS?";
        }

        for (Workout workout : allWorkouts) {
            if (workout.weekday.contains(todayString)) {
                todayWorkouts.add(workout);
            } else {
                otherWorkouts.add(workout);
            }
        }

        workoutsRecycler.setAdapter(new WorkoutListAdapter(todayWorkouts, otherWorkouts, this));
        workoutsRecycler.setLayoutManager(new StickyHeaderLayoutManager());

    }

}

