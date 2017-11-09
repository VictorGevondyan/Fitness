package com.flycode.jasonfit.util;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flycode.jasonfit.R;
import com.flycode.jasonfit.activity.StartActivity;

/**
 * Created - Schumakher on  11/9/17.
 */

public class DialogUtil {

    public static void beSorryForSubscription(Context context) {
        new MaterialDialog
                .Builder(context)
                .title(R.string.sorry)
                .content(R.string.something_goes_wrong_during_subscription)
                .positiveText(R.string.ok)
                .show();
    }
}
