package com.hackathon.android.translate.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpUtils {

	private static HttpClient httpClient;
	static {
		httpClient = new DefaultHttpClient();
	}

	public static String getResponseAsString(HttpResponse response) {
		try{
		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		String sResponse;
		StringBuilder stringBuilder = new StringBuilder();
		while ((sResponse = reader.readLine()) != null) {
			stringBuilder = stringBuilder.append(sResponse);
		}
		return stringBuilder.toString();
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	public static HttpClient getHttpClient() {
		return httpClient;
	}

	public static String executeAndReturnStringResponse(HttpPost post) {
		try {
			return getResponseAsString(getHttpClient().execute(post));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
