package aic.monitor.util;

import java.io.FileInputStream;
import java.util.Properties;

public class PropertyManager {
	private Properties properties = null;

	private PropertyManager() {
		properties = new Properties();
		try {
			properties.loadFromXML(new FileInputStream("properties.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Properties getProperties() {
		return properties;
	}

	public String getProperty(String key) {
		return this.properties.getProperty(key);
	}

	private static PropertyManager manager = null;

	public static PropertyManager getInstance() {
		if (manager == null) {
			manager = new PropertyManager();
		}

		return manager;
	}
}
