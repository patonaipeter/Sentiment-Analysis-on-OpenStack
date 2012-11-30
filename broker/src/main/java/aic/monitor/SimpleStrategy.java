package aic.monitor;

import java.io.IOException;
import java.util.List;

import org.openstack.nova.model.Server;

public class SimpleStrategy implements Strategy {
	private int lastDecision = 0;
	
	@Override
	public int getSleepTime() {
		if(lastDecision!=0){
			//give it some time
			return 2*60000;
		}
		return 10000;
	}

	@Override
	public int getReserveServers() {
		return 2;
	}

	@Override
	public int decide(List<ServerConnection> servers) throws IOException {
		double sum = 0;
		int count = 0;
		
		for(ServerConnection con : servers) {
			Server server = con.getServer();
			SSHMonitor ssh = con.getSsh();
			if(server!=null && ssh!=null && server.getStatus().equals("ACTIVE")){
				sum += ssh.getLoadAvg();
				count++;
			}
		}
		double mean = sum / count;
		if(mean>0.8){
			lastDecision=1;
		}else if(mean<0.3){
			lastDecision=-1;
		}else{
			lastDecision=0;
		}
		return lastDecision;
	}

}
