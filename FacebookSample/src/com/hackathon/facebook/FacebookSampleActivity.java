package com.hackathon.facebook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ListView;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.hackathon.facebook.lazylist.LazyAdapter;

public class FacebookSampleActivity extends Activity {

	private static final String ACCESS_TOKEN = "access_token";
	Facebook facebook = new Facebook("378619692194435");
	private SharedPreferences sharedPreferences;
	private AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);

	private Handler handler = new Handler() {
        public void  handleMessage(Message msg) {
    		@SuppressWarnings("unchecked")
    		List<Friend> friends = (List<Friend>) msg.obj;
			//List<String> friendNames = (List<String>) msg.obj;
        	ListView friendsList = (ListView) findViewById(R.id.friendsListView);
        	LazyAdapter adapter=new LazyAdapter(FacebookSampleActivity.this, friends);
            friendsList.setAdapter(adapter);
        	
        }};
   

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		sharedPreferences = getPreferences();
		String accessToken = sharedPreferences.getString(ACCESS_TOKEN, null);
		long expires = sharedPreferences.getLong("access_expires", 0);
		if (accessToken != null) {
			facebook.setAccessToken(accessToken);
		}
		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}
		if (!facebook.isSessionValid()) {
			facebook.authorize(this, new String[] { "email", "publish_checkins" }, new DialogListener() {
				@Override
				public void onComplete(Bundle values) {
					String token = facebook.getAccessToken();
					save(token);
				}

				@Override
				public void onCancel() {
				}

				@Override
				public void onFacebookError(FacebookError e) {
				}

				@Override
				public void onError(DialogError e) {
				}
			});
		}
		mAsyncRunner.request("me/friends", new FriendsRequestListener());
	}

	private void save(String token) {
		SharedPreferences prefs = getPreferences();
		prefs.edit().putString(ACCESS_TOKEN, token).commit();
	}

	private SharedPreferences getPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		facebook.extendAccessTokenIfNeeded(this, null);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		facebook.authorizeCallback(requestCode, resultCode, data);
		// mAsyncRunner.request("me/friends", new FriendsRequestListener());
	}

	class FriendsRequestListener implements RequestListener {
	     public void run() {
	         //your business logic.
	     }

		@Override
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
				Message msg= Message.obtain();
				msg.obj = friends;
				handler.sendMessage(msg);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e, Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onMalformedURLException(MalformedURLException e, Object state) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub
		}
	}

	

}
