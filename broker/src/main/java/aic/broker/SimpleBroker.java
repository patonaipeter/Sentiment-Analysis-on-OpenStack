package aic.broker;

import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import aic.service.IService;

public class SimpleBroker implements IService{
	private List<IService> services=new ArrayList<IService>();
	
	public SimpleBroker() throws MalformedURLException, RemoteException, NotBoundException {
    	//this simple demo broker has just one Worker Service on localhost which is fixed
    	IService service=(IService)Naming.lookup("rmi://localhost/TSA");
    	services.add(service);
	}
	
	public static void main(String[] args) throws Exception {
        try {
            String name = "Broker";
            SimpleBroker broker=new SimpleBroker();
            IService stub =(IService) UnicastRemoteObject.exportObject(broker, 0);
            Registry registry = null;
            try{
            	//get existing registry
            	registry = LocateRegistry.getRegistry();
            	registry.rebind(name, stub);
            }catch(RemoteException e){
            	//create new registry
            	registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            	registry.rebind(name, stub);
            }
            System.out.println("Broker Service ready on port: " + Registry.REGISTRY_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }

	}
	
	private synchronized IService getAvailableService(){
		//TODO implement real broker
		return services.get(0);
	}

	//gets called from the frontend
	public double analyseSentiment(String company) throws RemoteException {
		return getAvailableService().analyseSentiment(company);
	}

	public double getSystemLoad(){
		return ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
	}
	
	public long getUsedMemory(){
		Runtime rt = Runtime.getRuntime();
		return rt.totalMemory()-rt.freeMemory();
	}

	public boolean isBusy(){
		return false;
	}

	@Override
	public double analyseSentiment(String company, int split, int index)
			throws RemoteException {
		return getAvailableService().analyseSentiment(company,split,index);
	}
	
}
