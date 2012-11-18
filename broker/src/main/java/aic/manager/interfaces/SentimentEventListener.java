package aic.manager.interfaces;

import java.io.IOException;

public interface SentimentEventListener extends java.util.EventListener {
	public abstract void sentimentEvent(aic.manager.util.SentimentEvent arg0)
			throws IOException;
}
