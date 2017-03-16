package com.flycode.jasonfit.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.flycode.jasonfit.JasonFitApplication;
import com.flycode.jasonfit.R;
import com.flycode.jasonfit.model.StatsData;
import com.flycode.jasonfit.model.User;
import com.flycode.jasonfit.model.UserPreferences;
import com.flycode.jasonfit.model.Workout;
import com.flycode.jasonfit.model.WorkoutTrack;
import com.flycode.jasonfit.model.WorkoutTrackPreferences;
import com.flycode.jasonfit.service.WorkoutTimerService;
import com.flycode.jasonfit.util.ImageUtil;
import com.flycode.jasonfit.util.MetricConverter;
import com.flycode.jasonfit.util.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.flycode.jasonfit.service.WorkoutTimerService.WORKOUT_BROADCAST_IDENTIFIER;

public class WorkoutActivity extends AppCompatActivity {

    @BindView(R.id.title) TextView workoutTitle;
    @BindView(R.id.title_background) ImageView workoutTitleBackground;
    @BindView(R.id.workout_species_title) TextView workoutSpeciesTitle;
    @BindView(R.id.workout_image_view) ImageView workoutImageView;
    @BindView(R.id.workout_time_current) TextView workoutTimeCurrent;
    @BindView(R.id.workout_time_estimated) TextView workoutTimeEstimated;
    @BindView(R.id.workout_rounded_button) Button workoutRoundedButton;
    @BindView(R.id.workout_progress) ProgressBar workoutProgress;
    @BindView(R.id.toolbar) Toolbar workoutToolbar;

    private WorkoutTrackPreferences workoutTrackPreferences;
    private IntentFilter intentFilter;
    private int setSize;
    private long estimatedTimeSecsFull;
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

        workoutTrackPreferences
                .edit()
                .putTotalWorkoutTime(0)
                .putSubWorkoutNumber(0)
                .putSubWorkoutTime(0)
                .apply();

        calculateSpeciesTimes();

        setupView();

        fillSetNamePreferences();
    }

    @OnClick(R.id.workout_rounded_button)
    public void startStopTimerClick() {
        String status = workoutTrackPreferences.get().totalWorkoutStatus();


        if (status.equals(WorkoutTrack.STATUS.RUNNING) || status.equals("")) {
//            stopService(new Intent(this, WorkoutTimerService.class));
            workoutTrackPreferences
                    .edit()
                    .putTotalWorkoutStatus(WorkoutTrack.STATUS.PAUSED)
                    .apply();

        } else if (status.equals(WorkoutTrack.STATUS.PAUSED) || status.equals(WorkoutTrack.STATUS.FINISHED) || status.equals(WorkoutTrack.STATUS.IDLE) || status.equals("")) {

            startService(new Intent(this, WorkoutTimerService.class));
            workoutTrackPreferences
                    .edit()
                    .putTotalWorkoutStatus(WorkoutTrack.STATUS.RUNNING)
                    .apply();

        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            decrementCurrentTime();
            redrawDependsWorkoutItem();

            checkForWorkoutEnd();
        }
    };

    /**
     * End of TextToSpeech flow methods
     */

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

        workoutSpeciesTitle.setText(workout.getSetName().get(0));
        workoutImageView.setImageResource(workout.getSetPicture().get(0));

        processWorkoutTimeEstimated();

        setupToolbar();

    }

    private void setupToolbar(){

        workoutToolbar.setTitle(workout.getName());
        setSupportActionBar(workoutToolbar);

        // This action bar is, in fact, the same workoutToolbar
        ActionBar workoutActionBar = getSupportActionBar();

        // This function call sets back icon as the home button icon
        workoutActionBar.setDisplayHomeAsUpEnabled(true);
        workoutActionBar.setDisplayShowHomeEnabled(true);
        workoutActionBar.setHomeButtonEnabled(true);
        workoutToolbar.setNavigationOnClickListener(backButtonClickListener);

    }

    View.OnClickListener backButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

    private void redrawDependsWorkoutItem() {
        int workoutNumber = workoutTrackPreferences.getSubWorkoutNumber();
        int currentWorkoutTime = (int) (workoutTrackPreferences.getSubWorkoutTime() / 1000);

        String speciesTitle = workout
                .getSetName()
                .get(workoutNumber);

        workoutProgress.setMax(workoutTrackPreferences.getCurrentWorkoutTimeArray().get(workoutNumber));
        workoutProgress.setProgress(currentWorkoutTime);

        workoutSpeciesTitle.setText(speciesTitle);
        workoutImageView.setImageResource(workout.getSetPicture().get(workoutNumber));
    }

    private void calculateSpeciesTimes() {

        ArrayList<Integer> arrayList = new ArrayList<>();

        for (int i = 0; i <= setSize; i++) {
            int speciesTimeHours = 0;
            int speciesTimeMins = 0;
            int speciesTimeSecs = 0;
            int speciesTimeSecsFull;

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

        workoutTimeEstimated.setText(StringUtil.getFormattedTime(estimatedTimeHours, estimatedTimeMins, estimatedTimeSecs));
    }

    private void decrementCurrentTime() {

        long totalWorkoutTime = workoutTrackPreferences
                .get()
                .totalWorkoutTime();

        int timeLeftSecsFull = (int) (estimatedTimeSecsFull - (totalWorkoutTime / 1000));

        if (timeLeftSecsFull >= 0) {
            int timeLeftMins = timeLeftSecsFull / 60;
            int timeLeftSecs = timeLeftSecsFull % 60;
            int timeLeftHours = timeLeftMins / 60;
            timeLeftMins = timeLeftMins % 60;

            workoutTimeCurrent.setText(StringUtil.getFormattedTime(timeLeftHours, timeLeftMins, timeLeftSecs));
        }
    }

    private void checkForWorkoutEnd() {

        String status = workoutTrackPreferences
                .getTotalWorkoutStatus();

        if (status.equals(WorkoutTrack.STATUS.FINISHED)) {

            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

            workoutTrackPreferences
                    .edit()
                    .putSubWorkoutTime(0)
                    .putTotalWorkoutTime(0)
                    .putSubWorkoutNumber(0)
                    .putTotalWorkoutStatus(WorkoutTrack.STATUS.FINISHED)
                    .apply();

            stopService(new Intent(WorkoutActivity.this, WorkoutTimerService.class));

            MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                    .content(getString(R.string.workout_congrats, formattedWeightMeasurement()))
                    .inputType(InputType.TYPE_CLASS_NUMBER)
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .alwaysCallInputCallback()
                    .input("", "", new MaterialDialog.InputCallback() {
                        @SuppressWarnings("ConstantConditions")
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            if (input.length() == 0) {
                                dialog.getInputEditText().setError(getString(R.string.please_enter_valid_height));
                                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                                return;
                            }

                            Intent goToStatsIntent = new Intent(WorkoutActivity.this, MainActivity.class);
                            goToStatsIntent.putExtra("FROM_WORKOUT", true);
                            startActivity(goToStatsIntent);
                            //TODO:save input in db

                            int weight;

                            try {
                                weight = Integer.valueOf(input.toString());
                            } catch (NumberFormatException e) {
                                e.printStackTrace();

                                dialog.getInputEditText().setError(getString(R.string.please_enter_valid_height));
                                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                                return;
                            }

                            if ( weight < 20 || weight > 200) {
                                dialog.getInputEditText().setError(getString(R.string.please_enter_valid_height));
                                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                                return;
                            }

                            saveWeight(MetricConverter.convertWeight(weight, User.sharedPreferences(WorkoutActivity.this).getWeightMeasurement(), true));

                            dialog.getInputEditText().setError(null);
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                        }

                    })
                    .onPositive(enterWeightOkCallback)
                    .show();

            materialDialog.getInputEditText().setError(null);
            materialDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
        }
    }

    MaterialDialog.SingleButtonCallback enterWeightOkCallback = new MaterialDialog.SingleButtonCallback() {

        @Override
        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

            workoutTrackPreferences
                    .edit()
                    .putTotalWorkoutStatus(WorkoutTrack.STATUS.IDLE)
                    .apply();

        }
    };

    @Override
    public void onBackPressed() {
        if (workoutTrackPreferences.getTotalWorkoutStatus().equals(WorkoutTrack.STATUS.RUNNING)) {

            new MaterialDialog.Builder(this)
                    .content(R.string.sure_go_back)
                    .positiveText(R.string.yes)
                    .negativeText(R.string.no)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            LocalBroadcastManager.getInstance(WorkoutActivity.this).unregisterReceiver(receiver);
                            stopService(new Intent(WorkoutActivity.this, WorkoutTimerService.class));

                            workoutTrackPreferences
                                    .edit()
                                    .putSubWorkoutTime(0)
                                    .putTotalWorkoutTime(0)
                                    .putSubWorkoutNumber(0)
                                    .putTotalWorkoutStatus(WorkoutTrack.STATUS.IDLE)
                                    .apply();

                            WorkoutActivity.super.onBackPressed();

                        }
                    })
                    .show();

        } else {
            super.onBackPressed();
        }
    }

    private void fillSetNamePreferences() {
        ArrayList<String> setNameArray = new ArrayList<>();

        for (int i = 0; i <= setSize; i++) {
            setNameArray.add(workout.getSetName().get(i));
        }

        workoutTrackPreferences
                .edit()
                .putCurrentWorkoutNameArray(setNameArray)
                .apply();
    }

    private void saveWeight(int weight) {
        Calendar calendar = Calendar.getInstance();

        int currentYear = calendar.get(Calendar.YEAR);
        int currentDay = calendar.get(Calendar.DAY_OF_YEAR);

        StatsData statsData = null;

        try {
            statsData = new Select()
                    .from(StatsData.class)
                    .where("year = ?", currentYear)
                    .where("dayOfYear = ?", currentDay)
                    .executeSingle();
        } catch (Exception ignored) {
        }

        if (statsData != null) {
            statsData.burntCalories += (double) (estimatedTimeSecsFull * 7 / 60);
            statsData.multiplier += 1;
        } else {
            statsData = new StatsData();
            statsData.burntCalories = (double) (estimatedTimeSecsFull * 7 / 60);
            statsData.year = currentYear;
            statsData.dayOfYear = currentDay;
            statsData.multiplier = 1;
        }

        statsData.weight = weight;

        User.sharedPreferences(JasonFitApplication.sharedApplication())
                .edit()
                .putWeight(statsData.weight)
                .apply();

        statsData.save();
    }

    private String formattedWeightMeasurement() {
        return getString(User.sharedPreferences(this).getWeightMeasurement().equals(User.METRICS.KG) ? R.string.kg : R.string.lbs);
    }
}
