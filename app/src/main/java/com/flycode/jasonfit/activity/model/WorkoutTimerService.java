package com.flycode.jasonfit.activity.model;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created - Schumakher on  3/10/17.
 */

public class WorkoutTimerService extends Service {
    public static final String WORKOUT_BROADCAST_IDENTIFIER = "com.flycode.action.WORKOUT_BROADCAST_IDENTIFIER";

    private Timer timer;

    public void onCreate() {
        timer = new Timer(false);
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                WorkoutTrackPreferences preferences = WorkoutTrack.sharedPreferences(WorkoutTimerService.this);

                preferences.edit()
                        .putTotalWorkoutTime(preferences.get().totalWorkoutTime() + 1000)
                        .putCurrentWorkoutTime(preferences.get().currentWorkoutTime() + 1000)
                        .apply();

                int workoutNumber = preferences.getWorkoutNumber();
                int currentWorkoutTime = (int) (preferences.getCurrentWorkoutTime() / 1000);
                int maxCurrentWorkoutTime = preferences.getCurrentWorkoutTimeArray().get(workoutNumber);
                int workoutsCount = preferences.getCurrentWorkoutTimeArray().size();

                if (currentWorkoutTime == maxCurrentWorkoutTime && workoutNumber < workoutsCount - 1 ) {

                    preferences
                            .edit()
                            .putCurrentWorkoutTime(0)
                            .putWorkoutNumber(workoutNumber + 1)
                            .apply();
                    Log.i("TAGG", String.valueOf(workoutNumber));
                }

                Intent broadcastIntent = new Intent(WORKOUT_BROADCAST_IDENTIFIER);
                broadcastIntent.putExtra(WORKOUT_BROADCAST_IDENTIFIER, "timer tick");
                LocalBroadcastManager.getInstance(WorkoutTimerService.this).sendBroadcast(broadcastIntent);
            }
        }, 0, 1000);

        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer.purge();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
