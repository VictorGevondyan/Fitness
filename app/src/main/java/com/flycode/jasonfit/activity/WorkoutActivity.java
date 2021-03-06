package com.flycode.jasonfit.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.flycode.jasonfit.Constants;
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

import java.util.Calendar;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mbanje.kurt.fabbutton.FabButton;
import me.zhanghai.android.materialprogressbar.HorizontalProgressDrawable;

import static com.flycode.jasonfit.service.WorkoutTimerService.WORKOUT_BROADCAST_IDENTIFIER;

public class WorkoutActivity extends AppCompatActivity {
    @BindView(R.id.title) TextView workoutTitle;
    @BindView(R.id.title_background) ImageView workoutTitleBackground;
    @BindView(R.id.sub_workout_title) TextView subWorkoutTitle;
    @BindView(R.id.workout_image_view) ImageView workoutImageView;
    @BindView(R.id.workout_time_current) TextView workoutTimeCurrent;
    @BindView(R.id.workout_time_estimated) TextView workoutTimeEstimated;
    @BindView(R.id.pause_resume) Button workoutRoundedButton;
    @BindView(R.id.current_workout_progress) FabButton currentWorkoutProgress;
    @BindView(R.id.workout_progress) ProgressBar workoutProgress;
    @BindView(R.id.toolbar) Toolbar workoutToolbar;
    @BindView(R.id.complete) TextView completeTextView;
    @BindView(R.id.current) TextView currentTextView;

    private WorkoutTrackPreferences workoutTrackPreferences;
    private Workout workout;
    private int lastWorkoutNumber = -1;

    private UserPreferences userPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        ButterKnife.bind(this);

        workout = (Workout) getIntent().getSerializableExtra(Constants.EXTRAS.CURRENT_WORKOUT);

        userPreferences = User.sharedPreferences(JasonFitApplication.sharedApplication());

        workoutTrackPreferences = WorkoutTrack.sharedPreferences(this);

        workoutTrackPreferences
                .edit()
                .putWorkoutId(workout.id)
                .putTotalWorkoutTime(0)
                .putSubWorkoutNumber(0)
                .putSubWorkoutTime(0)
                .putSubWorkoutNames(workout.getSubNames())
                .putTiming(workout.getSubTiming())
                .apply();

        setupToolbar();
        preRenderWorkoutData();
        renderWorkoutData();

    }

    @OnClick(R.id.pause_resume)
    public void startStopTimerClick() {
        String status = workoutTrackPreferences.get().status();

        if (status.equals(WorkoutTrack.STATUS.RUNNING) || status.equals("")) {
            workoutTrackPreferences
                    .edit()
                    .putStatus(WorkoutTrack.STATUS.PAUSED)
                    .apply();
        } else if (status.equals(WorkoutTrack.STATUS.PAUSED) || status.equals(WorkoutTrack.STATUS.FINISHED) || status.equals(WorkoutTrack.STATUS.IDLE) || status.equals("")) {
            if (status.equals(WorkoutTrack.STATUS.FINISHED) || status.equals(WorkoutTrack.STATUS.IDLE)) {
                startService(new Intent(this, WorkoutTimerService.class));
            }

            workoutTrackPreferences
                    .edit()
                    .putStatus(WorkoutTrack.STATUS.RUNNING)
                    .apply();
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            renderWorkoutData();
            checkForWorkoutEnd();
        }

    };

    /**
     * End of TextToSpeech flow methods
     */

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(WORKOUT_BROADCAST_IDENTIFIER));
        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onPause();
    }

    private void setupToolbar(){
        setSupportActionBar(workoutToolbar);

        // We set minimum height of toolbar the same, as its height.
        // We need it in order to make Toolbar Back Button to center vertical in toolbar( strange, but it true  )
        workoutToolbar.setMinimumHeight(getDpFromPixels(75));

        // We do so in order the layout, containing workout image to be toolbar background.
        workoutToolbar.setBackgroundColor( getResources().getColor(android.R.color.transparent) );

        // This action bar is, in fact, the same workoutToolbar
        ActionBar workoutActionBar = getSupportActionBar();

        // This function call sets back icon as the home button icon
        workoutActionBar.setDisplayHomeAsUpEnabled(true);
        workoutActionBar.setDisplayShowHomeEnabled(true);
        workoutActionBar.setHomeButtonEnabled(true);

        // We do so, because we use title TextView as the Toolbar title
        workoutActionBar.setDisplayShowTitleEnabled(false);
        workoutToolbar.setNavigationOnClickListener(backButtonClickListener);

        workoutTitleBackground.setImageBitmap(ImageUtil.getImageBitmap(this, workout.picture));
        workoutTitle.setText(workout.name);
    }

    View.OnClickListener backButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

    private void preRenderWorkoutData() {
        int estimatedTotalTimeSeconds = getTotalEstimatedTime();
        int color = Color.parseColor(workout.color);

        HorizontalProgressDrawable drawable = new HorizontalProgressDrawable(this);
        drawable.setTint(color);
        drawable.setUseIntrinsicPadding(false);
        workoutProgress.setProgressDrawable(drawable);

        workoutProgress.setMax(estimatedTotalTimeSeconds * 100);
        currentWorkoutProgress.showProgress(true);
        currentWorkoutProgress.setIndeterminate(false);
        currentWorkoutProgress.setProgressColor(color);

        completeTextView.setTextColor(color);
        currentTextView.setTextColor(color);

        subWorkoutTitle.setBackgroundColor(color);
    }

    private void renderWorkoutData() {
        int workoutNumber = workoutTrackPreferences.getSubWorkoutNumber();
        int estimatedTotalTimeSeconds = getTotalEstimatedTime();

        // Render progress

        if (lastWorkoutNumber != workoutNumber) {
            currentWorkoutProgress.setMaxProgress(workoutTrackPreferences.getTiming()[workoutNumber] * 100);
        }

        currentWorkoutProgress.setProgress((int) (workoutTrackPreferences.getSubWorkoutTime() / 10));
        workoutProgress.setProgress((int) (workoutTrackPreferences.getTotalWorkoutTime() / 10));

        // Render title and picture

        if (lastWorkoutNumber != workoutNumber) {
            subWorkoutTitle.setText(workout.getSubNames()[workoutNumber]);
            workoutImageView.setImageBitmap(ImageUtil.getImageBitmap(this, workout.getSubPictures()[workoutNumber]));
        }

        // Render total time ticker

        int totalRemainingTimeSeconds = estimatedTotalTimeSeconds - (int) (workoutTrackPreferences.getTotalWorkoutTime() / 1000);

        if (totalRemainingTimeSeconds >= 0) {
            int estimatedTimeMinutes = totalRemainingTimeSeconds / 60;
            int estimatedTimeSeconds = totalRemainingTimeSeconds % 60;
            int estimatedTimeHours = estimatedTimeMinutes / 60;
            estimatedTimeMinutes = estimatedTimeMinutes % 60;

            workoutTimeEstimated.setText(StringUtil.getFormattedTime(estimatedTimeHours, estimatedTimeMinutes, estimatedTimeSeconds));
        }

        // Render current time ticker

        int currentRemainingWorkoutTimeSeconds = (int) (workoutTrackPreferences.getTiming()[workoutNumber]
                - (workoutTrackPreferences.getSubWorkoutTime() / 1000));

        if (currentRemainingWorkoutTimeSeconds >= 0) {
            int timeLeftMinutes = currentRemainingWorkoutTimeSeconds / 60;
            int timeLeftSecs = currentRemainingWorkoutTimeSeconds % 60;
            int timeLeftHours = timeLeftMinutes / 60;
            timeLeftMinutes = timeLeftMinutes % 60;

            workoutTimeCurrent.setText(StringUtil.getFormattedTime(timeLeftHours, timeLeftMinutes, timeLeftSecs));
        }

        lastWorkoutNumber = workoutNumber;
    }

    private void checkForWorkoutEnd() {
        String status = workoutTrackPreferences.getStatus();

        if (status.equals(WorkoutTrack.STATUS.FINISHED)) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

            workoutTrackPreferences
                    .edit()
                    .putSubWorkoutTime(0)
                    .putTotalWorkoutTime(0)
                    .putSubWorkoutNumber(0)
                    .putStatus(WorkoutTrack.STATUS.FINISHED)
                    .apply();

            stopService(new Intent(WorkoutActivity.this, WorkoutTimerService.class));

            saveCalories();

            showInputWeightDialog();
        }

    }

    @Override
    public void onBackPressed() {

        if (!workoutTrackPreferences.getStatus().equals(WorkoutTrack.STATUS.IDLE)) {

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
                                    .putStatus(WorkoutTrack.STATUS.IDLE)
                                    .apply();

                            WorkoutActivity.super.onBackPressed();

                        }

                    })
                    .show();

        } else {
            super.onBackPressed();
        }

    }

    private void saveCalories() {
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
            statsData.burntCalories += (double) (getTotalEstimatedTime() * 7 / 60);
            statsData.multiplier += 1;
        } else {
            statsData = new StatsData();
            statsData.burntCalories = (double) (getTotalEstimatedTime() * 7 / 60);
            statsData.year = currentYear;
            statsData.dayOfYear = currentDay;
            statsData.multiplier = 1;
        }

        statsData.save();
    }

    @SuppressWarnings("StringBufferReplaceableByString")
//    private String formattedWeight() {
//        return new StringBuilder()
//                .append( Math.round( MetricConverter.convertWeight(userPreferences.getWeight(), userPreferences.getWeightMeasurement(), false) )  )
//                .append(" ")
//                .append(formattedWeightMeasurement())
//                .toString();
//    }

    private String formattedWeightMeasurement() {
        return getString(User.sharedPreferences(this).getWeightMeasurement().equals(User.METRICS.KG) ? R.string.kg : R.string.lbs);
    }

    private int getTotalEstimatedTime() {
        int estimatedTotalTimeSeconds = 0;

        for (int index = 0; index < workoutTrackPreferences.getTiming().length; index++) {
            estimatedTotalTimeSeconds += workoutTrackPreferences.getTiming()[index];
        }

        return estimatedTotalTimeSeconds;
    }

    private int getDpFromPixels( int dpValue ){
        final float scale = this.getResources().getDisplayMetrics().density;

        return (int) (dpValue * scale + 0.5f);
    }

    private void showInputWeightDialog() {
        // Multiply all values by 2. [20-200] with 0.5 step <-> 40-400 with 1 step
        MaterialNumberPicker numberPicker = new MaterialNumberPicker.Builder(this)
                .minValue(Math.round(MetricConverter.convertWeight(40, userPreferences.getWeightMeasurement(), false)))
                .maxValue(Math.round(MetricConverter.convertWeight(400, userPreferences.getWeightMeasurement(), false)))
                .defaultValue(Math.round(2 * MetricConverter.convertWeight(userPreferences.getWeight(), userPreferences.getWeightMeasurement(), false)))
                .backgroundColor(getResources().getColor(R.color.colorWhite))
                .separatorColor(getResources().getColor(R.color.colorBlack))
                .textColor(getResources().getColor(R.color.colorBlack))
                .textSize(20)
                .enableFocusability(false)
                .wrapSelectorWheel(true)
                .formatter(new NumberPicker.Formatter() {

                    @Override
                    public String format(int value) {
                        return StringUtil.formattedDigitValue(value * 0.5f);
                    }
                })
                .build();

        new MaterialDialog.Builder(this)
                .title(getString(R.string.workout_congrats, formattedWeightMeasurement()))
                .customView(numberPicker, false)
                .positiveText(R.string.ok)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        MaterialNumberPicker numberPicker = (MaterialNumberPicker) dialog.getCustomView();
                        float weight = ((float) numberPicker.getValue()) / 2;
                        weight = MetricConverter.convertWeight(weight, userPreferences.getWeightMeasurement(), true);

                        userPreferences
                                .edit()
                                .putWeight(weight)
                                .apply();

                        workoutTrackPreferences
                                .edit()
                                .putStatus(WorkoutTrack.STATUS.IDLE)
                                .apply();

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

                        statsData.weight = weight;
                        statsData.save();

                        onBackPressed();
                    }
                })
                .show();
    }

}