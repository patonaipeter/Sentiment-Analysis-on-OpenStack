package aic.manager.interfaces;

import java.rmi.RemoteException;
import java.util.TooManyListenersException;

import aic.manager.model.SentimentTask;

public interface ITaskProcessor extends Runnable {
	void addSentimentTask(SentimentTask task) throws RemoteException;

	void addEventListener(SentimentEventListener arg0)
			throws TooManyListenersException;

	void removeEventListeners();
}
