package com.hackathon.android.translate.util;

import static java.util.UUID.randomUUID;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;

import com.amazonaws.services.s3.model.PutObjectRequest;
import com.hackathon.android.translate.constant.Constants;
import com.hackathon.android.translate.model.KeyValuePair;
import com.hackathon.android.translate.s3.S3;
import com.hackathon.android.translate.service.RestfulService;

public class UploadImage {
	String serverUploadUrl = Constants.SERVER_URL + "/upload";

	public static void upload(File file, Context context) throws Exception {
		String fileName = uploadFileToS3(file);
		Parcelable[] value = new KeyValuePair[2];
		//value[0] = new KeyValuePair(Constants.FACEBOOK_ID, Utility.getFacebookId());
		value[0] = new KeyValuePair(Constants.ACCESS_TOKEN, Utility.getAccessToken());
		value[1] = new KeyValuePair(Constants.UPLOADED_FILE_NAME, fileName);
		
		Intent intent = new Intent(Intent.ACTION_SYNC, null, context, RestfulService.class);
		intent.putExtra(Constants.REST_ACTION, Constants.REST_URL_ACTIONS.UPLOAD_FILE);
		intent.putExtra(Constants.REST_QUERY_DATA, value);
		context.startService(intent);
	}

	protected static String uploadFileToS3(File file) {
		S3UploadTask uploadTask = new S3UploadTask();
		String fileName = getFileName();
		uploadTask.execute(new PutObjectRequest(Constants.BUCKET_NAME, fileName, file));
		return fileName;
	}

	private static String getFileName() {
		return randomUUID().toString() + ".jpg";
	}

	private static class S3UploadTask extends AsyncTask<PutObjectRequest, Long, Long> {
		protected Long doInBackground(PutObjectRequest... reqs) {
			try {
				S3.getInstance().putObject(reqs[0]);
			} catch (Exception e) {
				return 0L;
			}
			return 1L;
		}
	}

}
