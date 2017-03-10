package com.flycode.jasonfit.activity.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
    @BindView(R.id.workout_title) TextView workoutTitle;
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

        startService(new Intent(this, WorkoutTimerService.class));
        preferences.edit().putStatus("started").apply();

        Workout workout = (Workout) getIntent().getSerializableExtra("CURRENT_WORKOUT");
        //setSize = workout.getSetName().size();
        setSize = 4;

        setupView(workout);
    }

    @OnClick(R.id.workout_rounded_button)
    public void startStopTimerClick() {

        if (preferences.get().status().equals("started")) {
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
            Log.i("TAGG", "broadcast");
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
        //processWorkoutTimeEstimated(workout);

        for (int i = 0; i <= setSize; i++) {
           // Timer timer = new Timer()
        }
    }

    private void processWorkoutTimeEstimated(Workout workout) {
        String estimatedTime = "";

        int estimatedTimeMins = 0;
        int estimatedTimeSecs = 0;

        for (int i = 0; i <= setSize; i++) {

            String time = workout.getSetTiming().get(i);

            String[] split = time.split(":");
            estimatedTimeMins += Integer.getInteger(split[0]);
            estimatedTimeSecs += Integer.getInteger(split[1]);
        }

        estimatedTimeMins += (estimatedTimeSecs / 60);
        estimatedTimeSecs = estimatedTimeSecs % 60;
        estimatedTime = estimatedTimeMins + ":" + estimatedTimeSecs;

        workoutTimeEstimated.setText(estimatedTime);
    }
}
