package com.hackathon.android.push;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.google.android.c2dm.C2DMessaging;

public class PushEndpointDemo extends Activity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
  }
  
  public void registerAccount(View v) {
    EditText acct=(EditText)findViewById(R.id.account);   
    C2DMessaging.register(this, acct.getText().toString());
  }
}