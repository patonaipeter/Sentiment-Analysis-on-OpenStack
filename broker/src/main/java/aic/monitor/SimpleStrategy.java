package aic.monitor;

import java.io.IOException;
import java.util.List;

import org.openstack.nova.model.Server;

public class SimpleStrategy implements Strategy {
	private int lastDecision = 0;
	private final float highThreshold = 0.8f;
	private final float lowThreshold = 0.5f;

	@Override
	public int getSleepTime() {
		if (lastDecision != 0) {
			// give it some time
			return 12000;
		}
		return 2000;
	}

	@Override
	public int getReserveServers() {
		return 1;
	}

	@Override
	public int decide(List<ServerConnection> servers) throws IOException {
		double sum = 0;
		int count = 0;

		for (ServerConnection con : servers) {
			Server server = con.getServer();
			SSHMonitor ssh = con.getSsh();
			if (server != null && ssh != null
					&& server.getStatus().equals("ACTIVE")) {
				sum += ssh.getLoadAvg();
				count++;
			}
		}
		double mean = sum / count;
		if (mean > highThreshold && count != Strategy.SERVER_MAX_COUNT) {
			lastDecision = 1;
		} else if (mean < lowThreshold) {
			lastDecision = -1;
		} else {
			lastDecision = 0;
		}
		return lastDecision;
	}

}
