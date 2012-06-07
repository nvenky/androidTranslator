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
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.hackathon.android.translate.R;
import com.hackathon.android.translate.constant.Constants;
import com.hackathon.android.translate.model.Image;
import com.hackathon.android.translate.model.KeyValuePair;
import com.hackathon.android.translate.service.RestfulService;
import com.hackathon.android.translate.ui.TranslateImagePagerAdapter;
import com.hackathon.android.translate.util.Utility;

public class TranslateImageActivity extends Activity {

	private ArrayAdapter<String> adapter;
	private List<String> translations = new ArrayList<String>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.translation_image_pager);
		ProgressDialog dialog = ProgressDialog.show(TranslateImageActivity.this, "", "Loading...");
		List<Image> images = Utility.getImages(getApplicationContext());
		TranslateImagePagerAdapter adapter = new TranslateImagePagerAdapter(images);
		ViewPager translationImagePager = (ViewPager) findViewById(R.id.translationImagePager);
		translationImagePager.setAdapter(adapter);
		int imagePosition = getIntent().getIntExtra(Constants.IMAGE_POSITION, -1);
		translationImagePager.setCurrentItem(imagePosition);
		Intent intent = new Intent(Intent.ACTION_SYNC, null, this, RestfulService.class);
		intent.putExtra(Constants.RECEIVER, new ImagesForTranslationReceiver(new TranslateImageViewHandler(dialog)));
		long imageId = images.get(imagePosition).getId();
		intent.putExtra(Constants.REST_ACTION,
				Constants.REST_URL_ACTIONS.IMAGES + "/" + imageId);
		getIntent().putExtra(Constants.IMAGE_ID, imageId);
		startService(intent);
	}

	public void submitTranslation(View view) {
		String imageId = getIntent().getStringExtra(Constants.IMAGE_ID);
		Intent intent = new Intent(Intent.ACTION_SYNC, null, this, RestfulService.class);

		EditText translationText = (EditText) findViewById(R.id.translation_text);
		Parcelable[] value = new KeyValuePair[1];
		String translatedData = translationText.getText().toString();
		value[0] = new KeyValuePair(Constants.TRANSLATION_DATA, translatedData);
		String url = Constants.REST_URL_ACTIONS.IMAGES + "/" + imageId + "/translations";
		intent.putExtra(Constants.REST_ACTION, url);
		intent.putExtra(Constants.REST_QUERY_DATA, value);
		startService(intent);
		translations.add(translatedData);
		adapter.notifyDataSetChanged();
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
			
			try {
				json = new JSONObject(resultData.getString(Constants.RESULT));
				JSONArray translationsJson = json.getJSONArray(Constants.TRANSLATIONS);
				int l = (translationsJson != null ? translationsJson.length() : 0);
				for (int i = 0; i < l; i++) {
					JSONObject obj = translationsJson.getJSONObject(i);
					translations.add(obj.getString(Constants.DATA));
				}
				Message msg = Message.obtain();
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

		public void handleMessage(Message msg) {
			adapter = new ArrayAdapter<String>(TranslateImageActivity.this,
					R.id.translation_data_list, translations);
			ListView translationList = (ListView) findViewById(R.id.translation_data_list);
			translationList.setAdapter(adapter);
			dialog.dismiss();
		}
	}

}
