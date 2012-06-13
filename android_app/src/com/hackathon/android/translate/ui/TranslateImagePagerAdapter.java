package com.hackathon.android.translate.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hackathon.android.translate.R;
import com.hackathon.android.translate.constant.Constants;
import com.hackathon.android.translate.lazylist.ImageLoader;
import com.hackathon.android.translate.model.Image;
import com.hackathon.android.translate.service.RestfulService;

public class TranslateImagePagerAdapter extends PagerAdapter {

	private List<Image> images;
	private View currentView;

	public TranslateImagePagerAdapter(List<Image> images) {
		super();
		this.images = images;
	}

	public View getCurrentView() {
		return currentView;
	}

	@Override
	public int getCount() {
		return images.size();
	}

	@Override
	public Object instantiateItem(View collection, int position) {
		Context context = collection.getContext();
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.translate_image, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.translation_image);
		TextView text = (TextView) view.findViewById(R.id.translation_image_user_name);
		ProgressDialog dialog = ProgressDialog.show(context, "", "Loading...");
		Image image = images.get(position);
		ImageLoader imageLoader = new ImageLoader(context);
		imageLoader.displayImage(image.getFileURL(), imageView);
		view.setTag(image.getId());
		text.setText(image.getUserName());
		text.requestFocus();
		loadPageWithImageDetails(context, dialog, image.getId());
		((ViewPager) collection).addView(view, position);
		return view;
	}

	private void loadPageWithImageDetails(Context context, ProgressDialog dialog, long imageId) {
		Intent intent = new Intent(Intent.ACTION_SYNC, null, context, RestfulService.class);
		intent.putExtra(Constants.RECEIVER, new ImagesForTranslationReceiver(new TranslateImageViewHandler(dialog,
				context)));
		intent.putExtra(Constants.REST_ACTION, Constants.REST_URL_ACTIONS.IMAGES + "/" + imageId);
		context.startService(intent);
	}

	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView((View) view);

	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == ((View) arg1);

	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		currentView = (View) object;
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
				List<String> translations = new ArrayList<String>();
				for (int i = 0; i < l; i++) {
					JSONObject obj = translationsJson.getJSONObject(i);
					translations.add(obj.getString(Constants.TRANSLATION_DATA));
				}
				Message msg = Message.obtain();
				msg.obj = translations;
				handler.sendMessage(msg);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private class TranslateImageViewHandler extends Handler {
		private final ProgressDialog dialog;
		private Context context;

		public TranslateImageViewHandler(ProgressDialog dialog, Context context) {
			this.dialog = dialog;
			this.context = context;
		}

		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			ListView translationList = (ListView) currentView.findViewById(R.id.translation_data_list);
			translationList.setAdapter(new ArrayAdapter<String>(context, R.layout.translation_data_item,
					(List<String>) msg.obj));
			dialog.dismiss();
		}
	}
}
