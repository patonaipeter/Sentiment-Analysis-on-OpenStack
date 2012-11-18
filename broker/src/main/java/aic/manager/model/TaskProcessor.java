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
import aic.service.analyzer.IAnalyzer;

public class TaskProcessor implements ITaskProcessor {
	private IAnalyzer analyser;
	private BlockingQueue<SentimentTask> taskQueue = null;
	private List<SentimentEventListener> listeners = new ArrayList<SentimentEventListener>();

	public TaskProcessor(IAnalyzer analyser) {
		this.analyser = analyser;
		taskQueue = new LinkedBlockingQueue<SentimentTask>();
	}

	@Override
	public void run() {
		try {
			processTasks();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addSentimentTask(SentimentTask task) throws RemoteException {
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
			try {
				l.sentimentEvent(new SentimentEvent(args));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * Async wrap method for notifySentimentEvent.
	 */
	private void asyncNotifySentimentEvent(final SentimentEventArgs args) {
		Runnable task = new Runnable() {

            @Override
            public void run() {
                try {
                	notifySentimentEvent(args);
                } catch (Exception ex) {
                    //handle error which cannot be thrown back
                }
            }
        };
        new Thread(task).start(); 
	}

	private void processTasks() throws InterruptedException, IOException {
		while (true) {
			SentimentTask task = taskQueue.take();
			if (task != null) {
				// compute value
				double value = this.analyser.analyze(task.getSearch());
				// notifies about 
				asyncNotifySentimentEvent(new SentimentEventArgs(task.getId(), value));
			}
		}
	}

}
