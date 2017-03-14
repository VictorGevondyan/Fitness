package com.flycode.jasonfit.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;
import com.flycode.jasonfit.R;
import com.flycode.jasonfit.model.WorkoutTrack;
import com.flycode.jasonfit.model.WorkoutTrackPreferences;
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

public class
WorkoutTimerService extends Service {

    public static final String WORKOUT_BROADCAST_IDENTIFIER = "com.flycode.action.WORKOUT_BROADCAST_IDENTIFIER";

    private Timer timer;
    private WorkoutTrackPreferences workoutTrackPreferences;
    private static final int NOTIFICATION_ID = 666;
    private Notification.Builder builder;
    private NotificationManager notificationManager;

    private TextToSpeech textToSpeech;

    public void onCreate() {
        timer = new Timer(false);

        // Create TextToSpeech instance and set the listener. Listener onInit() method
        // will be called automatically after textToSpeech is successfully created
        textToSpeech = new TextToSpeech(this, onWorkoutTextInitListener);

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

                WorkoutTrackPreferences preferences = WorkoutTrack.sharedPreferences(WorkoutTimerService.this);

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
                updateNotification();

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

        // Don't forget to shutdown tts!
        if ( textToSpeech != null) {
            stopTextToSpeech();
        }

        cancelNotification();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * TextToSpeech flow methods
     */

    // This listener onInit() method is called when TextToSpeech instance is created ( new TextToSpeech(...) )
    TextToSpeech.OnInitListener onWorkoutTextInitListener = new TextToSpeech.OnInitListener() {

        @Override
        public void onInit(int status) {

            if ( status == TextToSpeech.SUCCESS ) {
                startTextToSpeech();
            } else {
                Toast.makeText(WorkoutTimerService.this, R.string.tts_failed, Toast.LENGTH_SHORT).show();
            }

        }

    };

    public void startTextToSpeech(){
        String text = "We are the champions my friend";
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void stopTextToSpeech(){
        textToSpeech.stop();
        textToSpeech.shutdown();
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

    private void updateNotification() {
        int currentWorkoutTime = (int) (workoutTrackPreferences.getCurrentWorkoutTime() / 1000);

        builder.setContentText(StringUtil.getFormattedTime(0, 0 , currentWorkoutTime));

        Notification notification = builder.build();

        notification.ledARGB = Color.BLUE;
        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;

        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
