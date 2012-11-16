package aic.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IService extends Remote{
	double analyseSentiment(String company) throws RemoteException;
	
	double analyseSentiment(String company,int split,int index) throws RemoteException;

	long getUsedMemory() throws RemoteException;

	boolean isBusy() throws RemoteException;

	double getSystemLoad() throws RemoteException;

}
