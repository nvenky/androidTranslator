package com.hackathon.android.translate.activity;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.GridView;

import com.hackathon.android.translate.R;
import com.hackathon.android.translate.lazylist.ImagesForTranslationLazyAdapter;
import com.hackathon.android.translate.model.Image;
import com.hackathon.android.translate.service.ImageService;

public class TranslationGridActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.translation_grid);
		ProgressDialog dialog = ProgressDialog.show(TranslationGridActivity.this, "", "Loading...");
		ImagesForTranslationHandler handler = new ImagesForTranslationHandler(dialog);
		new ImageService(this).getImagesFromServer(handler);
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
			ImagesForTranslationLazyAdapter adapter = new ImagesForTranslationLazyAdapter(TranslationGridActivity.this,
					images);
			translationGridView.setAdapter(adapter);
			dialog.dismiss();
		}
	}
}
