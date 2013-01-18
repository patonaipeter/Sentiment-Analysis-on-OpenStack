package aic.monitor;

import java.io.IOException;
import java.util.List;

import org.openstack.nova.model.Server;

/**
 * -- Utilization Avoid Violations Strategy --
 * 
 * Metric: avgLoad (5 min)
 * 
 * Idea: compute utility function and based on it make decisions concerning VMs
 * allocations. If required utility won't be achieved through re-allocation, do
 * nothing.
 * 
 * Similar to Utilization Strategy (US). Difference: If number of servers in US
 * achieved a point, when it's useless to add new servers, new servers could be
 * added just if cpu queue was violated (mean > 1) (and the same for suspending
 * servers).
 * 
 * This strategy tries to avoid violations by computing real utility by adding
 * more than 1 server. It doesn't add more than 1 server per time, but it
 * supposes that it can add one more server on next step.
 * 
 * @author grig
 * 
 */
public class UtilizationAvoidViolationsStrategy implements Strategy {
	private final float highThreshold = 0.7f;
	private final float lowThreshold = 0.5f;

	// number of currently suspending servers
	private int suspendingServers = 0;
	// number of currently resuming servers
	private int resumingServers = 0;
	// time for all servers to be finally suspended
	private int allSuspendedTime = 0;
	// time for all servers to be finally resumed
	private int allResumedTime = 0;

	@Override
	public int getSleepTime() {
		return 60000; // 1 min
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
		int decision = 0;
		if (mean > highThreshold && count != Strategy.SERVER_MAX_COUNT
				&& suspendingServers == 0) {
			double requiredUtility = mean / highThreshold;
			double realUtility = Strategy.SERVER_MAX_COUNT
					/ (count + resumingServers);
			if (mean > 1.0f || realUtility >= requiredUtility) {
				decision = 1;
				resumingServers += 1;
				allResumedTime = Strategy.VM_ALLOCATE_TIME;
			}
		} else if (mean < lowThreshold && resumingServers == 0) {
			double requiredUtility = lowThreshold / mean;
			double realUtility = this.getReserveServers()
					/ (count - suspendingServers);
			if (realUtility >= requiredUtility) {
				decision = -1;
				suspendingServers += 1;
				allSuspendedTime = Strategy.VM_ALLOCATE_TIME;
			}
		} else {
			decision = 0;
		}

		manageTime();
		return decision;
	}

	private void manageTime() {
		final int sleepTime = this.getSleepTime();
		allResumedTime = allResumedTime - sleepTime >= 0 ? allResumedTime
				- sleepTime : 0;
		allSuspendedTime = allSuspendedTime - sleepTime >= 0 ? allSuspendedTime
				- sleepTime : 0;

		if (allResumedTime == 0) {
			resumingServers = 0;
		}

		if (allSuspendedTime == 0) {
			suspendingServers = 0;
		}
	}
}
