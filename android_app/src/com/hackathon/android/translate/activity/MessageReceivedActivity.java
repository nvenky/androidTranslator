package com.hackathon.android.translate.activity;

import com.hackathon.android.translate.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MessageReceivedActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		TextView friendName = (TextView) findViewById(R.id.friendName);
		setContentView(R.layout.activity_result);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String message = extras.getString("payload");
			if (message != null && message.length() > 0) {				
				friendName.setText(message);
			}
		}
		super.onCreate(savedInstanceState);
	}

}
