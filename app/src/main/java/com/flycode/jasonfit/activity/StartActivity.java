package com.flycode.jasonfit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.activeandroid.query.Select;
import com.flycode.jasonfit.R;
import com.flycode.jasonfit.model.StatsData;
import com.flycode.jasonfit.model.User;
import com.flycode.jasonfit.model.UserPreferences;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartActivity extends AppCompatActivity {

    private UserPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ButterKnife.bind(this);

        userPreferences = User.sharedPreferences(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.try_for_free)
    public void onTryForFree() {

        float weight = userPreferences.getWeight();
        Calendar calendar = Calendar.getInstance();

        int currentYear = calendar.get(Calendar.YEAR);
        int currentDay = calendar.get(Calendar.DAY_OF_YEAR);

        StatsData statsData = new StatsData();
        // TODO: ask what to do here
        statsData.weight = weight;
        statsData.burntCalories = 0.0;
        statsData.year = currentYear;
        statsData.dayOfYear = currentDay;
        statsData.save();

        Intent mainActivityIntent = new Intent(this,MainActivity.class);
        startActivity(mainActivityIntent);

    }

}