package com.hackathon.android.translate.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.hackathon.android.translate.constant.Constants;
import com.hackathon.android.translate.util.HttpUtils;
import com.hackathon.android.translate.util.Utility;

public class RestfulService extends IntentService {
	public RestfulService() {
		super("RestfulService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		final ResultReceiver receiver = intent.getParcelableExtra(Constants.RECEIVER);
		String path = intent.getStringExtra(Constants.REST_PATH);
		List<NameValuePair> data = new ArrayList<NameValuePair>();
		data.add(new BasicNameValuePair(Constants.ACCESS_TOKEN, Utility.getAccessToken()));
		
		String result = post(Constants.SERVER_URL+ path, data); 
		Bundle bundle = new Bundle();
		try {
			bundle.putString(Constants.RESULT, result);
			receiver.send(Constants.STATUS_FINISHED, bundle);
		} catch (Exception e) {
			bundle.putString(Intent.EXTRA_TEXT, e.toString());
			receiver.send(Constants.STATUS_ERROR, bundle);
		}
		this.stopSelf();
	}

	public String post(String url, List<NameValuePair> data) {
		HttpPost post = new HttpPost(url);
		try {
			post.setEntity(new UrlEncodedFormEntity(data));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return HttpUtils.executeAndReturnStringResponse(post);
	}
}
