package aic.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IService extends Remote{
	double analyseSentiment(String company) throws RemoteException;

	long getUsedMemory() throws RemoteException;

	boolean isBusy() throws RemoteException;

	double getSystemLoad() throws RemoteException;

}
