package aic.manager.model;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import aic.manager.interfaces.ITaskProcessor;
import aic.manager.interfaces.SentimentEventListener;
import aic.manager.util.SentimentEvent;
import aic.manager.util.SentimentEventArgs;

public class TaskProcessor implements ITaskProcessor {
	private IAnalyser analyser;
	private BlockingQueue<SentimentTask> taskQueue = null;
	private List<SentimentEventListener> listeners = new ArrayList<SentimentEventListener>();

	public TaskProcessor(IAnalyser analyser) {
		this.analyser = analyser;
		taskQueue = new LinkedBlockingQueue<SentimentTask>();
	}

	@Override
	public void run() {
		processTasks();
	}

	@Override
	public void addSentimentTask(SentimentTask task) {
		try {
			taskQueue.put(task);
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RemoteException(e.getMessage());
		}
	}

	@Override
	public void addEventListener(SentimentEventListener arg0)
			throws TooManyListenersException {
		listeners.add(arg0);
	}

	@Override
	public void removeEventListeners() {
		listeners.clear();
	}

	/*
	 * Notifies listeners about SentimentEvent.
	 */
	private void notifySentimentEvent(SentimentEventArgs args) {
		for (SentimentEventListener l : listeners) {
			l.sentimentEvent(new SentimentEvent(args));
		}
	}

	private void processTasks() throws InterruptedException, IOException {
		while (true) {
			SentimentTask task = taskQueue.take();
			if (task != null) {
				// compute value
				double value = this.analyser.analyse(task.getSearch());
				// notifies about 
				notifySentimentEvent(new SentimentEventArgs(task.getId(), value));
			}
		}
	}

}
