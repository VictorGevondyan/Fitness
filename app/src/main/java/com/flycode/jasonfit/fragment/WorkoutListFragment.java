package com.flycode.jasonfit.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flycode.jasonfit.R;
import com.flycode.jasonfit.activity.WorkoutActivity;
import com.flycode.jasonfit.adapter.WorkoutListAdapter;
import com.flycode.jasonfit.model.Workout;

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
        hulk.setName("HULK HITS");
        hulk.setId(1);
        hulk.setPicture("hulk.jpg");
        hulk.setSetTiming(new ArrayList<>(Arrays.asList("21:30", "1:01:30", "02: 40", "00 : 20")));
        hulk.setSetName(new ArrayList<>(Arrays.asList("punching bag", "running", "sparring", "breath")));
        hulk.setSetPicture(new ArrayList<>(Arrays.asList(R.drawable.hulk_bag)));

        todayWorkouts.add(hulk);

        Workout haunt = new Workout();
        haunt.setName("THE HAUNT");
        haunt.setId(2);
        haunt.setPicture("haunt.jpg");
        haunt.setSetTiming(new ArrayList<>(Arrays.asList("21:30", "01:35", "02: 40", ": 24")));
        haunt.setSetName(new ArrayList<>(Arrays.asList("running", "chasing", "more running", "breath")));
        haunt.setSetPicture(new ArrayList<>(Arrays.asList(R.drawable.haunt_run)));

        todayWorkouts.add(haunt);

        ArrayList<Workout> otherWorkouts = new ArrayList<>();

        Workout gladiator = new Workout();
        gladiator.setName("Gladiator CHASE");
        gladiator.setId(1);
        gladiator.setPicture("gladiator.jpg");
        gladiator.setSetTiming(new ArrayList<>(Arrays.asList("21:30", "06:30", "02: 40", "00 : 00")));
        gladiator.setSetName(new ArrayList<>(Arrays.asList("running in equipment", "sword practise", "sparring", "breath")));
        gladiator.setSetPicture(new ArrayList<>(Arrays.asList(R.drawable.gladiator_run)));

        otherWorkouts.add(gladiator);

        Workout outpark = new Workout();
        outpark.setName("OUTPARK WILD");
        outpark.setId(2);
        outpark.setPicture("outdoor.jpg");
        outpark.setSetTiming(new ArrayList<>(Arrays.asList("0:5", "00:5", "00: 5", "00 : 5")));
        outpark.setSetName(new ArrayList<>(Arrays.asList("climbing", "piss drinking", "in camel sleeping", "cliff jumping")));
        outpark.setSetPicture(new ArrayList<>(Arrays.asList(R.drawable.outpark_climb, R.drawable.outpark_piss,
                R.drawable.outpark_camel, R.drawable.outpark_cliff)));

        otherWorkouts.add(outpark);

        workoutsRecycler.setAdapter(new WorkoutListAdapter(todayWorkouts, otherWorkouts, this));
        workoutsRecycler.setLayoutManager(new StickyHeaderLayoutManager());
    }
}
