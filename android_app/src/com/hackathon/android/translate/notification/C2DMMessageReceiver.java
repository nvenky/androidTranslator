package com.hackathon.android.translate.notification;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hackathon.android.translate.R;
import com.hackathon.android.translate.activity.MessageReceivedActivity;
import com.hackathon.android.translate.constant.Constants;
import com.hackathon.android.translate.util.Utility;

public class C2DMMessageReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.w("C2DM", "Registration Receiver called");
		if ("com.google.android.c2dm.intent.REGISTRATION".equals(action)) {
			String error = intent.getStringExtra("error");
			String registrationId = intent.getStringExtra("registration_id");
			//String deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
			// Utility.updateDeviceId(deviceId);
			Log.d("C2DM", "dmControl: registrationId = " + registrationId + ", error = " + error);
			//createNotification(context, registrationId);
			Utility.updateC2DMRegistration(registrationId);			
			sendRegistrationIdToServer(registrationId);
		} else if ("com.google.android.c2dm.intent.RECEIVE".equals(action)) {
			Log.w("C2DM", "Received message");
			final String payload = intent.getStringExtra("payload");
			Log.d("C2DM", "dmControl: payload = " + payload);
			createNotification(context, payload);
		}
	}

	@SuppressWarnings("deprecation")
	public void createNotification(Context context, String payload) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.camera, "Need your help",
				System.currentTimeMillis());
		// Hide the notification after its selected
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		Intent intent = new Intent(context, MessageReceivedActivity.class);
		intent.putExtra("payload", payload);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, null, 0);
		notification.setLatestEventInfo(context, "Translator", "Please help your friend '"+ payload + " with translation", pendingIntent);
		notificationManager.notify(0, notification);
	}

	// Incorrect usage as the receiver may be canceled at any time
	// do this in an service and in an own thread
	private void sendRegistrationIdToServer(String registrationId) {
		Log.d("C2DM", "Sending registration ID to my application server");
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(Constants.SERVER_URL + "/register");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		// Get the deviceID
		nameValuePairs.add(new BasicNameValuePair("access_token", Utility.getAccessToken()));
		nameValuePairs.add(new BasicNameValuePair("registration_id", registrationId));

		try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(post);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
}
