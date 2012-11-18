package aic.manager.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SentimentTask implements Serializable {
	private int id;
	private String search;
	
	public SentimentTask(int id, String search) {
		super();
		this.id = id;
		this.search = search;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}
}
