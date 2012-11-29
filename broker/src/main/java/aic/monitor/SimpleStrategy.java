package aic.monitor;

import java.io.IOException;
import java.util.List;

public class SimpleStrategy implements Strategy {
	private int lastDecision = 0;
	
	@Override
	public int getSleepTime() {
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
		
		for(ServerConnection s : servers) {
			if(s.getServer().getStatus().equals("ACTIVE") && s.getSsh()!=null){
				sum += s.getSsh().getLoadAvg();
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
