package raka.tunneling.server.client.threads;

import java.net.SocketTimeoutException;
import java.util.logging.Logger;

import raka.tunneling.server.client.dto.ChannelConnection;
import raka.tunneling.server.client.executor.ThreadPreservedExecutor;
import raka.tunneling.server.client.service.TunnelClientService;

public class WriteThread extends ChannelThread{
	static Logger LOGGER = Logger.getLogger(WriteThread.class.getName());
	
	
	
	public WriteThread(ChannelConnection connection, TunnelClientService service) {
		super(connection, service);	
	}

	@Override
	public void processChannel() throws Exception {
		while(this.getConnection().isActive()) {
			try {
				byte[] b = new byte[1024*128];
				int i = this.getConnection().getInputStream().read(b);
				if(i<0) {
					this.getService().shutdownOutput(this.getConnection());
					break;
				}
				else if(i == b.length) {
					write(b);
					
				}
				else if(i>0) {
					byte[] buffer = new byte[i];
					System.arraycopy(b, 0, buffer, 0, i);
					write(buffer);					
				}
				LOGGER.info("transfer bytes: " + i);
			}
			catch(SocketTimeoutException ex) {
				LOGGER.info("timeout");
			}
		}
	}
	
	private void write(byte[] b) throws Exception{
		this.getService().write(this.getConnection(), b);
	}

}
