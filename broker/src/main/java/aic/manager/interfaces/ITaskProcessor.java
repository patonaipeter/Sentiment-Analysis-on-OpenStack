package aic.manager.interfaces;

import java.util.TooManyListenersException;

import aic.manager.model.SentimentTask;

public interface ITaskProcessor extends Runnable {
	void addSentimentTask(SentimentTask task);

	void addEventListener(SentimentEventListener arg0)
			throws TooManyListenersException;

	void removeEventListeners();
}
