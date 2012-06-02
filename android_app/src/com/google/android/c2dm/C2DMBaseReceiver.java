/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.c2dm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

/**
 * Base class for C2D message receiver. Includes constants for the strings used
 * in the protocol.
 */
public abstract class C2DMBaseReceiver extends IntentService {
	protected static final String C2DM_RETRY = "com.google.android.c2dm.intent.RETRY";

	public static final String REGISTRATION_CALLBACK_INTENT = "com.google.android.c2dm.intent.REGISTRATION";
	private static final String C2DM_INTENT = "com.google.android.c2dm.intent.RECEIVE";

	protected static final String TAG = "C2DM";
	public static final String EXTRA_UNREGISTERED = "unregistered";

	public static final String EXTRA_ERROR = "error";

	public static final String EXTRA_REGISTRATION_ID = "registration_id";

	public static final String ERR_SERVICE_NOT_AVAILABLE = "SERVICE_NOT_AVAILABLE";
	public static final String ERR_ACCOUNT_MISSING = "ACCOUNT_MISSING";
	public static final String ERR_AUTHENTICATION_FAILED = "AUTHENTICATION_FAILED";
	public static final String ERR_TOO_MANY_REGISTRATIONS = "TOO_MANY_REGISTRATIONS";
	public static final String ERR_INVALID_PARAMETERS = "INVALID_PARAMETERS";
	public static final String ERR_INVALID_SENDER = "INVALID_SENDER";
	public static final String ERR_PHONE_REGISTRATION_ERROR = "PHONE_REGISTRATION_ERROR";

    private static final String WAKELOCK_KEY = "C2DM_LIB";

	private static PowerManager.WakeLock mWakeLock;

	public C2DMBaseReceiver() {
		super("C2DMBaseReceiver");
	}

	protected abstract void onMessage(Context context, Intent intent);

	public abstract void onError(Context context, String errorId);

	public abstract void onRegistered(Context context, String registrationId);

    public abstract void onUnregistered(Context context);
    
    public abstract void onServiceUnavailable(Context context);

	@Override
	public final void onHandleIntent(Intent intent) {
		try {
			Context context = getApplicationContext();
			if (intent.getAction().equals(REGISTRATION_CALLBACK_INTENT)) {
				handleRegistration(context, intent);
			} else if (intent.getAction().equals(C2DM_INTENT)) {
				onMessage(context, intent);
			} else if (intent.getAction().equals(C2DM_RETRY)) {
				C2DMessaging.register(context);
			}
		} finally {
			mWakeLock.release();
		}
	}

	protected static void acquireWakeLock(Context context) {
		if (mWakeLock == null) {
			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK_KEY);
		}
		mWakeLock.acquire();
	}

	private void handleRegistration(final Context context, Intent intent) {
		final String registrationId = intent.getStringExtra(EXTRA_REGISTRATION_ID);
		String error = intent.getStringExtra(EXTRA_ERROR);
		String removed = intent.getStringExtra(EXTRA_UNREGISTERED);

		if (removed != null) {
			onUnregistered(context);
			return;
		} else if (error != null) {
			Log.e(TAG, "Registration error " + error);
			onError(context, error);
			if ("SERVICE_NOT_AVAILABLE".equals(error)) {
				onServiceUnavailable(context);
			}
		} else {
			onRegistered(context, registrationId);
		}
	}
}