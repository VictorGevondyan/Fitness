package com.flycode.jasonfit;

/**
 * Created - acerkinght on  3/11/17.
 */

public class Constants {
    public static class IN_APP_PURCHASE {
        public static String SUBSCRIPTION_ID = "monthly_subscription";
        public static  String SUBSCRIPTION_COACHES_ID = "monthly_subscription_coaches";
//        public static String SUBSCRIPTION_ID = "android.test.item_unavailable";
        public static String TYPE = "subs";
//        public static String TYPE = "inapp";
    }

    public static class EXTRAS {
        public static final String FOOD = EXTRAS.class.getName() + "_food";
        public static final String MEAL = EXTRAS.class.getName() + "_meal";
        public final static String CURRENT_WORKOUT = EXTRAS.class.getName() + "_currentWorkout";
        public static final String ALREADY_SUBSCRIBED = EXTRAS.class.getName() + "_alreadySubscribed";
    }
}