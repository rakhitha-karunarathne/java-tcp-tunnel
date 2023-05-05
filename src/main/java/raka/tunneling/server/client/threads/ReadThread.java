package raka.tunneling.server.client.threads;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import raka.tunneling.server.client.dto.ChannelConnection;
import raka.tunneling.server.client.executor.ThreadPreservedExecutor;
import raka.tunneling.server.client.service.TunnelClientService;
import raka.tunneling.server.dto.ReadResponse;

public class ReadThread extends ChannelThread{
	static Logger LOGGER = Logger.getLogger(ReadThread.class.getName());
	public ReadThread(ChannelConnection connection, TunnelClientService service) {
		super(connection, service);
	}
	
	

	public void processChannel() throws Exception {
		while(this.getConnection().isActive()) {
			try {
				ReadResponse response = getService().read(this.getConnection());
				if(response.getLength() < 0)
				{
					this.getConnection().shutdownOutput();
					break;
				}
				else if(response.getLength()>0) {
					LOGGER.info("transfer bytes: " + response.getLength());
					
					ThreadPreservedExecutor.getInstance().Execute(new Runnable() {
						
						@Override
						public void run() {
							try {
								getConnection().getOutputStream().write(response.getData());
								getConnection().getOutputStream().flush();
							}
							catch(Exception ex) {
								throw new RuntimeException(ex);
							}
						}
					});
					
				}
					
			}
			catch(SocketTimeoutException timeout) {
				LOGGER.info("timeout");
			}
		}
	}

}
