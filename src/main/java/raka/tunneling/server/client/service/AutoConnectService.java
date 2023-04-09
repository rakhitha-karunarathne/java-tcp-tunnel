package raka.tunneling.server.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import raka.tunneling.server.client.dto.TunnelClientConnection;
import raka.tunneling.server.config.ClientConfig;
import raka.tunneling.server.config.ClientConnectionConfig;

@Component
public class AutoConnectService {
	
	@Autowired
	TunnelClientService service;
	
	@Autowired
	ClientConfig clientConfig;
	
	@Scheduled(fixedDelay = 20000, initialDelay = 10000)
	public void createConnections() {		
		if(!clientConfig.isEnabled())
			return;		
		
		for (ClientConnectionConfig clientConnectionConfig : clientConfig.getConnections()) {
			try {
				if(!service.connectionExist(clientConnectionConfig.getId()))
				{
					TunnelClientConnection c = new TunnelClientConnection();
					c.setName(clientConnectionConfig.getName());
					c.setId(clientConnectionConfig.getId());
					c.setPublicPort(clientConnectionConfig.getPublicPort());
					c.setServerHost(clientConnectionConfig.getServerHost());
					c.setToken(clientConnectionConfig.getToken());
					c.setTunnelToServer(clientConnectionConfig.getTunnelToServer());
					c.setTunnelToPort(clientConnectionConfig.getTunnelToPort());
					service.connect(c);
				}
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
