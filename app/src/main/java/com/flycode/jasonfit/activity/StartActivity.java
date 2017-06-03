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

import com.android.vending.billing.IInAppBillingService;
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

    IInAppBillingService inAppBillingService;
    String premiumUpgradePrice;

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

        try {
            if (skuFromBillingRequest() == null) {
                return;
            }

            if (checkForActiveSubscriptions()) {
                Intent mainActivityIntent = new Intent(this,MainActivity.class);
                startActivity(mainActivityIntent);
                return;
            }

            buyItem(skuFromBillingRequest());

            Log.i("TAGG", "error, no sku");

        } catch (InterruptedException | JSONException | IntentSender.SendIntentException | RemoteException | ExecutionException e) {
            e.printStackTrace();
        }

//        Intent mainActivityIntent = new Intent(this,MainActivity.class);
//        startActivity(mainActivityIntent);

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

    private String skuFromBillingRequest() throws InterruptedException, JSONException, ExecutionException {
        ArrayList<String> skuList = new ArrayList<>();
        skuList.add("monthlySubscription");

        final Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

        //Below We create new thread because as google says ->
        // "calling this method triggers a network request that could block your main thread"

        AsyncTask<Void, Integer, Bundle> task = new AsyncTask<Void, Integer, Bundle>() {
            @Override
            protected Bundle doInBackground(Void... params) {

                try {
                    Bundle skuDetails = inAppBillingService.getSkuDetails(3,
                            getPackageName(),
                            "subs",
                            querySkus);

                    return skuDetails;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bundle bundle) {
                super.onPostExecute(bundle);
            }
        };

        task.execute();
        Bundle skuDetails = task.get();

        //end of thread hardcore party

        int response = skuDetails.getInt("RESPONSE_CODE");

        if (response == 0) {
            ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");

            for (String thisResponse : responseList) {
                JSONObject object = new JSONObject(thisResponse);

                String sku = object.getString("productId");
                String price = object.getString("price");

                if (sku.equals("monthlySubscription")) {
                    premiumUpgradePrice = price;
                    return sku;
                }
            }
        }

        return null;
    }

    private void buyItem(String sku) throws IntentSender.SendIntentException, RemoteException {
        Bundle buyIntentBundle = inAppBillingService.getBuyIntent(3, getPackageName(), sku, "subs", null /*developerPayload*/);

        PendingIntent pendingIntent = buyIntentBundle.getParcelable("RESPONSE_BUY_INTENT");

        startIntentSenderForResult(pendingIntent.getIntentSender(),
                BUY_REQUEST_CODE,
                new Intent(),
                Integer.valueOf(0),
                Integer.valueOf(0),
                Integer.valueOf(0));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode != BUY_REQUEST_CODE) {
            return;
        }

        int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
        String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");

        if (responseCode != RESULT_OK) {
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(purchaseData);
            String sku = jsonObject.getString("productID");
            Log.i("TAGG", sku + "bought");

            Intent mainActivityIntent = new Intent(this,MainActivity.class);
            startActivity(mainActivityIntent);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private boolean checkForActiveSubscriptions() throws RemoteException, JSONException, ExecutionException, InterruptedException {

        Bundle activeSubscriptions = inAppBillingService.getPurchases(3, "com.flycode.jasonfit", "subs", null);

        if (activeSubscriptions.getInt("RESPONSE_CODE") == 0) {

            List<String> responseList = activeSubscriptions.getStringArrayList("INAPP_PURCHASE_DATA_LIST");

            for (String purchaseData : responseList) {

                JSONObject jsonObject = new JSONObject(purchaseData);
                String productId = jsonObject.getString("productId");

                if (productId.equals(skuFromBillingRequest())) {
                    return true;
                }
            }

        }

        return false;
    }
}