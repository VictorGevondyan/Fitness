package com.flycode.jasonfit.util;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;

import com.android.vending.billing.IInAppBillingService;
import com.flycode.jasonfit.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created - Schumakher on  11/9/17.
 */

public class SubscriptionTask extends AsyncTask<Void, Integer, Bundle> {

    private OnSomethingWentWrongListener listener;
    private OnSubscriptionStatusResponseListener subscriptionStatusListener;
    private IInAppBillingService inAppBillingService;
    private String packageName;
    private String idString;

    public SubscriptionTask(OnSomethingWentWrongListener listener, OnSubscriptionStatusResponseListener subscriptionStatusListener,
                            IInAppBillingService inAppBillingService, String packageName, String idString) {
        this.listener = listener;
        this.subscriptionStatusListener = subscriptionStatusListener;
        this.inAppBillingService = inAppBillingService;
        this.packageName = packageName;
        this.idString = idString;
    }

    @Override
    protected Bundle doInBackground(Void... params) {

        try {
            ArrayList<String> itemList = new ArrayList<>();
            itemList.add(idString);

            final Bundle queryItems = new Bundle();
            queryItems.putStringArrayList("ITEM_ID_LIST", itemList);

            return inAppBillingService.getSkuDetails(3,
                    packageName,
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
            listener.onSomethingWentWrong();
            return;
        }

        int response = result.getInt("RESPONSE_CODE");

        if (response != 0) {
            listener.onSomethingWentWrong();
            return;
        }

        ArrayList<String> responseList = result.getStringArrayList("DETAILS_LIST");

        if (responseList == null) {
            listener.onSomethingWentWrong();
            return;
        }

        String sku = null;

        for (String thisResponse : responseList) {
            try {
                JSONObject object = new JSONObject(thisResponse);

                sku = object.getString("productId");
            } catch (JSONException e) {
                e.printStackTrace();

                listener.onSomethingWentWrong();
                return;
            }

            if (!sku.equals(idString)) {
                listener.onSomethingWentWrong();
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
            subscriptionStatusListener.onAlreadySubscribed(true);
            return;
        }

        subscriptionStatusListener.onBuyItem(sku);
    }

    public interface OnSomethingWentWrongListener {
        void onSomethingWentWrong();
    }

    public interface OnSubscriptionStatusResponseListener {
        void onBuyItem(String sku);
        @SuppressWarnings("SameParameterValue")
        void onAlreadySubscribed(boolean alreadySubscribed);
    }

    private boolean checkForActiveSubscriptions() throws RemoteException, JSONException, ExecutionException, InterruptedException {

        Bundle activeSubscriptions = inAppBillingService.getPurchases(3, "com.flycode.jasonfit", Constants.IN_APP_PURCHASE.TYPE, null);

        if (activeSubscriptions.getInt("RESPONSE_CODE") == 0) {

            List<String> responseList = activeSubscriptions.getStringArrayList("INAPP_PURCHASE_DATA_LIST");

            if (responseList == null) {
                return false;
            }

            for (String purchaseData : responseList) {

                JSONObject jsonObject = new JSONObject(purchaseData);
                String productId = jsonObject.getString("productId");

                if (productId.equals(idString)) {
                    return true;
                }
            }

        }

        return false;
    }
}