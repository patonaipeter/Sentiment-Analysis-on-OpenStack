package aic.monitor;

import java.io.IOException;
import java.util.List;

import org.openstack.nova.model.Server;

/**
 * -- Self-Based Threshold Strategy --
 * 
 * It is computed avgLoad(5 min) during avery step. Strategy has movable between
 * own min and max high and low thresholds
 * 
 * Rules: 1. if cpu queue was violated (mean > 1), it means that diff between
 * highThreshold and 1 is big and highThreshold should be decreased. new
 * instance should be resumed on.
 * 
 * 2. if a certain instance was suspended, low threshold should be decreased in
 * order to decrease number of actions under VMs in future.
 * 
 * 3. otherwise thresholds should be increased to increase avgLoad
 * 
 * Goal: to make a dynamically spread between low and high thresholds and hold
 * system avg workload within it.
 * 
 * @author grig
 * 
 */
public class SelfBasedThresholdStrategy implements Strategy {
	private float highThreshold = 0.8f;
	private final float maxHighThreshold = 0.95f;
	private final float minHighThreshold = 0.65f;
	private float lowThreshold = 0.5f;
	private final float maxLowThreshold = 0.65f;
	private final float minLowThreshold = 0.35f;
	private final float thresholdCoef = 0.05f;

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
		if (mean > highThreshold && count != Strategy.SERVER_MAX_COUNT) {
			decision = 1;
			// try to decrease number of cpu queue violations
			if (mean > 1.0f) {
				float diff = (highThreshold - minHighThreshold) * thresholdCoef;
				highThreshold -= highThreshold - diff >= minHighThreshold ? diff
						: 0;
			}
		} else if (mean < lowThreshold) {
			decision = -1;
			// try to decrease number of suspend/resume actions
			float diff = (lowThreshold - minLowThreshold) * thresholdCoef;
			lowThreshold -= lowThreshold - diff >= minLowThreshold ? diff : 0;
		} else {
			// increase both thresholds
			float diffHigh = (maxHighThreshold - highThreshold) * thresholdCoef;
			highThreshold += highThreshold + diffHigh <= maxHighThreshold ? diffHigh
					: 0;
			float diffLow = ((float) mean - lowThreshold) * thresholdCoef;
			lowThreshold += lowThreshold + diffLow <= maxLowThreshold ? diffLow
					: 0;
		}

		return decision;
	}

}
