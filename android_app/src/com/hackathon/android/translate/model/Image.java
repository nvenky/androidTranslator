package com.hackathon.android.translate.model;

import java.io.Serializable;

import com.hackathon.android.translate.constant.Constants;

public class Image implements Serializable {

	private long id;
	private String userName;
	private String fileName;

	public Image(long id, String userName, String fileName) {
		super();
		this.id = id;
		this.userName = userName;
		this.fileName = fileName;
	}

	public String getUserName() {
		return userName;
	}

	public String getFileName() {
		return fileName;
	}

	public String getFileURL() {
		return Constants.BUCKET_URL + fileName;
	}

	public long getId() {
		return id;
	}
}
