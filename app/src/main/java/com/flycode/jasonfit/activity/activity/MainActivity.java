package com.flycode.jasonfit.activity.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;

import com.flycode.jasonfit.R;
import com.flycode.jasonfit.activity.adapter.SideMenuAdapter;
import com.flycode.jasonfit.activity.fragment.FoodsFragment;
import com.flycode.jasonfit.activity.fragment.InfoFragment;
import com.flycode.jasonfit.activity.fragment.MealsFragment;
import com.flycode.jasonfit.activity.fragment.StatsFragment;
import com.flycode.jasonfit.activity.fragment.WorkoutsFragment;
import com.flycode.jasonfit.activity.fragment.SettingsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SideMenuAdapter.OnSideMenuClickListener {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer) DrawerLayout drawerLayout;
    @BindView(R.id.side_menu) RecyclerView sideMenuRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        processDrawer();

        FragmentTransaction settingsTransaction = getFragmentManager().beginTransaction();
        settingsTransaction.replace(R.id.container, new WorkoutsFragment());
        settingsTransaction.commit();
    }

    @Override
    public void onSideMenuItemClick(SideMenuAdapter.SideMenuItem sideMenuItem) {
        Fragment fragment = null;

        switch (sideMenuItem) {
            case WORKOUTS:
                fragment = new WorkoutsFragment();
                break;
            case MEALS:
                fragment = new MealsFragment();
                break;
            case STATS:
                fragment = new StatsFragment();
                break;
            case FOODS:
                fragment = new FoodsFragment();
                break;
            case SETTINGS:
                fragment = new SettingsFragment();
                break;
            case INFO:
                fragment = new InfoFragment();
                break;
        }

        FragmentTransaction settingsTransaction = getFragmentManager().beginTransaction();
        settingsTransaction.replace(R.id.container, fragment);
        settingsTransaction.commit();

        drawerLayout.closeDrawer(Gravity.START);
    }

    private void processDrawer() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        sideMenuRecyclerView.setAdapter(new SideMenuAdapter(this));
        sideMenuRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
