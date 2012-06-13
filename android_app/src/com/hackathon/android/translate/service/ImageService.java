package com.hackathon.android.translate.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;

import com.hackathon.android.translate.constant.Constants;
import com.hackathon.android.translate.model.Image;
import com.hackathon.android.translate.util.Utility;

public class ImageService {

	private final Context context;

	public ImageService(Context context) {
		this.context = context;

	}

	public void getImagesFromServer(Handler handler) {
		Intent intent = new Intent(Intent.ACTION_SYNC, null, context, RestfulService.class);
		intent.putExtra(Constants.RECEIVER, new ImagesForTranslationReceiver(handler));
		intent.putExtra(Constants.REST_ACTION, Constants.REST_URL_ACTIONS.IMAGES);
		context.startService(intent);
	}

	private class ImagesForTranslationReceiver extends ResultReceiver {
		private final Handler handler;

		public ImagesForTranslationReceiver(Handler handler) {
			super(handler);
			this.handler = handler;
		}

		@Override
		public void onReceiveResult(int resultCode, Bundle resultData) {
			JSONObject json;
			List<Image> images = new ArrayList<Image>();
			try {
				json = new JSONObject(resultData.getString(Constants.RESULT));
				JSONArray d = json.getJSONArray("data");
				int l = (d != null ? d.length() : 0);
				for (int i = 0; i < l; i++) {
					JSONObject obj = d.getJSONObject(i);
					images.add(new Image(obj.getLong("id"), obj.getString("user_name"), obj.getString("file_name")));
				}
				Message msg = Message.obtain();
				msg.obj = images;
				Utility.updateImages(context.getApplicationContext(), images);
				if (handler != null)
					handler.sendMessage(msg);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

}
