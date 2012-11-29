package aic.monitor;

import java.io.IOException;
import java.util.List;

public interface Strategy {
	//returns the time in milliseconds that the thread should sleep
	int getSleepTime();
	
	//returns the number of servers that should be kept as a reserver
	int getReserveServers();
	
	//returns the number of new servers
	//e.g.: 1 add one
	//		0 do nothing
	//		-1 remove one
	int decide(List<ServerConnection> servers) throws IOException;
}
