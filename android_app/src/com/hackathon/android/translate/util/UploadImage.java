package com.hackathon.android.translate.util;

import java.io.File;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import com.hackathon.android.translate.constant.Constants;

public class UploadImage {
	String serverUploadUrl = Constants.SERVER_URL + "/upload";

	public String upload(File file) throws Exception {
		HttpClient httpClient = HttpUtils.getHttpClient();
		HttpPost postRequest = new HttpPost(serverUploadUrl);
		MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		reqEntity.addPart("access_token", new StringBody(Utility.getAccessToken()));
		FileBody bin = new FileBody(file);
		reqEntity.addPart("uploadedFile", bin);
		postRequest.setEntity(reqEntity);
		HttpResponse response = httpClient.execute(postRequest);
		return HttpUtils.getResponseAsString(response);
	}

}
