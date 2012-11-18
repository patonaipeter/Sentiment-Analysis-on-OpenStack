package aic.manager.util;

public class SentimentEventArgs {
	private double value;
	private int id;
	
	public SentimentEventArgs(int id, double value) {
		super();
		this.value = value;
		this.id = id;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
