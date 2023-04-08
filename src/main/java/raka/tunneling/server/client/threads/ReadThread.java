package raka.tunneling.server.client.threads;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import raka.tunneling.server.client.dto.ChannelConnection;
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
					this.getConnection().getOutputStream().write(response.getData());
					this.getConnection().getOutputStream().flush();					
				}
					
			}
			catch(SocketTimeoutException timeout) {
				LOGGER.info("timeout");
			}
		}
	}

}
