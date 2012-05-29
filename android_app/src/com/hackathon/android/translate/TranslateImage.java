package com.hackathon.android.translate;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class TranslateImage {
	String urlServer = "http://192.168.1.4:3000/upload";

	public String translate(File image) throws Exception {
		String output = uploadFile(image);
		return output;
	}

	private String uploadFile(File file) throws Exception {
		try {
			//ByteArrayOutputStream bos = new ByteArrayOutputStream();
			//bm.compress(CompressFormat.JPEG, 75, bos);
			//byte[] data = bos.toByteArray();
			//ByteArrayBody bab = new ByteArrayBody(data, "forest.jpg");
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(urlServer);
		    FileBody bin = new FileBody(file);
			MultipartEntity reqEntity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			reqEntity.addPart("uploadedFile", bin);
			reqEntity.addPart("photoCaption", new StringBody("Translate"));
			postRequest.setEntity(reqEntity);
			HttpResponse response = httpClient.execute(postRequest);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent(), "UTF-8"));
			String sResponse;
			StringBuilder s = new StringBuilder();

			while ((sResponse = reader.readLine()) != null) {
				s = s.append(sResponse);
			}
			System.out.println("Response: " + s);
			return s.toString();
		} catch (Exception e) {
			// handle exception here
			Log.e(e.getClass().getName(), e.getMessage());
		}
		return null;
	}
}
