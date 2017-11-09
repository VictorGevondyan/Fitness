package com.flycode.jasonfit.activity;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.vending.billing.IInAppBillingService;
import com.flycode.jasonfit.Constants;
import com.flycode.jasonfit.R;
import com.flycode.jasonfit.model.StatsData;
import com.flycode.jasonfit.model.UserPreferences;
import com.flycode.jasonfit.util.DialogUtil;
import com.flycode.jasonfit.util.SubscriptionTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartActivity extends AppCompatActivity implements SubscriptionTask.OnSomethingWentWrongListener, SubscriptionTask.OnSubscriptionStatusResponseListener {

    private static final int BUY_REQUEST_CODE = 661;

    private IInAppBillingService inAppBillingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ButterKnife.bind(this);

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (inAppBillingService != null) {
            unbindService(serviceConnection);
        }
    }

    @OnClick(R.id.try_for_free)
    public void onTryForFree() {

        float weight = new UserPreferences(this).getWeight();
        Calendar calendar = Calendar.getInstance();

        int currentYear = calendar.get(Calendar.YEAR);
        int currentDay = calendar.get(Calendar.DAY_OF_YEAR);

        StatsData statsData = new StatsData();
        statsData.weight = weight;
        statsData.burntCalories = 0.0;
        statsData.year = currentYear;
        statsData.dayOfYear = currentDay;
        statsData.save();

        new SubscriptionTask(this,
                this,
                                        inAppBillingService,
                                            getPackageName(),
                                                Constants.IN_APP_PURCHASE.SUBSCRIPTION_ID)
                .execute();
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            inAppBillingService = IInAppBillingService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            inAppBillingService = null;
        }
    };

    private void buyItem(String sku) throws IntentSender.SendIntentException, RemoteException {
        Bundle buyIntentBundle = inAppBillingService.getBuyIntent(3, getPackageName(), sku, Constants.IN_APP_PURCHASE.TYPE, null /*developerPayload*/);

        PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");

        if (pendingIntent == null) {
            DialogUtil.beSorryForSubscription(StartActivity.this);
            return;
        }

        startIntentSenderForResult(pendingIntent.getIntentSender(),
                BUY_REQUEST_CODE,
                new Intent(),
                0, 0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode != BUY_REQUEST_CODE) {
            return;
        }

        String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");

        if (resultCode != RESULT_OK) {
            return;
        }

        if (purchaseData == null) {
            DialogUtil.beSorryForSubscription(StartActivity.this);
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(purchaseData);
            String sku = jsonObject.getString("productId");
            Log.i("TAGG", sku + "bought");

            Intent mainActivityIntent = new Intent(this, MainActivity.class);
            startActivity(mainActivityIntent);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    @Override
    public void onSomethingWentWrong() {
        DialogUtil.beSorryForSubscription(this);
    }

    @Override
    public void onBuyItem(String sku) {
        try {
            buyItem(sku);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAlreadySubscribed(boolean alreadySubscribed) {

        if (!alreadySubscribed) {
            return;
        }

        Intent mainActivityIntent = new Intent(StartActivity.this, MainActivity.class);
        mainActivityIntent.putExtra(Constants.EXTRAS.ALREADY_SUBSCRIBED, true);
        startActivity(mainActivityIntent);
        finish();
    }
}