package com.flycode.jasonfit.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.vending.billing.IInAppBillingService;
import com.flycode.jasonfit.Constants;
import com.flycode.jasonfit.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class SplashActivity extends AppCompatActivity {
    private IInAppBillingService inAppBillingService;
    private Timer timer;
    private boolean isSubscriptionActive = false;
    private boolean subscriptionChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (subscriptionChecked) {
            startTimer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (timer != null) {
            timer.purge();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (inAppBillingService != null) {
            unbindService(serviceConnection);
        }
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            inAppBillingService = IInAppBillingService.Stub.asInterface(service);

            try {
                isSubscriptionActive = checkForActiveSubscriptions();
                subscriptionChecked = true;
                startTimer();
            } catch (RemoteException | JSONException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            inAppBillingService = null;
        }
    };

    private void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isSubscriptionActive) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(SplashActivity.this, IntroActivity.class));
                    finish();
                }
            }
        }, 3000);

    }

    private boolean checkForActiveSubscriptions() throws RemoteException, JSONException, ExecutionException, InterruptedException {

        Bundle activeSubscriptions = inAppBillingService.getPurchases(3, "com.flycode.jasonfit", Constants.IN_APP_PURCHASE.TYPE, null);

        if (activeSubscriptions.getInt("RESPONSE_CODE") == 0) {

            List<String> responseList = activeSubscriptions.getStringArrayList("INAPP_PURCHASE_DATA_LIST");

            for (String purchaseData : responseList) {

                JSONObject jsonObject = new JSONObject(purchaseData);
                String productId = jsonObject.getString("productId");

                if (productId.equals(Constants.IN_APP_PURCHASE.SUBSCRIPTION_ID)) {
                    return true;
                }
            }

        }

        return false;
    }
}
