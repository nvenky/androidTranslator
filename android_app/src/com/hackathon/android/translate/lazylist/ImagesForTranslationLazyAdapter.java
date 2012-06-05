package com.hackathon.android.translate.lazylist;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hackathon.android.translate.R;
import com.hackathon.android.translate.model.Image;

public class ImagesForTranslationLazyAdapter extends BaseAdapter {

	private Activity activity;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;
	private List<Image> images;

	public ImagesForTranslationLazyAdapter(Activity a, List<Image> images) {
		activity = a;
		this.images = images;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	public int getCount() {
		return images.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.translation_grid_data, null);

		TextView text = (TextView) vi.findViewById(R.id.translation_image_user_name);
		ImageView image = (ImageView) vi.findViewById(R.id.translation_image);
		text.setText(images.get(position).getUserName());
		imageLoader.displayImage(images.get(position).getFileURL(), image);
		return vi;
	}
}