package com.hackathon.android.translate.notification;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.c2dm.C2DMBaseReceiver;
import com.hackathon.android.translate.R;
import com.hackathon.android.translate.activity.MessageReceivedActivity;
import com.hackathon.android.translate.constant.Constants;
import com.hackathon.android.translate.util.HttpUtils;
import com.hackathon.android.translate.util.Utility;

public class C2DMMessageReceiver extends C2DMBaseReceiver {

	public static void runIntentInService(Context context, Intent intent) {
		acquireWakeLock(context);
		intent.setClassName(context, C2DMMessageReceiver.class.getName());
		context.startService(intent);
	}
	
	@Override
	public void onRegistered(Context context, String registrationId) {
		Log.w("C2DMReceiver-onRegistered", registrationId);
		// String deviceId = Secure.getString(context.getContentResolver(),
		// Secure.ANDROID_ID);
		// Utility.updateDeviceId(deviceId);
		// createNotification(context, registrationId);
		Utility.updateC2DMRegistration(registrationId);
		sendRegistrationIdToServer(registrationId);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.w("C2DM", "Received message");
		final String payload = intent.getStringExtra("payload");
		Log.d("C2DM", "dmControl: payload = " + payload);
		createNotification(context, payload);
	}

	@Override
	public void onError(Context context, String errorId) {
		onUnregistered(context);
	}

	@Override
	public void onUnregistered(Context context) {
		Utility.clearC2DMRegistration();
		sendRegistrationIdToServer(null);
	}

	@Override
	public void onServiceUnavailable(Context context) {
		long backoffTimeMs = Utility.getBackoff();
		Log.d("C2DM", "Scheduling registration retry, backoff = " + backoffTimeMs);
		Intent retryIntent = new Intent(C2DM_RETRY);
		PendingIntent retryPIntent = PendingIntent.getBroadcast(context, 0, retryIntent, 0);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.ELAPSED_REALTIME, backoffTimeMs, retryPIntent);
		backoffTimeMs *= 2;
		Utility.updateBackoff(backoffTimeMs);
	}

	@SuppressWarnings("deprecation")
	public void createNotification(Context context, String payload) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.camera_icon, "Need your help", System.currentTimeMillis());
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		Intent intent = new Intent(context, MessageReceivedActivity.class);
		intent.putExtra("payload", payload);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, null, 0);
		notification.setLatestEventInfo(context, "Translator", "Please help your friend '" + payload
				+ "' with translation", pendingIntent);
		notificationManager.notify(0, notification);
	}

	private void sendRegistrationIdToServer(String registrationId) {
		Log.d("C2DM", "Sending registration ID to my application server");
		HttpPost post = new HttpPost(Constants.SERVER_URL + "/register");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("access_token", Utility.getAccessToken()));
		nameValuePairs.add(new BasicNameValuePair("registration_id", registrationId));
		try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			String response = HttpUtils.executeAndReturnStringResponse(post);
			Log.i(TAG, response);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
