package com.hackathon.android.translate.model;

import android.os.Parcel;
import android.os.Parcelable;

public class KeyValuePair implements Parcelable {
	private String key;
	private String value;

	public KeyValuePair() {
	}

	public KeyValuePair(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public static final Parcelable.Creator<KeyValuePair> CREATOR = new Parcelable.Creator<KeyValuePair>() {
		public KeyValuePair createFromParcel(Parcel source) {
			return new KeyValuePair(source.readString(), source.readString());
		}

		public KeyValuePair[] newArray(int size) {
			throw new UnsupportedOperationException();
		}
	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int ignored) {
		dest.writeString(key);
		dest.writeString(value);
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
	
}