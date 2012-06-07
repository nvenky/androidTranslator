package com.hackathon.android.translate.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.google.android.c2dm.C2DMessaging;
import com.hackathon.android.translate.R;
import com.hackathon.android.translate.util.Utility;

public class WelcomeActivity extends Activity {

	public WelcomeActivity() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utility.loadComponents(WelcomeActivity.this);
		setContentView(R.layout.welcome);
	}

	public void login(View view) {
		Utility.loginWithExistingToken();
		if (!Utility.getFacebook().isSessionValid()) {
			Utility.getFacebook().authorize(this, new String[] {}, new DialogListener() {
				public void onComplete(Bundle values) {
					Utility.updateAccessToken();
					//Utility.updateFacebookId();
					afterLogin();
				}

				public void onCancel() {
				}

				public void onFacebookError(FacebookError e) {
				}

				public void onError(DialogError e) {
				}
			});
		} else {
			afterLogin();
		}
	}

	public void afterLogin() {
		C2DMessaging.register(WelcomeActivity.this);
		Intent dashboardActivityIntent = new Intent(getApplicationContext(), DashboardActivity.class);
		startActivity(dashboardActivityIntent);
		this.finish();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Utility.getFacebook().authorizeCallback(requestCode, resultCode, data);
	}
}
