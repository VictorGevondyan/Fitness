package com.flycode.jasonfit.activity.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flycode.jasonfit.R;
import com.flycode.jasonfit.activity.model.Workout;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Timer;

import butterknife.BindView;

public class WorkoutActivity extends AppCompatActivity {
    @BindView(R.id.workout_title) TextView workoutTitle;
    @BindView(R.id.workout_species_title) TextView workoutSpeciesTitle;
    @BindView(R.id.workout_image_view) ImageView workoutImageView;
    @BindView(R.id.workout_time_current) TextView workoutTimeCurrent;
    @BindView(R.id.workout_time_estimated) TextView workoutTimeEstimated;
    @BindView(R.id.workout_rounded_button) Button workoutRoundedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        Workout workout = (Workout) getIntent().getSerializableExtra("CURRENT_WORKOUT");

        //setupView(workout);
    }

    private void setupView(Workout workout) {
        workoutTitle.setText(workout.getName());

        int setSize = workout.getSetName().size();
        String estimatedTime = "";

        int estimatedTimeMins = 0;
        int estimatedTimeSecs = 0;

        for (int i = 0; i <= setSize; i++) {

            String time = workout.getSetTiming().get(i);

//            estimatedTimeMins += Integer.getInteger(time.substring(0,1));
//            estimatedTimeSecs += Integer.getInteger(time.substring(Math.max(time.length() - 2, 0)));

            String[] split = time.split(":");
            estimatedTimeMins += Integer.getInteger(split[0]);
            estimatedTimeSecs += Integer.getInteger(split[1]);
        }

        estimatedTimeMins += (estimatedTimeSecs / 60);
        estimatedTimeSecs = estimatedTimeSecs % 60;
        estimatedTime = estimatedTimeMins + ":" + estimatedTimeSecs;

        workoutTimeEstimated.setText(estimatedTime);


        //workout.getSetTiming()


        for (int i = 0; i <= setSize; i++) {
           // Timer timer = new Timer()
        }
    }
}
