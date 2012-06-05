package com.hackathon.android.translate.constant;

public interface Constants {
	String ACCESS_TOKEN = "access_token";
	String FACEBOOK_APP_ID = "378619692194435";
	String C2DM_REGISTRATION_ID = "registration_id";
    String SERVER_URL = "http://10.16.3.215:3000";
	// String SERVER_URL = "http://192.168.1.4:3000";
	//String SERVER_URL = "http://192.168.43.204:3000";
	String BACKOFF = "backoff";
	String RECEIVER = "receiver";
	String REST_ACTION = "restPath";
	String REST_QUERY_DATA = "restQueryData";
	int STATUS_FINISHED = 0;
	int STATUS_ERROR = 1;
	String RESULT = "result";
	String BUCKET_NAME = "facebookandroidtranslator";
	String BUCKET_URL = "http://" + BUCKET_NAME + ".s3.amazonaws.com/";
	String UPLOADED_FILE_NAME = "uploaded_file_name";
	String FACEBOOK_ID = "facebook_id";

	public interface REST_URL_ACTIONS {
		String UPLOAD_FILE = "/upload";
		String REGISTER = "/register";
		String FRIENDS = "/friends";
		String IMAGES = "/images";
	}
}
