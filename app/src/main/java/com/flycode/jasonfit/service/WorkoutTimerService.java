package com.flycode.jasonfit.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.flycode.jasonfit.R;
import com.flycode.jasonfit.model.WorkoutTrack;
import com.flycode.jasonfit.model.WorkoutTrackPreferences;
import com.flycode.jasonfit.activity.WorkoutActivity;
import com.flycode.jasonfit.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created - Schumakher on  3/10/17.
 */

public class
WorkoutTimerService extends Service {

    public static final String WORKOUT_BROADCAST_IDENTIFIER = "com.flycode.action.WORKOUT_BROADCAST_IDENTIFIER";

    private static final int NOTIFICATION_ID = 666;

    private Timer timer;

    private WorkoutTrackPreferences workoutTrackPreferences;

    private Notification.Builder builder;
    private NotificationManager notificationManager;

    private TextToSpeech textToSpeech;
    private boolean ttsIsReady = false;

    public void onCreate() {

        // Create TextToSpeech instance and set the listener. Listener onInit() method
        // will be called automatically after textToSpeech is successfully created
        textToSpeech = new TextToSpeech(this, onWorkoutTextInitListener);

        textToSpeech.setOnUtteranceProgressListener(workoutUtteranceProgressListener);

        timer = new Timer(false);
        workoutTrackPreferences = WorkoutTrack.sharedPreferences(WorkoutTimerService.this);
        builder = new Notification.Builder(this);
        notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);

        showNotification();

        super.onCreate();

    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                String totalWorkoutStatus = workoutTrackPreferences.getTotalWorkoutStatus();

                if (!ttsIsReady || totalWorkoutStatus.equals(WorkoutTrack.STATUS.PAUSED)) {
                    return;
                }

                WorkoutTrackPreferences preferences = WorkoutTrack.sharedPreferences(WorkoutTimerService.this);

                if (!totalWorkoutStatus.equals(WorkoutTrack.STATUS.FINISHED)) {

                    workoutTrackPreferences.edit()
                            .putTotalWorkoutTime(workoutTrackPreferences.get().totalWorkoutTime() + 1000)
                            .putSubWorkoutTime(workoutTrackPreferences.get().subWorkoutTime() + 1000)
                            .apply();

                }

                int currentSubWorkoutNumber = workoutTrackPreferences.getSubWorkoutNumber();
                int currentWorkoutTime = (int) (workoutTrackPreferences.getSubWorkoutTime() / 1000);
                int maxCurrentWorkoutTime = workoutTrackPreferences.getCurrentWorkoutTimeArray().get(currentSubWorkoutNumber);

                int workoutsCount = workoutTrackPreferences.getCurrentWorkoutTimeArray().size();

                if (currentWorkoutTime == maxCurrentWorkoutTime && currentSubWorkoutNumber < workoutsCount - 1) {

                    workoutTrackPreferences
                            .edit()
                            .putSubWorkoutTime(0)
                            .putSubWorkoutNumber(currentSubWorkoutNumber + 1)
                            .apply();

                    speakSubWorkoutNames();

                }

                checkForWorkoutEnd();
                updateNotification(currentSubWorkoutNumber);

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
        if (textToSpeech != null) {
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

            if (status == TextToSpeech.SUCCESS) {
                speakSubWorkoutNames();
            } else {
                Toast.makeText(WorkoutTimerService.this, R.string.tts_failed, Toast.LENGTH_SHORT).show();
            }

        }

    };

    public void speakSubWorkoutNames() {

        ttsIsReady = false;

        ArrayList<String> subworkoutNamesArray = workoutTrackPreferences.getCurrentWorkoutNameArray();
        int subWorkoutNumber = workoutTrackPreferences.getSubWorkoutNumber();
        String subworkoutName = subworkoutNamesArray.get(subWorkoutNumber);

        // If the 3d parameter of speak() is set to null, the UtteranceProgressListener will not be called.
        // So, if we need it will be called, we must use this HashMap
        HashMap<String, String> paramsHashMap = new HashMap<>();
        paramsHashMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, subworkoutName);

        textToSpeech.speak(subworkoutName, TextToSpeech.QUEUE_FLUSH, paramsHashMap);

    }

    public void speakWorkoutEnd(){

        String workoutEnd = getString(R.string.workout_end);

        // If the 3d parameter of speak() is set to null, the UtteranceProgressListener will not be called.
        // So, if we need it will be called, we must use this HashMap
        HashMap<String, String> paramsHashMap = new HashMap<>();
        paramsHashMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, workoutEnd);
        textToSpeech.speak(workoutEnd, TextToSpeech.QUEUE_FLUSH, paramsHashMap);

    }

    public void stopTextToSpeech() {
        textToSpeech.stop();
        textToSpeech.shutdown();
    }

    UtteranceProgressListener workoutUtteranceProgressListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String utteranceId) {
            ttsIsReady = true;
        }

        @Override
        public void onDone(String utteranceId) {

            // If we have this utterance id here, it means, that workout end message is finished,
            // so we must finish the service.
            if( utteranceId.equals(getString(R.string.workout_end)) ){

                workoutTrackPreferences
                        .edit()
                        .putTotalWorkoutStatus(WorkoutTrack.STATUS.FINISHED)
                        .apply();

            }

        }

        @Override
        public void onError(String utteranceId) {

        }
    };


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
            // We need to speak workout finished message, before finishing the service.
            // so we set status "PREPARING_TO_FINISH" , until we speak.
            workoutTrackPreferences
                    .edit()
                    .putTotalWorkoutStatus(WorkoutTrack.STATUS.PREPARING_TO_FINISH)
                    .apply();

            speakWorkoutEnd();

        }

    }

    private void showNotification() {
        int workoutNumber = workoutTrackPreferences.getSubWorkoutNumber();
        ArrayList<String> stringArray = workoutTrackPreferences.getCurrentWorkoutNameArray();
        String currentWorkoutTitle = stringArray.get(workoutNumber);
        int currentWorkoutTimeSecs = (int) (workoutTrackPreferences.getSubWorkoutTime() / 1000);
        String currentWorkoutTimeFormatted = StringUtil.getFormattedTime(0, 0, currentWorkoutTimeSecs);

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

        int currentWorkoutTime = (int) (workoutTrackPreferences.getSubWorkoutTime() / 1000);
        String currentWorkoutTitle = workoutTrackPreferences.getCurrentWorkoutNameArray().get(workoutNumber);


        builder.setContentText(StringUtil.getFormattedTime(0, 0 , currentWorkoutTime))
                .setContentTitle(currentWorkoutTitle);

        Notification notification = builder.build();

        notification.ledARGB = Color.BLUE;
        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;

        notificationManager.notify(NOTIFICATION_ID, notification);

    }

}















