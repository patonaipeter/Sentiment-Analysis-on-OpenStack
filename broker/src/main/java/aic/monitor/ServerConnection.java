package aic.monitor;

import org.openstack.nova.model.Server;

public class ServerConnection {
	private Server server;
	private SSHMonitor ssh;
	private boolean primary;
	
	public ServerConnection(Server server, SSHMonitor ssh) {
		super();
		this.server = server;
		this.ssh = ssh;
		this.primary = false;
	}
	
	public ServerConnection(Server server, SSHMonitor ssh, boolean primary) {
		super();
		this.server = server;
		this.ssh = ssh;
		this.primary = primary;
	}
	
	public synchronized Server getServer() {
		return server;
	}
	public synchronized void setServer(Server server) {
		this.server = server;
	}
	public synchronized SSHMonitor getSsh() {
		return ssh;
	}
	public synchronized void setSsh(SSHMonitor ssh) {
		this.ssh = ssh;
	}
	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}
	
	
}
