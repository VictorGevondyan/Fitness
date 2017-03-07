package com.flycode.jasonfit.activity.activity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.flycode.jasonfit.R;
import com.flycode.jasonfit.activity.adapter.DrawerAdapter;

public class MainActivity extends AppCompatActivity implements DrawerAdapter.OnDrawerItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        processDrawer();
    }

    @Override
    public void onDrawerItemClick(DrawerAdapter.DrawerItems drawerItems) {

    }

    private void processDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.parent_drawer);
        RecyclerView drawerRecycler = (RecyclerView) findViewById(R.id.drawer_recycler);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        drawerRecycler.setAdapter(new DrawerAdapter(new DrawerAdapter.DrawerItemData[] {
                new DrawerAdapter.DrawerItemData(R.string.workouts),
                new DrawerAdapter.DrawerItemData(R.string.meals),
                new DrawerAdapter.DrawerItemData(R.string.stats),
                new DrawerAdapter.DrawerItemData(R.string.foods),
                new DrawerAdapter.DrawerItemData(R.string.settings),
                new DrawerAdapter.DrawerItemData(R.string.info),
        }, this));

        drawerRecycler.setLayoutManager(new LinearLayoutManager(this));
    }
}
