package aic.manager.model;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import aic.manager.interfaces.ITaskNotifier;
import aic.manager.util.SentimentEvent;
import aic.manager.util.SentimentEventArgs;

public class TaskNotifier implements ITaskNotifier {
	public List<String> subscribers = new ArrayList<String>();

	@Override
	public void sentimentEvent(SentimentEvent arg0) throws IOException {
		notify(arg0.getArgs());
	}

	@Override
	public synchronized void addSubscriber(String name) {
		subscribers.add(name);
	}

	@Override
	public synchronized void removeSubscriber(String name) {
		subscribers.remove(name);
	}

	/*
	 * http://127.0.0.1:8080/website/postresults/
	 */
	private void notify(SentimentEventArgs args) {
		for (String subscriber : subscribers) {
			URL url;
			try {
				// return the result to the website (not test!! change url if
				// necessary)
				url = new URL(String.format("%s?id=%d&result=%f", subscriber,
						args.getId(), args.getValue()));
				url.openStream().close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
