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
import android.os.Parcelable;
import android.os.ResultReceiver;

import com.hackathon.android.translate.constant.Constants;
import com.hackathon.android.translate.model.KeyValuePair;
import com.hackathon.android.translate.util.HttpUtils;

public class RestfulService extends IntentService {
	public RestfulService() {
		super("RestfulService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		final ResultReceiver receiver = intent.getParcelableExtra(Constants.RECEIVER);
		String path = intent.getStringExtra(Constants.REST_ACTION);
		Parcelable[] parcelableArrayExtra = intent.getParcelableArrayExtra(Constants.REST_QUERY_DATA);
		List<NameValuePair> data = parcelableArrayExtra == null ? new ArrayList<NameValuePair>()
				: getPostParameters(parcelableArrayExtra);

		String result = post(Constants.SERVER_URL + path, data);
		if (receiver != null) {
			notifyReceiver(receiver, result);
		}
		this.stopSelf();
	}

	private List<NameValuePair> getPostParameters(Parcelable[] parcelables) {
		List<NameValuePair> data = new ArrayList<NameValuePair>();
		for (int i = 0; i < parcelables.length; i++) {
			KeyValuePair keyValuePair = (KeyValuePair) parcelables[i];
			data.add(new BasicNameValuePair(keyValuePair.getKey(), keyValuePair.getValue()));
		}
		return data;
	}

	private void notifyReceiver(final ResultReceiver receiver, String result) {
		Bundle bundle = new Bundle();
		try {
			bundle.putString(Constants.RESULT, result);
			receiver.send(Constants.STATUS_FINISHED, bundle);
		} catch (Exception e) {
			bundle.putString(Intent.EXTRA_TEXT, e.toString());
			receiver.send(Constants.STATUS_ERROR, bundle);
		}
	}

	private String post(String url, List<NameValuePair> data) {
		HttpPost post = new HttpPost(url);
		try {
			post.setEntity(new UrlEncodedFormEntity(data));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return HttpUtils.executeAndReturnStringResponse(post);
	}
}
