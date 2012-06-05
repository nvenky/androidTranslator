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
import android.widget.GridView;

import com.hackathon.android.translate.R;
import com.hackathon.android.translate.constant.Constants;
import com.hackathon.android.translate.lazylist.ImagesForTranslationLazyAdapter;
import com.hackathon.android.translate.model.Image;
import com.hackathon.android.translate.model.KeyValuePair;
import com.hackathon.android.translate.service.RestfulService;

public class TranslateActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.translate);
		ProgressDialog dialog = ProgressDialog.show(TranslateActivity.this, "", "Loading...");

		Parcelable[] value = new KeyValuePair[0];
		// value[0] = new KeyValuePair(Constants.ACCESS_TOKEN,
		// Utility.getAccessToken());
		Intent intent = new Intent(Intent.ACTION_SYNC, null, this, RestfulService.class);
		intent.putExtra(Constants.RECEIVER, new ImagesForTranslationReceiver(new ImagesForTranslationHandler(dialog)));
		intent.putExtra(Constants.REST_QUERY_DATA, value);
		intent.putExtra(Constants.REST_ACTION, Constants.REST_URL_ACTIONS.IMAGES);
		startService(intent);
	}

	private class ImagesForTranslationHandler extends Handler {
		private final ProgressDialog dialog;

		public ImagesForTranslationHandler(ProgressDialog dialog) {
			this.dialog = dialog;
		}

		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			List<Image> images = (List<Image>) msg.obj;
			GridView translationGridView = (GridView) findViewById(R.id.translationGridView);
			ImagesForTranslationLazyAdapter adapter = new ImagesForTranslationLazyAdapter(TranslateActivity.this,
					images);
			translationGridView.setAdapter(adapter);
			dialog.dismiss();
		}
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
				handler.sendMessage(msg);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
