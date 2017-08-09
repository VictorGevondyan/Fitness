package com.flycode.jasonfit.activity;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.vending.billing.IInAppBillingService;
import com.flycode.jasonfit.Constants;
import com.flycode.jasonfit.R;
import com.flycode.jasonfit.model.StatsData;
import com.flycode.jasonfit.model.User;
import com.flycode.jasonfit.model.UserPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartActivity extends AppCompatActivity {
    private UserPreferences userPreferences;

    private static final int BUY_REQUEST_CODE = 661;

    private IInAppBillingService inAppBillingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ButterKnife.bind(this);

        userPreferences = User.sharedPreferences(this);

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

        float weight = userPreferences.getWeight();
        Calendar calendar = Calendar.getInstance();

        int currentYear = calendar.get(Calendar.YEAR);
        int currentDay = calendar.get(Calendar.DAY_OF_YEAR);

        StatsData statsData = new StatsData();
        statsData.weight = weight;
        statsData.burntCalories = 0.0;
        statsData.year = currentYear;
        statsData.dayOfYear = currentDay;
        statsData.save();

        new SubscriptionTask().execute();
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
            beSorryForSubscription();
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
            beSorryForSubscription();
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(purchaseData);
            String sku = jsonObject.getString("productId");
            Log.i("TAGG", sku + "bought");

            Intent mainActivityIntent = new Intent(this,MainActivity.class);
            startActivity(mainActivityIntent);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private boolean checkForActiveSubscriptions() throws RemoteException, JSONException, ExecutionException, InterruptedException {

        Bundle activeSubscriptions = inAppBillingService.getPurchases(3, "com.flycode.jasonfit", Constants.IN_APP_PURCHASE.TYPE, null);

        if (activeSubscriptions.getInt("RESPONSE_CODE") == 0) {

            List<String> responseList = activeSubscriptions.getStringArrayList("INAPP_PURCHASE_DATA_LIST");

            for (String purchaseData : responseList) {

                JSONObject jsonObject = new JSONObject(purchaseData);
                String productId = jsonObject.getString("productId");

                if (productId.equals(Constants.IN_APP_PURCHASE.ID)) {
                    return true;
                }
            }

        }

        return false;
    }

    private class SubscriptionTask extends AsyncTask<Void, Integer, Bundle> {
        @Override
        protected Bundle doInBackground(Void... params) {

            try {
                ArrayList<String> itemList = new ArrayList<>();
                itemList.add(Constants.IN_APP_PURCHASE.ID);

                final Bundle queryItems = new Bundle();
                queryItems.putStringArrayList("ITEM_ID_LIST", itemList);

                return inAppBillingService.getSkuDetails(3,
                        getPackageName(),
                        Constants.IN_APP_PURCHASE.TYPE,
                        queryItems);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            if (result == null) {
                beSorryForSubscription();
                return;
            }

            int response = result.getInt("RESPONSE_CODE");

            if (response != 0) {
                beSorryForSubscription();
                return;
            }

            ArrayList<String> responseList = result.getStringArrayList("DETAILS_LIST");

            if (responseList == null) {
                beSorryForSubscription();
                return;
            }

            String sku = null;

            for (String thisResponse : responseList) {
                try {
                    JSONObject object = new JSONObject(thisResponse);

                    sku = object.getString("productId");
                } catch (JSONException e) {
                    e.printStackTrace();

                    beSorryForSubscription();

                    return;
                }

                if (!sku.equals(Constants.IN_APP_PURCHASE.ID)) {
                    beSorryForSubscription();

                    return;
                }
            }

            boolean alreadySubscribed = false;

            try {
                alreadySubscribed = checkForActiveSubscriptions();
            } catch (InterruptedException | JSONException | RemoteException | ExecutionException e) {
                e.printStackTrace();
            }

            if (alreadySubscribed) {
                Intent mainActivityIntent = new Intent(StartActivity.this, MainActivity.class);
                mainActivityIntent.putExtra(Constants.EXTRAS.ALREADY_SUBSCRIBED, true);
                startActivity(mainActivityIntent);
                finish();
                return;
            }

            try {
                buyItem(sku);
            } catch (IntentSender.SendIntentException | RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void beSorryForSubscription() {
        new MaterialDialog
                .Builder(StartActivity.this)
                .title(R.string.sorry)
                .content(R.string.something_goes_wrong_during_subscription)
                .positiveText(R.string.ok)
                .show();
    }
}