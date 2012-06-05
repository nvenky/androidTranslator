package com.hackathon.android.translate.lazylist;

import java.util.List;

import com.hackathon.android.translate.R;
import com.hackathon.android.translate.model.Friend;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FacebookFriendsLazyAdapter extends BaseAdapter {

	private Activity activity;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;
	private final List<Friend> friends;

	public FacebookFriendsLazyAdapter(Activity a, List<Friend> friends) {
		activity = a;
		this.friends = friends;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	public int getCount() {
		return friends.size();
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
			vi = inflater.inflate(R.layout.fb_friends_row_layout, null);

		TextView text = (TextView) vi.findViewById(R.id.text);
		ImageView image = (ImageView) vi.findViewById(R.id.image);
		text.setText(friends.get(position).getName());
		imageLoader.displayImage(friends.get(position).getProfileImagePath(), image);
		return vi;
	}
}