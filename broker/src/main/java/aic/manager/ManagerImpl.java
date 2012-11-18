package aic.manager;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import aic.monitor.IMonitor;
import aic.monitor.Monitor;

public class ManagerImpl implements IManager, Runnable {
	private BlockingQueue<TaskDTO> taskQueue=new LinkedBlockingQueue<TaskDTO>();
	private Thread processingTaskThread;
	private Thread monitorThread;
	// mocked
	private IMonitor monitorObj = new Monitor();
	
	@Override
	public void addSentimentAnalysisTask(TaskDTO task)
			throws RemoteException {
		try {
			taskQueue.put(task);
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RemoteException(e.getMessage());
		}
	}
		
	public void startProcessTasks(){
		processingTaskThread = new Thread(this);
		processingTaskThread.run();
	}
	
	public void startMonitor(){
		monitorThread = new Thread(monitorObj);
		monitorThread.run();
	}
	
	private void processTasks() throws InterruptedException, IOException{
		
		while(true){
			TaskDTO task=taskQueue.take();		
			
			double result=0.0;
			/* submit the tasks to mongodb */
			
			//return the result to the website (not testet!! change url if necessary)
            URL url = new URL("http://127.0.0.1:8080/website/postresults/?id="+task.getId()+"&result=" + result);
            url.openStream().close();
		}
	}

	@Override
	public void run() {
		try {
			processTasks();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws InterruptedException, IOException{
        try {
            String name = "Manager";
            ManagerImpl manager=new ManagerImpl();
            IManager stub =(IManager) UnicastRemoteObject.exportObject(manager, 0);
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
            System.out.println("Manager Service ready on port: " + Registry.REGISTRY_PORT);
            
            // start mocked monitor            
            manager.startMonitor();
            
            //submit the tasks to mongodb
            manager.startProcessTasks();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
