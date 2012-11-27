package aic.monitor.ui;

import java.io.FileInputStream;
import java.util.Properties;

import org.openstack.nova.model.Flavor;
import org.openstack.nova.model.Image;
import org.openstack.nova.model.Server;

import aic.monitor.LaunchMonitor;

public class StartUpMonitor {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Properties properties = new Properties();
		try {
			properties.loadFromXML(new FileInputStream("properties.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		LaunchMonitor monitor = new LaunchMonitor(properties);

		String flavorRef = null;
		String imgRef = null;

		// print all flavors
		for (Flavor flavor : monitor.getFlavors()) {
			if (flavor.getName().equals("m1.tiny.win"))
				flavorRef = flavor.getLinks().get(0).getHref();
			System.out.println(flavor);
		}

		// print all images
		for (Image image : monitor.getImages()) {
			if (image.getName().equals("Ubuntu 12.10 amd64")) {
				imgRef = image.getLinks().get(0).getHref();
			}
			System.out.println(image);
		}

		// print all instances
		for (Server server : monitor.getServers()) {
			System.out.println(server);
		}

		if (!(imgRef == null && flavorRef == null)) {
			// start instance
			Server server = monitor.createServer("new-server-from-java",
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
}
