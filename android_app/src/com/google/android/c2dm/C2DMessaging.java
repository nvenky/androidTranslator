package com.google.android.c2dm;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class C2DMessaging {
    public static final String EXTRA_SENDER = "sender";
    public static final String EXTRA_APPLICATION_PENDING_INTENT = "app";
    private static final String REQUEST_UNREGISTRATION_INTENT = "com.google.android.c2dm.intent.UNREGISTER";
    private static final String REQUEST_REGISTRATION_INTENT = "com.google.android.c2dm.intent.REGISTER";
    private static final String GSF_PACKAGE = "com.google.android.gsf";

    public static void register(Context context) {
        Intent registrationIntent = new Intent(REQUEST_REGISTRATION_INTENT);
        registrationIntent.setPackage(GSF_PACKAGE);
        registrationIntent.putExtra(EXTRA_APPLICATION_PENDING_INTENT,
                PendingIntent.getBroadcast(context, 0, new Intent(), 0));
        registrationIntent.putExtra(EXTRA_SENDER, "nvenky@gmail.com");
        context.startService(registrationIntent);
    }


    public static void unregister(Context context) {
        Intent regIntent = new Intent(REQUEST_UNREGISTRATION_INTENT);
        regIntent.setPackage(GSF_PACKAGE);
        regIntent.putExtra(EXTRA_APPLICATION_PENDING_INTENT, PendingIntent.getBroadcast(context,
                0, new Intent(), 0));
        context.startService(regIntent);
    }
}