package com.hackathon.android.translate.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.widget.ListView;

import com.hackathon.android.translate.R;
import com.hackathon.android.translate.constant.Constants;
import com.hackathon.android.translate.lazylist.LazyAdapter;
import com.hackathon.android.translate.model.Friend;
import com.hackathon.android.translate.model.KeyValuePair;
import com.hackathon.android.translate.service.RestfulService;
import com.hackathon.android.translate.util.Utility;

public class FacebookFriendsActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.facebook_friends);
		ProgressDialog dialog = ProgressDialog.show(FacebookFriendsActivity.this, "", "Loading...");

		Parcelable[] value = new KeyValuePair[1];
		value[0] = new KeyValuePair(Constants.ACCESS_TOKEN, Utility.getAccessToken());
		Intent intent = new Intent(Intent.ACTION_SYNC, null, this, RestfulService.class);
		intent.putExtra(Constants.RECEIVER, new FriendsListReceiver(new UpdateFriendsViewHandler(dialog)));
		intent.putExtra(Constants.REST_QUERY_DATA, value);
		intent.putExtra(Constants.REST_ACTION, Constants.REST_URL_ACTIONS.FRIENDS);
		startService(intent);
	}

	private class UpdateFriendsViewHandler extends Handler {
		private final ProgressDialog dialog;

		public UpdateFriendsViewHandler(ProgressDialog dialog) {
			this.dialog = dialog;
		}

		public void handleMessage(Message msg) {
			@SuppressWarnings("unchecked")
			List<Friend> friends = (List<Friend>) msg.obj;
			ListView friendsList = (ListView) findViewById(R.id.friendsListView);
			LazyAdapter adapter = new LazyAdapter(FacebookFriendsActivity.this, friends);
			friendsList.setAdapter(adapter);
			dialog.dismiss();
		}
	}

	private class FriendsListReceiver extends ResultReceiver {
		private final Handler handler;

		public FriendsListReceiver(Handler handler) {
			super(handler);
			this.handler = handler;
		}

		@Override
		public void onReceiveResult(int resultCode, Bundle resultData) {
			JSONObject json;
			List<Friend> friends = new ArrayList<Friend>();
			try {
				json = new JSONObject(resultData.getString(Constants.RESULT));
				JSONArray d = json.getJSONArray("data");
				int l = (d != null ? d.length() : 0);
				for (int i = 0; i < l; i++) {
					JSONObject obj = d.getJSONObject(i);
					Friend f = new Friend(obj.getString("id"), obj.getString("name"));
					friends.add(f);
				}
				Message msg = Message.obtain();
				msg.obj = friends;
				handler.sendMessage(msg);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
