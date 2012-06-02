/*
 */
package com.google.android.c2dm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hackathon.android.translate.notification.C2DMMessageReceiver;

public class C2DMBroadcastReceiver extends BroadcastReceiver {
    
    @Override
    public final void onReceive(Context context, Intent intent) {
        C2DMMessageReceiver.runIntentInService(context, intent);
        setResult(Activity.RESULT_OK, null, null);      
    }
}