package aic.manager.util;

import java.util.EventObject;

public class SentimentEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SentimentEventArgs args = null;

	public SentimentEvent(Object arg0) {
		super(arg0);
		args = (SentimentEventArgs) arg0;
	}

	public SentimentEventArgs getArgs() {
		return args;
	}

	public void setArgs(SentimentEventArgs args) {
		this.args = args;
	}

}
