package aic.manager;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IManager extends Remote{
	void addSentimentAnalysisTask(TaskDTO task) throws RemoteException;
}
