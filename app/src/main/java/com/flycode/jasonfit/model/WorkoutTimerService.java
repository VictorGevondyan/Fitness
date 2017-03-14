package com.flycode.jasonfit.model;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.flycode.jasonfit.R;
import com.flycode.jasonfit.activity.WorkoutActivity;
import com.flycode.jasonfit.util.StringUtil;

import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created - Schumakher on  3/10/17.
 */

public class WorkoutTimerService extends Service {
    public static final String WORKOUT_BROADCAST_IDENTIFIER = "com.flycode.action.WORKOUT_BROADCAST_IDENTIFIER";

    private Timer timer;
    private WorkoutTrackPreferences workoutTrackPreferences;
    private static final int NOTIFICATION_ID = 666;
    private Notification.Builder builder;
    private NotificationManager notificationManager;

    public void onCreate() {
        super.onCreate();

        timer = new Timer(false);
        workoutTrackPreferences = WorkoutTrack.sharedPreferences(WorkoutTimerService.this);
        builder = new Notification.Builder(this);
        notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);

        showNotification();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if (!workoutTrackPreferences.getStatus().equals(WorkoutTrack.STATUS.FINISHED)) {

                    workoutTrackPreferences.edit()
                            .putTotalWorkoutTime(workoutTrackPreferences.get().totalWorkoutTime() + 1000)
                            .putCurrentWorkoutTime(workoutTrackPreferences.get().currentWorkoutTime() + 1000)
                            .apply();
                }

                int workoutNumber = workoutTrackPreferences.getWorkoutNumber();
                int currentWorkoutTime = (int) (workoutTrackPreferences.getCurrentWorkoutTime() / 1000);
                int maxCurrentWorkoutTime = workoutTrackPreferences.getCurrentWorkoutTimeArray().get(workoutNumber);
                int workoutsCount = workoutTrackPreferences.getCurrentWorkoutTimeArray().size();

                if (currentWorkoutTime == maxCurrentWorkoutTime && workoutNumber < workoutsCount - 1 ) {

                    workoutTrackPreferences
                            .edit()
                            .putCurrentWorkoutTime(0)
                            .putWorkoutNumber(workoutNumber + 1)
                            .apply();
                }

                checkForWorkoutEnd();
                updateNotification(workoutNumber);

                Intent broadcastIntent = new Intent(WORKOUT_BROADCAST_IDENTIFIER);
                LocalBroadcastManager.getInstance(WorkoutTimerService.this).sendBroadcast(broadcastIntent);


            }
        }, 0, 1000);

        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();

        timer.cancel();
        timer.purge();

        cancelNotification();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void checkForWorkoutEnd() {
        int totalWorkoutTime = (int) (workoutTrackPreferences
                .get()
                .totalWorkoutTime() / 1000);

        int estimatedTimeSecsFull = 0;

        int setSize = workoutTrackPreferences
                .getCurrentWorkoutTimeArray()
                .size();

        for (int i = 0; i <= setSize - 1; i++) {
            estimatedTimeSecsFull += workoutTrackPreferences
                    .get()
                    .currentWorkoutTimeArray()
                    .get(i);
        }

        if (totalWorkoutTime == estimatedTimeSecsFull) {
            workoutTrackPreferences
                    .edit()
                    .putStatus(WorkoutTrack.STATUS.FINISHED)
                    .apply();
        }
    }

    private void showNotification() {
        int workoutNumber = workoutTrackPreferences.getWorkoutNumber();
        ArrayList<String> stringArray = workoutTrackPreferences.getCurrentWorkoutNameArray();
        String currentWorkoutTitle = stringArray.get(workoutNumber);
        int currentWorkoutTimeSecs = (int) (workoutTrackPreferences.getCurrentWorkoutTime() / 1000);
        String currentWorkoutTimeFormatted = StringUtil.getFormattedTime(0, 0 , currentWorkoutTimeSecs);

        Intent notificationIntent = new Intent(this, WorkoutActivity.class);
        PendingIntent notificationContentIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        builder
                .setTicker(currentWorkoutTitle)
                .setContentTitle(currentWorkoutTitle)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(false)
//                .setContentIntent(notificationContentIntent)
//                .addAction(R.drawable.arrow_up, "Start activity" , notificationContentIntent)
                .setContentText(currentWorkoutTimeFormatted);

        Notification notification = builder.build();

        notification.ledARGB = Color.BLUE;
        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void cancelNotification() {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private void updateNotification(int workoutNumber) {
        int currentWorkoutTime = (int) (workoutTrackPreferences.getCurrentWorkoutTime() / 1000);
        String currentWorkoutTitle = workoutTrackPreferences.getCurrentWorkoutNameArray().get(workoutNumber);


        builder.setContentText(StringUtil.getFormattedTime(0, 0 , currentWorkoutTime))
        .setContentTitle(currentWorkoutTitle);

        Notification notification = builder.build();

        notification.ledARGB = Color.BLUE;
        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;

        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
