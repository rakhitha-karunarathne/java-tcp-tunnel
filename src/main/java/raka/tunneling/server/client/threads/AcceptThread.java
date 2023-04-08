package raka.tunneling.server.client.threads;


import java.net.SocketTimeoutException;
import java.util.logging.Logger;

import raka.tunneling.server.client.dto.TunnelClientConnection;
import raka.tunneling.server.client.service.TunnelClientService;
import raka.tunneling.server.util.AbortException;

public class AcceptThread extends Thread{
	TunnelClientConnection connection;
	TunnelClientService service;
	static Logger LOGGER = Logger.getLogger(AcceptThread.class.getName());
	
	public AcceptThread(TunnelClientConnection connection, TunnelClientService service) {
		this.connection = connection;
		this.service = service;
	}
	public void run() {
		LOGGER.info("start: " + connection.getPublicPort());
		try {
			while(connection.isActive()) {
				try {
					service.accept(connection);
					LOGGER.info("accept new connection: " + connection.getPublicPort());
				}
				catch(SocketTimeoutException timeout) {
					LOGGER.info("timeout: " + connection.getPublicPort());
				}
				catch(AbortException ex) {
					ex.printStackTrace();
				}
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			try {
				service.close(connection);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		finally {
			this.connection.setActive(false);
			LOGGER.info("exit: " + connection.getPublicPort());
		}
	}
	
	public AcceptThread startThread() {
		this.start();
		return this;
	}
}
