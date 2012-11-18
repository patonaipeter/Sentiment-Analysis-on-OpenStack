package aic.manager.interfaces;

public interface ITaskNotifier extends SentimentEventListener {
	void addSubscriber(String name);

	void removeSubscriber(String name);
}
