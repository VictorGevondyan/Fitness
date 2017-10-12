package com.flycode.jasonfit.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flycode.jasonfit.R;
import com.flycode.jasonfit.adapter.CoachListAdapter;
import com.flycode.jasonfit.model.Coach;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created - Schumakher on  8/31/17.
 */
public class CoachListFragment extends Fragment implements CoachListAdapter.OnCoachItemClickListener {

    @BindView(R.id.coach_recycler) RecyclerView coachRecycler;

    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coach_list, container, false);

        unbinder = ButterKnife.bind(this, view);

        ArrayList<Coach> coaches = new ArrayList<>();

        Coach coach = new Coach();
        coach.setName("Vazgen Bagratuni");
        coach.setEmail("vazgen.bagratuni@yopmail.com");

        coaches.add(coach);

//        Coach coach1 = new Coach();
//        coach1.setName("Ashot II Bagratuni");
//        coach1.setEmail("ashotBagratuni914@yopmail.com");
//
//        coaches.add(coach1);

        coachRecycler.setAdapter(new CoachListAdapter(coaches, this));
        coachRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (coaches.size() == 1) {
            onCoachItemClick(coaches.get(0));
        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unbinder.unbind();
    }

    @Override
    public void onCoachItemClick(Coach coach) {

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto",coach.getEmail(), null));

        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, coach.getName());

        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }
}
