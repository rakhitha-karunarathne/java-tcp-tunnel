package raka.tunneling.server.client.threads;



import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import lombok.Getter;
import lombok.Setter;
import raka.tunneling.server.client.dto.ChannelConnection;
import raka.tunneling.server.client.service.TunnelClientService;


public class ChannelThread extends Thread{
	static Logger LOGGER = Logger.getLogger(ChannelThread.class.getName());
	
	@Getter
	@Setter
	ChannelConnection connection;
	@Getter
	@Setter
	TunnelClientService service;
	
	public ChannelThread(ChannelConnection connection, TunnelClientService service) {
		this.connection = connection;
		this.service = service;
	}	
	
	public void processChannel() throws Exception{
		
	}
	
	public void run() {
		LOGGER.info("start: " + this.getClass().getName());
		try {
			processChannel();
		}
		catch(Exception ex) {
			ex.printStackTrace();
			try {
				getService().close(getConnection());
			} catch (Exception e) {				
				e.printStackTrace();
			}		
		}
		finally {
			if(getConnection().threadStopped(this) <=0 ) {
				try {
					getService().close(getConnection());
				} catch (URISyntaxException | IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
			LOGGER.info("exit: " + this.getClass().getName());
		}
	}
	
	public ChannelThread startThread() {
		this.start();
		return this;
	}
}
