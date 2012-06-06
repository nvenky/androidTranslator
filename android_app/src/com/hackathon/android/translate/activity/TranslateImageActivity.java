package com.hackathon.android.translate.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.hackathon.android.translate.R;
import com.hackathon.android.translate.constant.Constants;
import com.hackathon.android.translate.lazylist.ImageLoader;
import com.hackathon.android.translate.model.KeyValuePair;
import com.hackathon.android.translate.service.RestfulService;
import com.hackathon.android.translate.ui.TranslateImagePagerAdapter;
import com.hackathon.android.translate.util.Utility;

public class TranslateImageActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.translation_image_pager);
		//ProgressDialog dialog = ProgressDialog.show(TranslateImageActivity.this, "", "Loading...");
		TranslateImagePagerAdapter adapter = new TranslateImagePagerAdapter(Utility.getImages(getApplicationContext()));
		ViewPager translationImagePager = (ViewPager) findViewById(R.id.translationImagePager);
		translationImagePager.setAdapter(adapter);
		translationImagePager.setCurrentItem(getIntent().getIntExtra(Constants.IMAGE_POSITION, -1));
//		Intent intent = new Intent(Intent.ACTION_SYNC, null, this, RestfulService.class);
//		intent.putExtra(Constants.RECEIVER, new ImagesForTranslationReceiver(new TranslateImageViewHandler(dialog)));
//		intent.putExtra(Constants.REST_ACTION,
//				Constants.REST_URL_ACTIONS.IMAGES + "/" + getIntent().getStringExtra(Constants.IMAGE_ID));
//		startService(intent);
	}

	public void submitTranslation(View view) {
		String imageId = getIntent().getStringExtra(Constants.IMAGE_ID);
		Intent intent = new Intent(Intent.ACTION_SYNC, null, this, RestfulService.class);

		EditText translationText = (EditText) findViewById(R.id.translationText);
		Parcelable[] value = new KeyValuePair[1];
		value[0] = new KeyValuePair(Constants.TRANSLATION_DATA, translationText.getText().toString());
		String url = Constants.REST_URL_ACTIONS.IMAGES + "/" + imageId + "/translations";
		intent.putExtra(Constants.REST_ACTION, url);
		intent.putExtra(Constants.REST_QUERY_DATA, value);
		startService(intent);
	}

	private class ImagesForTranslationReceiver extends ResultReceiver {
		private final Handler handler;

		public ImagesForTranslationReceiver(Handler handler) {
			super(handler);
			this.handler = handler;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onReceiveResult(int resultCode, Bundle resultData) {
			JSONObject json;
			List<String> translations = new ArrayList<String>();
			try {
				json = new JSONObject(resultData.getString(Constants.RESULT));
				JSONArray translationsJson = json.getJSONArray(Constants.TRANSLATIONS);
				int l = (translationsJson != null ? translationsJson.length() : 0);
				for (int i = 0; i < l; i++) {
					JSONObject obj = translationsJson.getJSONObject(i);
					translations.add(obj.getString(Constants.DATA));
				}
				Map mapData = new HashMap();
				mapData.put(Constants.TRANSLATIONS, translations);
				mapData.put(Constants.FILE_NAME, json.getString(Constants.FILE_NAME));
				Message msg = Message.obtain();
				msg.obj = mapData;
				handler.sendMessage(msg);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private class TranslateImageViewHandler extends Handler {
		private final ProgressDialog dialog;

		public TranslateImageViewHandler(ProgressDialog dialog) {
			this.dialog = dialog;
		}

		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			Map data = (Map) msg.obj;
			ImageLoader imageLoader = new ImageLoader(TranslateImageActivity.this);
			ImageView imageView = (ImageView) findViewById(R.id.translation_image);
			imageLoader.displayImage(Constants.BUCKET_URL + (String) data.get(Constants.FILE_NAME), imageView);
			dialog.dismiss();
		}
	}

}
