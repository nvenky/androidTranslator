package com.hackathon.android.translate.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.hackathon.android.translate.R;
import com.hackathon.android.translate.util.UploadImage;

public class CameraPreviewActivity extends Activity {
	private SurfaceView preview = null;
	private SurfaceHolder previewHolder = null;
	private boolean inPreview = false;
	private int cameraDisplayOrientaton = 0;
	private int backCameraId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.camera_preview);

		preview = (SurfaceView) findViewById(R.id.cameraPreview);
		previewHolder = preview.getHolder();
		setBackCameraId();
		cameraPreview = new CameraPreview(getApplicationContext());
		previewHolder.addCallback(cameraPreview);
		cameraPreview.setCamera(Camera.open(backCameraId));
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	private void setBackCameraId() {
		CameraInfo cameraInfo = new CameraInfo();
		for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
				backCameraId = i;
			}
		}

	}

	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio = (double) w / h;
		if (sizes == null)
			return null;

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

	@Override
	public void onResume() {
		super.onResume();
		cameraPreview.startPreview();
	}

	@Override
	public void onPause() {
		super.onPause();
		cameraPreview.releaseCamera();
	}

	public void takePicture(View view) {
		cameraPreview.takePicture();
	}

	class CameraPreview extends ViewGroup implements SurfaceHolder.Callback {

		public CameraPreview(Context context) {
			super(context);
		}

		public void takePicture() {
			if (inPreview) {
				camera.autoFocus(autoFocusCallback);
				inPreview = false;
			}
		}

		private static final String TAG = "CameraPreview";
		private Size previewSize;
		private List<Size> supportedPreviewSizes;
		private Camera camera;

		public void surfaceCreated(SurfaceHolder holder) {
			try {
				if (camera != null) {
					camera.setPreviewDisplay(holder);
				}
			} catch (IOException exception) {
				stopPreview();
				Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
			}
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			// We purposely disregard child measurements because act as a
			// wrapper to a SurfaceView that centers the camera preview instead
			// of stretching it.
			final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
			final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
			setMeasuredDimension(width, height);

			if (supportedPreviewSizes != null) {
				previewSize = getOptimalPreviewSize(supportedPreviewSizes, width, height);
			}
		}

		public void setCamera(Camera cameraObj) {
			this.camera = cameraObj;
			Parameters parameters = camera.getParameters();
			supportedPreviewSizes = parameters.getSupportedPreviewSizes();
			requestLayout();
		}

		private void startPreview() {
			if (camera == null) {
				setCamera(Camera.open(backCameraId));
			}
			camera.startPreview();
			inPreview = true;
		}

		private void releaseCamera() {
			stopPreview();
			if (camera != null) {
				camera.release();
				camera = null;
			}
		}

		private void stopPreview() {
			if (inPreview && camera != null) {
				camera.stopPreview();
				inPreview = false;
			}
		}

		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b) {
			if (changed && getChildCount() > 0) {
				final View child = getChildAt(0);

				final int width = r - l;
				final int height = b - t;

				int previewWidth = width;
				int previewHeight = height;
				if (previewSize != null) {
					previewWidth = previewSize.width;
					previewHeight = previewSize.height;
				}

				// Center the child SurfaceView within the parent.
				if (width * previewHeight > height * previewWidth) {
					final int scaledChildWidth = previewWidth * height / previewHeight;
					child.layout((width - scaledChildWidth) / 2, 0, (width + scaledChildWidth) / 2, height);
				} else {
					final int scaledChildHeight = previewHeight * width / previewWidth;
					child.layout(0, (height - scaledChildHeight) / 2, width, (height + scaledChildHeight) / 2);
				}
			}
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			stopPreview();
			Camera.Parameters parameters = camera.getParameters();
			requestLayout();
			camera.setParameters(parameters);
			setCameraDisplayOrientation();
			startPreview();

		}

		private void setCameraDisplayOrientation() {
			Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

			if (display.getRotation() == Surface.ROTATION_0) {
				camera.setDisplayOrientation(90);
				cameraDisplayOrientaton = 90;
			}

			if (display.getRotation() == Surface.ROTATION_90) {
				camera.setDisplayOrientation(0);
				cameraDisplayOrientaton = 0;
			}

			if (display.getRotation() == Surface.ROTATION_180) {
				camera.setDisplayOrientation(270);
				cameraDisplayOrientaton = 270;
			}

			if (display.getRotation() == Surface.ROTATION_270) {
				camera.setDisplayOrientation(180);
				cameraDisplayOrientaton = 180;
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			releaseCamera();
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
			// camera.startPreview();
			// inPreview = true;
		}
	};
	private CameraPreview cameraPreview;

	class SavePhotoTask extends AsyncTask<byte[], String, String> {
		@Override
		protected String doInBackground(byte[]... data) {
			Bitmap thePicture = getRotatedPictureBasedOnOrientation(data);
			File photo = getPhotoFile();
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				thePicture.compress(CompressFormat.JPEG, 75, bos);
				FileOutputStream fos = new FileOutputStream(photo.getPath());
				fos.write(bos.toByteArray());
				fos.close();
				UploadImage.upload(photo, CameraPreviewActivity.this);
			} catch (java.io.IOException e) {
				Log.e(this.getClass().getName(), "Exception in photoCallback", e);
			} catch (Exception e) {
				Log.e(this.getClass().getName(), "Upload Error", e);
			}
			return (null);
		}

		protected Bitmap getRotatedPictureBasedOnOrientation(byte[]... data) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;
			Bitmap thePicture = BitmapFactory.decodeByteArray(data[0], 0, data[0].length, options);			
			//return rotatePicture(thePicture);
			return thePicture;
		}

		protected Bitmap rotatePicture(Bitmap thePicture) {
			Matrix matrix = new Matrix();
			matrix.postRotate(cameraDisplayOrientaton);
			thePicture = Bitmap.createBitmap(thePicture, 0, 0, thePicture.getWidth(), thePicture.getHeight(), matrix,
					true);
			return thePicture;
		}

		protected File getPhotoFile() {
			File photo = new File(Environment.getExternalStorageDirectory(), "translator_photo.jpg");
			if (photo.exists()) {
				photo.delete();
			}
			return photo;
		}
	}
}