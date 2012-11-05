package aic.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IService extends Remote{
	public double analyseSentiment(String company) throws RemoteException;

}
