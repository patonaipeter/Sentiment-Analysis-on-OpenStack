package aic.data.dto;

import java.util.Date;

public class Tweet {
	private String username;
	private String text;
	private Date timestamp;

	public Tweet(String username, String text, Date timestamp) {
		this.username = username;
		this.text = text;
		this.timestamp = timestamp;
	}

	public String getUsername() {
		return this.username;
	}

	public String getText() {
		return this.text;
	}

	public Date getTimestamp() {
		return this.timestamp;
	}
}
