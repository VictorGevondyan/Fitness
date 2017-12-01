package com.flycode.jasonfit.fragment;


import android.app.Fragment;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.vending.billing.IInAppBillingService;
import com.flycode.jasonfit.Constants;
import com.flycode.jasonfit.R;
import com.flycode.jasonfit.adapter.CoachListAdapter;
import com.flycode.jasonfit.model.Coach;
import com.flycode.jasonfit.model.StatsData;
import com.flycode.jasonfit.model.UserPreferences;
import com.flycode.jasonfit.util.DeviceInfoUtil;
import com.flycode.jasonfit.util.DialogUtil;
import com.flycode.jasonfit.util.SubscriptionTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

/**
 * Created - Schumakher on  8/31/17.
 */
public class CoachListFragment extends Fragment implements CoachListAdapter.OnCoachItemClickListener, SubscriptionTask.OnSomethingWentWrongListener, SubscriptionTask.OnSubscriptionStatusResponseListener {

    @BindView(R.id.coach_recycler) RecyclerView coachRecycler;
    @BindView(R.id.coaching_subscription_linear) LinearLayout subscriptionLinear;

    private Unbinder unbinder;
    private static final int BUY_REQUEST_CODE = 661;

    private IInAppBillingService inAppBillingService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coach_list, container, false);

        unbinder = ButterKnife.bind(this, view);

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        getActivity().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unbinder.unbind();

        if (inAppBillingService != null) {
            getActivity().unbindService(serviceConnection);
        }
    }

    @OnClick(R.id.coaching_subscription_linear)
    public void onCoachingSubscriptionLinearClickListener() {
        subscriptionParty();
    }

    private void subscriptionParty() {
        float weight = new UserPreferences(getActivity()).getWeight();
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
                getActivity().getPackageName(),
                Constants.IN_APP_PURCHASE.SUBSCRIPTION_COACHES_ID)
                .execute();
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            inAppBillingService = IInAppBillingService.Stub.asInterface(service);
            subscriptionParty();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            inAppBillingService = null;
        }
    };

    private void buyItem(String sku) throws IntentSender.SendIntentException, RemoteException {
        Bundle buyIntentBundle = inAppBillingService.getBuyIntent(3, getActivity().getPackageName(), sku, Constants.IN_APP_PURCHASE.TYPE, null /*developerPayload*/);

        PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");

        if (pendingIntent == null) {
            DialogUtil.beSorryForSubscription(getActivity());
            return;
        }

        getActivity().startIntentSenderForResult(pendingIntent.getIntentSender(),
                BUY_REQUEST_CODE,
                new Intent(),
                0, 0, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode != BUY_REQUEST_CODE) {
            return;
        }

        String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");

        if (resultCode != RESULT_OK) {
            return;
        }

        if (purchaseData == null) {
            DialogUtil.beSorryForSubscription(getActivity());
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(purchaseData);
            String sku = jsonObject.getString("productId");
            Log.i("TAGG", sku + "bought");

            onAlreadySubscribed(true);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCoachItemClick(Coach coach) {

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto",coach.getEmail(), null));

        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, coach.getName());
        emailIntent.putExtra(Intent.EXTRA_TEXT, "\n\n\n\n\nDo not delete this." + "\n" + cryptography());

        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    @Override
    public void onSomethingWentWrong() {
        DialogUtil.beSorryForSubscription(getActivity());
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

        coachRecycler.setVisibility(View.VISIBLE);
        subscriptionLinear.setVisibility(View.GONE);

        ArrayList<Coach> coaches = new ArrayList<>();

        Coach coach = new Coach();
        coach.setName("Vazgen Bagratuni");
        coach.setEmail("vazgen.bagratuni@yopmail.com");

        coaches.add(coach);

        Coach coach1 = new Coach();
        coach1.setName("Ashot II Bagratuni");
        coach1.setEmail("ashotBagratuni914@yopmail.com");

        coaches.add(coach1);

        coachRecycler.setAdapter(new CoachListAdapter(coaches, this));
        coachRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (coaches.size() == 1) {
            onCoachItemClick(coaches.get(0));
        }
    }

    private String cryptography() {

//        return RSA();

        return DES();
    }

    private String DES() {

        String encripted = "";

        try {

            DESKeySpec keySpec = new DESKeySpec("secretKey".getBytes("UTF8"));

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

            SecretKey secretKey = keyFactory.generateSecret(keySpec);

            String text = DeviceInfoUtil.getDeviceInfo()+ " " + DeviceInfoUtil.getOsVersion() + " " + Calendar.getInstance().getTime();

//            byte[] clearText = "this copy of JasonFit application has permission for coaching".getBytes();
            byte[] clearText = text.getBytes();


            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            encripted = Base64.encodeToString(cipher.doFinal(clearText), Base64.DEFAULT);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return encripted.replace("\n", "");
    }

//    private String decrypt(String encripted) {
//
//        String decripted = "";
//
//        try {
//
//            DESKeySpec keySpec = new DESKeySpec("specialKey".getBytes("UTF8"));
//
//            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
//
//            SecretKey secretKey = keyFactory.generateSecret(keySpec);
//
//
//            Cipher cipher = Cipher.getInstance("DES");
//            cipher.init(Cipher.DECRYPT_MODE, secretKey);
//
//            decripted = new String(cipher.doFinal(Base64.decode(encripted, Base64.DEFAULT)), "UTF8");
//
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//        } catch (BadPaddingException e) {
//            e.printStackTrace();
//        } catch (InvalidKeySpecException e) {
//            e.printStackTrace();
//        } catch (IllegalBlockSizeException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        return decripted;
//    }

//    private String RSA() {
//        String encryptionString = "this copy of JasonFit application has permission for coaching";
//
//        //key generation
//
//        Key publicKey = null;
//        Key privateKey = null;
//
//        KeyPairGenerator generator = null;
//        try {
//            generator = KeyPairGenerator.getInstance("RSA");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        generator.initialize(1024);
//
//        KeyPair keyPair = generator.genKeyPair();
//        publicKey = keyPair.getPublic();
//        privateKey = keyPair.getPrivate();
//
//
//
//        //encoding
//
//        byte[] encodedBytes = null;
//
//        Cipher cipher = null;
//        try {
//            cipher = Cipher.getInstance("RSA");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//        }
//        try {
//            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            encodedBytes = cipher.doFinal(encryptionString.getBytes());
//        } catch (IllegalBlockSizeException e) {
//            e.printStackTrace();
//        } catch (BadPaddingException e) {
//            e.printStackTrace();
//        }
//
//        return Base64.encodeToString(encodedBytes, Base64.DEFAULT);
//    }
}