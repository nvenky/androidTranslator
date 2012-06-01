package com.hackathon.android.translate.listener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.facebook.android.FacebookError;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.hackathon.android.translate.model.Friend;

public class FBFriendsRequestListener implements RequestListener {
	
	private Handler handler;

	public FBFriendsRequestListener(Handler handler) {
		this.handler = handler;
	}

	public void onComplete(String response, Object state) {
		// process the response here: executed in background thread
		Log.d("Facebook-Example-Friends Request", "response.length(): " + response.length());
		Log.d("Facebook-Example-Friends Request", "Response: " + response);

		JSONObject json;
		List<Friend> friends = new ArrayList<Friend>();
		List<String> friendsName = new ArrayList<String>();
		try {
			json = new JSONObject(response);

			JSONArray d = json.getJSONArray("data");
			int l = (d != null ? d.length() : 0);
			Log.d("Facebook-Example-Friends Request", "d.length(): " + l);

			for (int i = 0; i < l; i++) {
				JSONObject obj = d.getJSONObject(i);
				Friend f = new Friend(obj.getString("id"), obj.getString("name"));
				friends.add(f);
				friendsName.add(obj.getString("name"));
			}
			Message msg = Message.obtain();
			msg.obj = friends;
			handler.sendMessage(msg);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void onIOException(IOException e, Object state) {
	}

	public void onFileNotFoundException(FileNotFoundException e, Object state) {
	}

	public void onMalformedURLException(MalformedURLException e, Object state) {
	}

	public void onFacebookError(FacebookError e, Object state) {
	}
}
