package com.flycode.jasonfit.activity.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flycode.jasonfit.R;
import com.flycode.jasonfit.activity.activity.WorkoutActivity;
import com.flycode.jasonfit.activity.adapter.WorkoutListAdapter;
import com.flycode.jasonfit.activity.model.Workout;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import java.util.ArrayList;
import java.util.Arrays;

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
        intent.putExtra("CURRENT_WORKOUT", workout);

        startActivity(intent);
    }

    private void fillWorkoutSetAdapter() {
        ArrayList<Workout> todayWorkouts = new ArrayList<>();

        Workout hulk = new Workout();
        hulk.setName("hulk");
        hulk.setId(1);
        hulk.setSetTiming(new ArrayList<>(Arrays.asList("1:30",
                "1:01:30",
                "02: 40",
                "00 : 20")));

        todayWorkouts.add(hulk);

        Workout haunt = new Workout();
        haunt.setName("haunt");
        haunt.setId(2);
        haunt.setSetTiming(new ArrayList<>(Arrays.asList("1:30",
                "01:35",
                "02: 40",
                "0 : 24")));

        todayWorkouts.add(haunt);

        ArrayList<Workout> otherWorkouts = new ArrayList<>();

        Workout gladiator = new Workout();
        gladiator.setName("gladiator");
        gladiator.setId(1);
        gladiator.setSetTiming(new ArrayList<>(Arrays.asList("1:30",
                "06:30",
                "02: 40",
                "05 : 20")));

        otherWorkouts.add(gladiator);

        Workout outpark = new Workout();
        outpark.setName("outpark");
        outpark.setId(2);
        outpark.setSetTiming(new ArrayList<>(Arrays.asList("1:30",
                "01:30",
                "02: 40",
                "00 : 20")));

        otherWorkouts.add(outpark);

        workoutsRecycler.setAdapter(new WorkoutListAdapter(todayWorkouts, otherWorkouts, this));
        workoutsRecycler.setLayoutManager(new StickyHeaderLayoutManager());
    }
}
