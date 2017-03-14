package com.flycode.jasonfit.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.flycode.jasonfit.R;
import com.flycode.jasonfit.model.WorkoutTrack;
import com.flycode.jasonfit.model.WorkoutTrackPreferences;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created - Schumakher on  3/10/17.
 */

public class
WorkoutTimerService extends Service {

    public static final String WORKOUT_BROADCAST_IDENTIFIER = "com.flycode.action.WORKOUT_BROADCAST_IDENTIFIER";

    private Timer timer;

    private TextToSpeech textToSpeech;

    public void onCreate() {
        timer = new Timer(false);

        // Create TextToSpeech instance and set the listener. Listener onInit() method
        // will be called automatically after textToSpeech is successfully created
        textToSpeech = new TextToSpeech(this, onWorkoutTextInitListener);

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

        // Don't forget to shutdown tts!
        if ( textToSpeech != null) {
            stopTextToSpeech();
        }

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

}
