package aic.monitor.ui;

import java.util.Properties;

import org.openstack.nova.model.Flavor;
import org.openstack.nova.model.Image;
import org.openstack.nova.model.Server;

import aic.monitor.LaunchMonitor;

public class StartUpMonitor {
	/**
	 * @param args
	 */

	/**
	 * Placeholder for later, we can use this variable to externally terminate the run-loop.
	 */
	static boolean running = true;

	public static void main(String[] args) {
		Properties properties = new Properties();
		try {
			properties.loadFromXML(ClassLoader.getSystemResourceAsStream("properties.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		LaunchMonitor monitor = new LaunchMonitor(properties);

		String flavorRef = null;
		String imgRef = null;

		while(running) {
			// The broker needs to perform a few steps in order to determine the current load
			// of the cloud and decide whether to start a new instance or stop a running instance
			// These steps should run in a loop that is active all the time while the broker is running.
			//1. Get the information of all running instances
			//   We need to manage a list of all instances (running and stopped), then we poll the active ones
			//   and retrieve the data from them, using the SshMonitor.
			/**
			 * double loadValue = monitor.getCloudMetrci();
			 */

			//2. Evaluate the load based on the data that was returned by the SshMonitor. It will be best to
			//   calculate some sort of metric from the information so we have a single value to decide if we
			//   need to start or stop an instance
			/**
			 *
			 */

			//3. We need to pass the value to a strategy class that decides if an instance needs to be started or
			//   stopped, particularly the strategy class needs to keep a history of the previous states the cloud was
			//   in, so we can compensate fluctuation (periodic starting/stopping of instances)


			//4. When we have decided on a suitable strategy then it needs to be executed and the right instance must be
			//   stopped or a new one must be started. Maybe we will need to handle the case somehow that no new instance can
			//   be started.
			/**
			 * Strategy strategy;
			 *
			 * strategy.execute();
			 */
		}

		// print all flavors
		flavorRef = getFlaverName(monitor, flavorRef);

		// print all images
		imgRef = getImageName(monitor, imgRef);

		// print all instances
		for (Server server : monitor.getServers()) {
			System.out.println(server);
		}

		if (!(imgRef == null && flavorRef == null)) {
			// start instance, the instance will be named mX with X being in [2,7]
			Server server = monitor.createServer("m" + 2,
					flavorRef, imgRef);
			// waiting for ACTIVE state
			Boolean isActive = false;
			while (!isActive) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				isActive = monitor.getServer(server.getId()).getStatus()
						.equals("ACTIVE");
			}
			// terminate instance
			monitor.terminateServer(server.getId());
		}

		// TODO: stop started instance
	}

	private static String getImageName(LaunchMonitor monitor, String imgRef) {
		for (Image image : monitor.getImages()) {
			if (image.getName().equals("Ubuntu 12.10 amd64")) {
				imgRef = image.getLinks().get(0).getHref();
			}
			System.out.println(image);
		}
		return imgRef;
	}

	private static String getFlaverName(LaunchMonitor monitor, String flavorRef) {
		for (Flavor flavor : monitor.getFlavors()) {
			if (flavor.getName().equals("m1.tiny.win"))
				flavorRef = flavor.getLinks().get(0).getHref();
			System.out.println(flavor);
		}
		return flavorRef;
	}
}
