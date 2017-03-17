package com.flycode.jasonfit.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;

import com.flycode.jasonfit.R;
import com.flycode.jasonfit.adapter.SideMenuAdapter;
import com.flycode.jasonfit.fragment.FoodListFragment;
import com.flycode.jasonfit.fragment.InfoFragment;
import com.flycode.jasonfit.fragment.MealsFragment;
import com.flycode.jasonfit.fragment.SettingsFragment;
import com.flycode.jasonfit.fragment.StatsFragment;
import com.flycode.jasonfit.fragment.WorkoutListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SideMenuAdapter.OnSideMenuClickListener {
    private static final String EXTRA_MENU_ITEM = "extraMenuItem";

    @BindView(R.id.side_menu_container) View sideMenuView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer) DrawerLayout drawerLayout;
    @BindView(R.id.side_menu) RecyclerView sideMenuRecyclerView;

    private SideMenuAdapter.SideMenuItem currentMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        processDrawer();

        currentMenuItem = SideMenuAdapter.SideMenuItem.WORKOUTS;

        if (savedInstanceState != null) {
            String currentMenuItemName = savedInstanceState.getString(EXTRA_MENU_ITEM, SideMenuAdapter.SideMenuItem.WORKOUTS.name());
            currentMenuItem = SideMenuAdapter.SideMenuItem.valueOf(currentMenuItemName);
        }

        FragmentTransaction settingsTransaction = getFragmentManager().beginTransaction();
        settingsTransaction.replace(R.id.container, getFragmentForSideMenuItem(currentMenuItem));
        settingsTransaction.commit();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        DrawerLayout.LayoutParams layoutParams = (DrawerLayout.LayoutParams) sideMenuView.getLayoutParams();
        layoutParams.width = metrics.widthPixels;
        sideMenuView.setLayoutParams(layoutParams);
        drawerLayout.openDrawer(GravityCompat.START, false);

        if (getIntent().getBooleanExtra("FROM_WORKOUT", false)) {
            currentMenuItem = SideMenuAdapter.SideMenuItem.STATS;
            settingsTransaction.replace(R.id.container, getFragmentForSideMenuItem(currentMenuItem));
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_MENU_ITEM, currentMenuItem.name());
    }

    @Override
    public void onSideMenuItemClick(SideMenuAdapter.SideMenuItem sideMenuItem) {
        currentMenuItem = sideMenuItem;

        Fragment fragment = getFragmentForSideMenuItem(sideMenuItem);

        FragmentTransaction settingsTransaction = getFragmentManager().beginTransaction();
        settingsTransaction.replace(R.id.container, fragment);
        settingsTransaction.commit();

        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private Fragment getFragmentForSideMenuItem(SideMenuAdapter.SideMenuItem sideMenuItem) {
        switch (sideMenuItem) {
            case WORKOUTS:
                return new WorkoutListFragment();
            case MEALS:
                return new MealsFragment();
            case STATS:
                return new StatsFragment();
            case FOODS:
                return new FoodListFragment();
            case SETTINGS:
                return new SettingsFragment();
            case INFO:
                return new InfoFragment();
            default:
                return new WorkoutListFragment();
        }
    }

    private void processDrawer() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        drawerToggle.setHomeAsUpIndicator(R.drawable.calendar_prev_arrow);

        sideMenuRecyclerView.setAdapter(new SideMenuAdapter(this));
        sideMenuRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
