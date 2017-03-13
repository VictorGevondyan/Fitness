package com.flycode.jasonfit.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flycode.jasonfit.R;
import com.flycode.jasonfit.model.WorkoutTrackPreferences;
import com.flycode.jasonfit.model.Workout;
import com.flycode.jasonfit.model.WorkoutTimerService;
import com.flycode.jasonfit.model.WorkoutTrack;
import com.flycode.jasonfit.util.ImageUtil;
import com.flycode.jasonfit.util.StringUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.flycode.jasonfit.model.WorkoutTimerService.WORKOUT_BROADCAST_IDENTIFIER;

public class WorkoutActivity extends AppCompatActivity {
    @BindView(R.id.title) TextView workoutTitle;
    @BindView(R.id.title_background) ImageView workoutTitleBackground;
    @BindView(R.id.workout_species_title) TextView workoutSpeciesTitle;
    @BindView(R.id.workout_image_view) ImageView workoutImageView;
    @BindView(R.id.workout_time_current) TextView workoutTimeCurrent;
    @BindView(R.id.workout_time_estimated) TextView workoutTimeEstimated;
    @BindView(R.id.workout_rounded_button) Button workoutRoundedButton;
    @BindView(R.id.workout_progress) ProgressBar workoutProgress;

    private WorkoutTrackPreferences workoutTrackPreferences;
    private IntentFilter intentFilter;
    private int setSize;
    private long estimatedTimeSecsFull;
    private String statusStopped;
    private String statusRunning;
    private String statusFinnished;
    private Workout workout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        ButterKnife.bind(this);

        workout = (Workout) getIntent().getSerializableExtra("CURRENT_WORKOUT");
        setSize = workout.getSetName().size() - 1;

        workoutTrackPreferences = WorkoutTrack.sharedPreferences(this);
        intentFilter = new IntentFilter(WORKOUT_BROADCAST_IDENTIFIER);

        statusStopped = getResources().getString(R.string.status_stopped);
        statusRunning = getResources().getString(R.string.status_running);
        statusFinnished = getResources().getString(R.string.status_finished);

        workoutTrackPreferences
                .edit()
                .putTotalWorkoutTime(0)
                .putWorkoutNumber(0)
                .putCurrentWorkoutTime(0)
                .apply();

        calculateSpeciesTimes();

        setupView();
    }

    @OnClick(R.id.workout_rounded_button)
    public void startStopTimerClick() {
        String status = workoutTrackPreferences.get().status();

        if (status.equals(statusRunning) || status.equals("")) {
            stopService(new Intent(this, WorkoutTimerService.class));
            workoutTrackPreferences
                    .edit()
                    .putStatus(statusStopped)
                    .apply();

        } else if (status.equals(statusStopped) || status.equals(statusFinnished) || status.equals("")) {

            startService(new Intent(this, WorkoutTimerService.class));
            workoutTrackPreferences
                    .edit()
                    .putStatus(statusRunning)
                    .apply();
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            incrementCurrentTime();
            redrawDependsWorkoutItem();
            checkForWorkoutEnd();
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

    private void setupView() {
        workoutTitle.setText(workout.getName());
        workoutTitleBackground.setImageBitmap(ImageUtil.getImageBitmap(this, workout));

        workoutTimeCurrent.setText(getResources().getString(R.string.workout_time_null));
        workoutTimeEstimated.setText(getResources().getString(R.string.workout_time_null));

        workoutSpeciesTitle.setText(workout.getSetName().get(0));
        workoutImageView.setImageResource(workout.getSetPicture().get(0));

        processWorkoutTimeEstimated();
    }

    private void redrawDependsWorkoutItem() {
        int workoutNumber = workoutTrackPreferences.getWorkoutNumber();

        String speciesTitle = workout
                .getSetName()
                .get(workoutNumber);

        workoutProgress.setMax(workoutTrackPreferences.getCurrentWorkoutTimeArray().get(workoutNumber));
        workoutProgress.setProgress((int) (workoutTrackPreferences.getCurrentWorkoutTime() / 1000));


        workoutSpeciesTitle.setText(speciesTitle);
        workoutImageView.setImageResource(workout.getSetPicture().get(workoutNumber));
    }

    private void calculateSpeciesTimes() {

        ArrayList<Integer> arrayList = new ArrayList<>();

        for (int i = 0; i <= setSize; i++) {
            int speciesTimeHours = 0;
            int speciesTimeMins = 0;
            int speciesTimeSecs = 0;
            int speciesTimeSecsFull = 0;

            String time = workout.getSetTiming().get(i);

            String[] split = time.split(":");

            switch (split.length) {
                default: speciesTimeSecs = 0;

                case 3:
                    if (!split[0].equals("")  && !split[1].equals("") && !split[2].equals("")) {
                        speciesTimeHours = Integer.parseInt(split[0].trim());
                        speciesTimeMins = Integer.parseInt(split[1].trim());
                        speciesTimeSecs = Integer.parseInt(split[2].trim());
                    }
                    break;

                case 2:
                    if (!split[0].equals("") && !split[1].equals("")) {
                        speciesTimeMins = Integer.parseInt(split[0].trim());
                        speciesTimeSecs = Integer.parseInt(split[1].trim());
                    }
                    break;

                case 1:
                    if (!split[0].equals("")) {
                        speciesTimeSecs = Integer.parseInt(split[0].trim());
                    }
                    break;
            }

            speciesTimeMins += speciesTimeSecs / 60;
            speciesTimeSecs = speciesTimeSecs % 60;
            speciesTimeHours += speciesTimeMins / 60;
            speciesTimeMins = speciesTimeMins % 60;

            speciesTimeSecsFull = speciesTimeHours * 60 * 60 + speciesTimeMins * 60 + speciesTimeSecs;
            arrayList.add(speciesTimeSecsFull);
        }

        workoutTrackPreferences
                .edit()
                .putCurrentWorkoutTimeArray(arrayList)
                .apply();
    }

    private void processWorkoutTimeEstimated() {

        for (int i = 0; i <= setSize; i++) {
            estimatedTimeSecsFull += workoutTrackPreferences
                                            .get()
                                            .currentWorkoutTimeArray()
                                            .get(i);
        }

        int estimatedTimeMins = (int) (estimatedTimeSecsFull / 60);
        int estimatedTimeSecs = (int) (estimatedTimeSecsFull % 60);
        int estimatedTimeHours = estimatedTimeMins / 60;
        estimatedTimeMins = estimatedTimeMins % 60;

        workoutTimeEstimated.setText(StringUtil.test(estimatedTimeHours, estimatedTimeMins, estimatedTimeSecs));
    }

    private void incrementCurrentTime() {

        long totalWorkoutTime = workoutTrackPreferences
                                            .get()
                                            .totalWorkoutTime();

        int currentTimeHours = (int) (totalWorkoutTime / 1000 / 60 / 60);

        int currentTimeMins = (int) ((totalWorkoutTime / 1000 / 60) % 60);

        int currentTimeSecs = (int) ((totalWorkoutTime / 1000) % 60);

        workoutTimeCurrent.setText(StringUtil.test(currentTimeHours, currentTimeMins, currentTimeSecs));
    }

    private void checkForWorkoutEnd() {
        int totalWorkoutTime = (int) (workoutTrackPreferences
                                                .get()
                                                .totalWorkoutTime() / 1000);

        if (totalWorkoutTime == estimatedTimeSecsFull) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

            workoutTrackPreferences
                    .edit()
                    .putCurrentWorkoutTime(0)
                    .putTotalWorkoutTime(0)
                    .putWorkoutNumber(0)
                    .putStatus(statusFinnished)
                    .apply();


            new MaterialDialog.Builder(this)
                    .content(R.string.workout_congrats)
                    .inputType(InputType.TYPE_CLASS_NUMBER)
                    .input(null, null , new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            // party
                        }
                    }).show();
        }
    }

    @Override
    public void onBackPressed() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        stopService(new Intent(this, WorkoutTimerService.class));
        workoutTrackPreferences
                .edit()
                .putCurrentWorkoutTime(0)
                .putTotalWorkoutTime(0)
                .putWorkoutNumber(0)
                .putStatus(statusStopped)
                .apply();

        super.onBackPressed();
    }
}
