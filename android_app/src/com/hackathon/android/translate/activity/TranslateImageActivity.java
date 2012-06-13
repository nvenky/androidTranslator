package com.hackathon.android.translate.activity;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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

	private TranslateImagePagerAdapter imagePagerAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.translation_image_pager);
		List<Image> images = Utility.getImages(getApplicationContext());
		initializePagerAdapterWithImagePosition(images);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	private void initializePagerAdapterWithImagePosition(List<Image> images) {
		imagePagerAdapter = new TranslateImagePagerAdapter(images);
		ViewPager translationImagePager = (ViewPager) findViewById(R.id.translationImagePager);
		translationImagePager.setAdapter(imagePagerAdapter);
		int imagePosition = getIntent().getIntExtra(Constants.IMAGE_POSITION, 0);
		translationImagePager.setCurrentItem(imagePosition);
	}

	@SuppressWarnings("unchecked")
	public void submitTranslation(View view) {
		View currentView = imagePagerAdapter.getCurrentView();
		long imageId = (Long) currentView.getTag();
		EditText translationText = (EditText) currentView.findViewById(R.id.translation_text);
		String translatedData = translationText.getText().toString();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(translationText.getWindowToken(), 0);
		ListView listView = (ListView) currentView.findViewById(R.id.translation_data_list);
		ArrayAdapter<String> arrayAdapter = (ArrayAdapter<String>) listView.getAdapter();
		arrayAdapter.add(translatedData);
		fetchTranslationsForImage(imageId, translatedData);
	}

	protected void fetchTranslationsForImage(long imageId, String translatedData) {
		Intent intent = new Intent(Intent.ACTION_SYNC, null, this, RestfulService.class);
		Parcelable[] value = new KeyValuePair[2];
		value[0] = new KeyValuePair(Constants.TRANSLATION_DATA, translatedData);
		value[1] = new KeyValuePair(Constants.FACEBOOK_ID, Utility.getFacebookId());
		String url = Constants.REST_URL_ACTIONS.IMAGES + "/" + imageId + "/translations";
		intent.putExtra(Constants.REST_ACTION, url);
		intent.putExtra(Constants.REST_QUERY_DATA, value);
		startService(intent);
	}

}
