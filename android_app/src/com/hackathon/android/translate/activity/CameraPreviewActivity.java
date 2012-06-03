package com.hackathon.android.translate.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.hackathon.android.translate.R;
import com.hackathon.android.translate.util.UploadImage;

public class CameraPreviewActivity extends Activity {
	private SurfaceView preview = null;
	private SurfaceHolder previewHolder = null;
	private Camera camera = null;
	private boolean inPreview = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.camera_preview);

		preview = (SurfaceView) findViewById(R.id.cameraPreview);
		previewHolder = preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void onResume() {
		super.onResume();
		camera = Camera.open();
		startPreview();
	}

	@Override
	public void onPause() {
		stopPreview();
		camera.release();	    
		camera = null;
		super.onPause();
	}

	public void takePicture(View view) {
		if (inPreview) {
			camera.autoFocus(autoFocusCallback);
			inPreview = false;
		}
	}

	private void startPreview() {
		if (camera != null) {
			camera.startPreview();
			inPreview = true;
		}
	}
	
	private void stopPreview() {
		if (inPreview) {
            camera.stopPreview();
            inPreview = false;    		
        }
	}

	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
			// no-op -- wait until surfaceChanged()
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			stopPreview();
	        try {
	            setCameraOrientationParameters(width, height);
	            camera.setPreviewDisplay(previewHolder);
	            startPreview();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

		}

		private void setCameraOrientationParameters(int width, int height) {
			Camera.Parameters parameters = camera.getParameters();
			Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

			if (display.getRotation() == Surface.ROTATION_0) {
			    parameters.setPreviewSize(height, width);
			    camera.setDisplayOrientation(90);
			}

			if (display.getRotation() == Surface.ROTATION_90) {
			    parameters.setPreviewSize(width, height);
			    camera.setDisplayOrientation(0);
			}

			if (display.getRotation() == Surface.ROTATION_180) {
			    parameters.setPreviewSize(height, width);
			    camera.setDisplayOrientation(270);
			}

			if (display.getRotation() == Surface.ROTATION_270) {
			    parameters.setPreviewSize(width, height);
			    camera.setDisplayOrientation(180);
			}
			camera.setParameters(parameters);
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
		     stopPreview();
		}
	};

	Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			camera.takePicture(null, null, photoCallback);
		}
	};

	Camera.PictureCallback photoCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			new SavePhotoTask().execute(data);
			//camera.startPreview();
			//inPreview = true;
		}
	};

	class SavePhotoTask extends AsyncTask<byte[], String, String> {
		@Override
		protected String doInBackground(byte[]... jpeg) {
			File photo = new File(Environment.getExternalStorageDirectory(),
					"photo.jpg");
			System.out.println("File name - " + photo.getAbsolutePath());
			if (photo.exists()) {
				photo.delete();
			}

			try {
				FileOutputStream fos = new FileOutputStream(photo.getPath());
				fos.write(jpeg[0]);
				fos.close();
				UploadImage.upload(photo, CameraPreviewActivity.this);
			} catch (java.io.IOException e) {
				Log.e("PictureDemo", "Exception in photoCallback", e);
			} catch (Exception e) {
				Log.e("Pic Demo", "Upload Error", e);
			}

			return (null);
		}
	}
}