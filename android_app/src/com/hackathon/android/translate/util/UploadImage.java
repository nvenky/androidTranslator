package com.hackathon.android.translate.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class UploadImage {
	String urlServer = "http://192.168.1.4:3000/upload";

	public String upload(File file) throws Exception {
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(urlServer);
			FileBody bin = new FileBody(file);
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			reqEntity.addPart("uploadedFile", bin);
			postRequest.setEntity(reqEntity);
			HttpResponse response = httpClient.execute(postRequest);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			String sResponse;
			StringBuilder s = new StringBuilder();

			while ((sResponse = reader.readLine()) != null) {
				s = s.append(sResponse);
			}
			return s.toString();
		} catch (Exception e) {
			Log.e(e.getClass().getName(), e.getMessage());
		}
		return null;
	}
}
