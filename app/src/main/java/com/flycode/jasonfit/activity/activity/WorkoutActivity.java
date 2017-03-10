package com.flycode.jasonfit.activity.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flycode.jasonfit.R;
import com.flycode.jasonfit.activity.model.Workout;
import com.flycode.jasonfit.activity.model.WorkoutTimerService;
import com.flycode.jasonfit.activity.model.WorkoutTrack;
import com.flycode.jasonfit.activity.model.WorkoutTrackPreferences;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.flycode.jasonfit.activity.model.WorkoutTimerService.WORKOUT_BROADCAST_IDENTIFIER;

public class WorkoutActivity extends AppCompatActivity {
    @BindView(R.id.title) TextView workoutTitle;
    @BindView(R.id.workout_species_title) TextView workoutSpeciesTitle;
    @BindView(R.id.workout_image_view) ImageView workoutImageView;
    @BindView(R.id.workout_time_current) TextView workoutTimeCurrent;
    @BindView(R.id.workout_time_estimated) TextView workoutTimeEstimated;
    @BindView(R.id.workout_rounded_button) Button workoutRoundedButton;

    private WorkoutTrackPreferences preferences;
    private IntentFilter intentFilter;
    private int setSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        ButterKnife.bind(this);

        preferences = WorkoutTrack.sharedPreferences(this);
        intentFilter = new IntentFilter(WORKOUT_BROADCAST_IDENTIFIER);

        preferences.edit().putTotalWorkoutTime(0).apply();

        Workout workout = (Workout) getIntent().getSerializableExtra("CURRENT_WORKOUT");
        //setSize = workout.getSetName().size();
        setSize = 3;

        setupView(workout);
    }

    @OnClick(R.id.workout_rounded_button)
    public void startStopTimerClick() {

        if (preferences.get().status().equals("started") || preferences.get().status().equals("")) {
            stopService(new Intent(this, WorkoutTimerService.class));
            preferences.edit().putStatus("stopped").apply();
        } else if (preferences.get().status().equals("stopped")) {
            startService(new Intent(this, WorkoutTimerService.class));
            preferences.edit().putStatus("started").apply();
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            incrementCurrentTime();
        }
    };

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onPause();
    }

    private void setupView(Workout workout) {
        workoutTitle.setText(workout.getName());
        workoutTimeCurrent.setText(getResources().getString(R.string.workout_time_null));
        workoutTimeEstimated.setText(getResources().getString(R.string.workout_time_null));

        processWorkoutTimeEstimated(workout);
    }

    private void processWorkoutTimeEstimated(Workout workout) {
        String estimatedTimeString;

        int estimatedTimeHours = 0;
        int estimatedTimeMins = 0;
        int estimatedTimeSecs = 0;

        for (int i = 0; i <= setSize; i++) {

            String time = workout.getSetTiming().get(i);

            String[] split = time.split(":");
            if (split.length == 3) {
                estimatedTimeHours += Integer.parseInt(split[0].trim());
                estimatedTimeMins += Integer.parseInt(split[1].trim());
                estimatedTimeSecs += Integer.parseInt(split[2].trim());
            } else if (split.length == 2) {
                estimatedTimeMins += Integer.parseInt(split[0].trim());
                estimatedTimeSecs += Integer.parseInt(split[1].trim());
            } else if (split.length == 1) {
                estimatedTimeSecs += Integer.parseInt(split[0].trim());
            }
        }

        estimatedTimeMins += estimatedTimeSecs / 60;
        estimatedTimeSecs = estimatedTimeSecs % 60;
        estimatedTimeHours += estimatedTimeMins / 60;
        estimatedTimeMins = estimatedTimeMins % 60;

        String estimatedTimeHoursString;
        String estimatedTimeMinsString;
        String estimatedTimeSecsString;

        if (estimatedTimeHours < 10) {
            estimatedTimeHoursString = "0" + String.valueOf(estimatedTimeHours);
        } else {
            estimatedTimeHoursString = String.valueOf(estimatedTimeHours);
        }

        if (estimatedTimeMins < 10) {
            estimatedTimeMinsString = "0" + String.valueOf(estimatedTimeMins);
        } else {
            estimatedTimeMinsString = String.valueOf(estimatedTimeMins);
        }

        if (estimatedTimeSecs < 10) {
            estimatedTimeSecsString = "0" + String.valueOf(estimatedTimeSecs);
        } else {
            estimatedTimeSecsString = String.valueOf(estimatedTimeSecs);
        }

        if (estimatedTimeHours != 0) {
            estimatedTimeString = estimatedTimeHoursString + ":" + estimatedTimeMinsString + ":" + estimatedTimeSecsString;
        } else if (estimatedTimeMins != 0) {
            estimatedTimeString = estimatedTimeMinsString + ":" + estimatedTimeSecsString;
        } else {
            estimatedTimeString = estimatedTimeSecsString;
        }

        workoutTimeEstimated.setText(estimatedTimeString);
    }

    private void incrementCurrentTime() {
        int currentTimeHours;
        int currentTimeMins;
        int currentTimeSecs;

        String currentTimeString;

        currentTimeHours = (int) (preferences.get().totalWorkoutTime() / 1000 / 60 / 60);
        currentTimeMins = (int) ((preferences.get().totalWorkoutTime() / 1000 / 60) % 60);
        currentTimeSecs = (int) ((preferences.get().totalWorkoutTime() / 1000) % 60);

        String currentTimeHoursString;
        String currentTimeMinsString;
        String currentTimeSecsString;

        if (currentTimeHours < 10) {
            currentTimeHoursString = "0" + String.valueOf(currentTimeHours);
        } else {
            currentTimeHoursString = String.valueOf(currentTimeHours);
        }

        if (currentTimeMins < 10) {
            currentTimeMinsString = "0" + String.valueOf(currentTimeMins);
        } else {
            currentTimeMinsString = String.valueOf(currentTimeMins);
        }

        if (currentTimeSecs < 10) {
            currentTimeSecsString = "0" + String.valueOf(currentTimeSecs);
        } else {
            currentTimeSecsString = String.valueOf(currentTimeSecs);
        }


        if (currentTimeHours != 0) {
            currentTimeString = currentTimeHoursString + ":" + currentTimeMinsString + ":" + currentTimeSecsString;
        } else if (currentTimeMins != 0){
            currentTimeString = currentTimeMinsString + ":" + currentTimeSecsString;
        } else {
            currentTimeString = "00:" + currentTimeSecsString;
        }

        workoutTimeCurrent.setText(currentTimeString);
    }
}
