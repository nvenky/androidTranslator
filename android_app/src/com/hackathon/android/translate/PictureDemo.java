package com.hackathon.android.translate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class PictureDemo extends Activity {
	private SurfaceView preview = null;
	private SurfaceHolder previewHolder = null;
	private Camera camera = null;
	private boolean inPreview = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

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

	private Camera.Size getBestPreviewSize(int width, int height,
			Camera.Parameters parameters) {
		Camera.Size result = null;

		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;

					if (newArea > resultArea) {
						result = size;
					}
				}
			}
		}

		return (result);
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
			camera.startPreview();
			inPreview = true;
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
				new TranslateImage().translate(photo);
			} catch (java.io.IOException e) {
				Log.e("PictureDemo", "Exception in photoCallback", e);
			} catch (Exception e) {
				AlertDialog alertDialog = new AlertDialog.Builder(
						PictureDemo.this).create();
				alertDialog.setTitle("Error Occurred");
				alertDialog.setMessage("Exception - " + e.getMessage());
				alertDialog.show();
				Log.e("Pic Demo", "Upload Error", e);
			}

			return (null);
		}
	}
}