package com.hackathon.facebook;

public class Friend {
	private String id;
	private String name;

	public Friend(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getProfileImagePath(){
		return "http://graph.facebook.com/" + id + "/picture";
	}
}
