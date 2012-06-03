package com.hackathon.android.translate.util;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;

import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.preference.PreferenceManager;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.hackathon.android.translate.constant.Constants;

public class Utility extends Application {

	private static Facebook facebook;
	private static SharedPreferences sharedPreferences;
    private static AsyncFacebookRunner asyncRunner;
    public static JSONObject mFriendsList;
    public static String userUID = null;
    public static String objectID = null;
    public static AndroidHttpClient httpclient = null;
    public static Hashtable<String, String> currentPermissions = new Hashtable<String, String>();
    private static final long DEFAULT_BACKOFF = 30000;

    public static Bitmap getBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(new FlushedInputStream(is));
            bis.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpclient != null) {
                httpclient.close();
            }
        }
        return bm;
    }

	public static void loadComponents(Context context) {
		facebook = new Facebook(Constants.FACEBOOK_APP_ID);
		sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(context);
		asyncRunner = new AsyncFacebookRunner(facebook);
	}
	
	public static void loginWithExistingToken(){
		String accessToken = sharedPreferences.getString(Constants.ACCESS_TOKEN, null);
		long expires = sharedPreferences.getLong("access_expires", 0);
		if (accessToken != null) {
			getFacebook().setAccessToken(accessToken);
		}
		if (expires != 0) {
			getFacebook().setAccessExpires(expires);
		}
	}

	public static Facebook getFacebook() {
		return facebook;
	}

	public static void updateAccessToken() {
		updateSharedPreferences(Constants.ACCESS_TOKEN, facebook.getAccessToken());		

	}
	
	public static void updateC2DMRegistration(String registrationId) {
		updateSharedPreferences(Constants.C2DM_REGISTRATION_ID, registrationId);		
	}

	public static void clearC2DMRegistration() {
		sharedPreferences.edit().remove(Constants.C2DM_REGISTRATION_ID).commit();		
	}

	public static AsyncFacebookRunner getAsyncRunner() {
		return asyncRunner;
	}
	
	public static void updateBackoff(long backoffTimeInMs) {
		updateSharedPreferences(Constants.BACKOFF, backoffTimeInMs);		
	}

	public static long getBackoff() {
		return sharedPreferences.getLong(Constants.BACKOFF, DEFAULT_BACKOFF);
	}
	
	public static String getAccessToken() {
		return sharedPreferences.getString(Constants.ACCESS_TOKEN, null);
	}
	
	public static String getFacebookId() {
		return sharedPreferences.getString(Constants.FACEBOOK_ID, null);
	}
	
	private static void updateSharedPreferences(String key, String value){
		sharedPreferences.edit().putString(key, value).commit();
	}
	private static void updateSharedPreferences(String key, long value){
		sharedPreferences.edit().putLong(key, value).commit();
	}

	public static void updateFacebookId() {
		JSONObject json;
		try {
			json = new JSONObject(facebook.request("me"));
			String userId = json.getString("id");
			updateSharedPreferences(Constants.FACEBOOK_ID, userId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} 		
	}
}
