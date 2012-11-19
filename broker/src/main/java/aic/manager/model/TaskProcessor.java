package aic.manager.model;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import aic.manager.interfaces.ITaskProcessor;
import aic.service.analyzer.IAnalyzer;

public class TaskProcessor implements ITaskProcessor {
	private IAnalyzer analyser;
	private BlockingQueue<SentimentTask> taskQueue = null;	
	private String subscriber;

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

	/*
	 * Notifies listeners about SentimentEvent.
	 */
	private void notifySentimentEvent(int id, double service) {
		URL url;
		try {
			// return the result to the website (not test!! change url
			// if
			// necessary)
			url = new URL(String.format("%s?id=%d&result=%f",
					subscriber, id, service));
			url.openStream().close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Async wrap method for notifySentimentEvent.
	 */
	private void asyncNotifySentimentEvent(final int id, final double service) {
		Runnable task = new Runnable() {

            @Override
            public void run() {
                try {
                	notifySentimentEvent(id, service);
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
				asyncNotifySentimentEvent(task.getId(), value);
			}
		}
	}

	@Override
	public void changeSubscriber(String serviceUrl) {
		this.subscriber = serviceUrl;		
	}

}
