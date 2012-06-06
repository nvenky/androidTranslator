package com.hackathon.android.translate.ui;

import java.util.List;

import com.hackathon.android.translate.R;
import com.hackathon.android.translate.lazylist.ImageLoader;
import com.hackathon.android.translate.model.Image;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TranslateImagePagerAdapter extends PagerAdapter{

	private List<Image> images;
	
	
	public TranslateImagePagerAdapter(List<Image> images) {
		super();
		this.images = images;
	}

	@Override
	public int getCount() {
		return images.size();
	}
	
	@Override
    public Object instantiateItem(View collection, int position) {
        LayoutInflater inflater = (LayoutInflater) collection.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.translate_image, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.translation_image);
		TextView text = (TextView) view.findViewById(R.id.translation_image_user_name);
		ImageLoader imageLoader = new ImageLoader(collection.getContext());
		imageLoader.displayImage(images.get(position).getFileURL(), imageView);
		text.setText(images.get(position).getUserName());
        ((ViewPager) collection).addView(view, 0);
        return view;
    }
	
    @Override
    public void destroyItem(View collection, int position, Object view) {
        ((ViewPager) collection).removeView((View) view);

    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((View) arg1);

    }

}
