package com.dam.puzzlepals.utils;

import android.content.Context;
import android.os.Bundle;

import com.dam.puzzlepals.enums.LoginMethod;
import com.google.firebase.analytics.FirebaseAnalytics;

public class FirebaseEvents {

    public static final String SIGN_OUT = "sign_out";
    public static final String COUNTRY = "country";
    public static final String LOCATION = "location";
    public static final java.lang.String START_GAME = "start_game";

    public static void loginEvent(Context context, LoginMethod loginMethodMethod) {
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.METHOD, String.valueOf(loginMethodMethod));
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
    }

    public static void singOutEvent(Context context, LoginMethod loginMethodMethod) {
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.METHOD, String.valueOf(loginMethodMethod));
        firebaseAnalytics.logEvent(SIGN_OUT, bundle);
    }

    public static void startGameEvent(Context context) {
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.logEvent(START_GAME, null);
    }

    public static void location(Context context, String countryCode) {
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(COUNTRY, String.valueOf(countryCode));
        firebaseAnalytics.logEvent(LOCATION, bundle);
    }

}
