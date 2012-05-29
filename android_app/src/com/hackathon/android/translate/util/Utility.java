package com.hackathon.android.translate.util;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
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
    private static int MAX_IMAGE_DIMENSION = 720;

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

    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break; // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
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
		sharedPreferences.edit().putString(Constants.ACCESS_TOKEN, facebook.getAccessToken()).commit();		
	}

	public static AsyncFacebookRunner getAsyncRunner() {
		return asyncRunner;
	}
}
