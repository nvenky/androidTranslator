package com.hackathon.android.translate.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

import com.hackathon.android.translate.R;
import com.hackathon.android.translate.R.drawable;
import com.hackathon.android.translate.R.layout;


public class IndexActivity extends TabActivity {
	
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.index);

	    Resources res = getResources();
	    TabHost tabHost = getTabHost();
	    TabHost.TabSpec spec;  
	    Intent intent;
		intent = new Intent().setClass(this, CameraPreviewActivity.class);
	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("Camera").setIndicator("Translate",
	                      res.getDrawable(R.drawable.camera))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, FacebookFriendsActivity.class);
	    spec = tabHost.newTabSpec("Facebook").setIndicator("Friends",
	                      res.getDrawable(R.drawable.ic_tab))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    tabHost.setCurrentTab(1);
	}

}
