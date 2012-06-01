package com.hackathon.android.translate.activity;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;

import com.hackathon.android.translate.R;
import com.hackathon.android.translate.lazylist.LazyAdapter;
import com.hackathon.android.translate.listener.FBFriendsRequestListener;
import com.hackathon.android.translate.model.Friend;
import com.hackathon.android.translate.util.Utility;

public class FacebookFriendsActivity extends Activity {	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.facebook_friends);
		ProgressDialog dialog = ProgressDialog.show(FacebookFriendsActivity.this, "", "Loading...");
		Handler handler = getUpdateViewHandler(dialog);
		Utility.getAsyncRunner().request("me/friends", new FBFriendsRequestListener(handler));		
		
	}
	
	private Handler getUpdateViewHandler(final ProgressDialog dialog) {
		return new Handler() {
			private ProgressDialog progDialog = dialog;
			public void handleMessage(Message msg) {
				@SuppressWarnings("unchecked")
				List<Friend> friends = (List<Friend>) msg.obj;
				ListView friendsList = (ListView) findViewById(R.id.friendsListView);
				LazyAdapter adapter = new LazyAdapter(FacebookFriendsActivity.this, friends);
				friendsList.setAdapter(adapter);
				progDialog.dismiss();
			}
		};
	}
}
