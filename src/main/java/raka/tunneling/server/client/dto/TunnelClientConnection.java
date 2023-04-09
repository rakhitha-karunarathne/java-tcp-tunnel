package raka.tunneling.server.client.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import raka.tunneling.server.client.threads.AcceptThread;

public class TunnelClientConnection {
	@Getter
	@Setter
	String name = "";
	
	@Getter
	@Setter
	String id;
	
	@Getter
	@Setter
	String serverHost;
	
	@Getter
	@Setter
	int publicPort;
	
	@Getter
	@Setter
	String tunnelToServer;
	
	@Getter
	@Setter
	int tunnelToPort;
	
	
	@Getter
	@Setter
	String portId;
	
	@Getter
	@Setter
	String portPassword;
	
	@Getter
	@Setter
	String token;
	
	@Getter
	@Setter
	List<ChannelConnection> channels = new ArrayList<>(); 
	
	@Getter
	@Setter
	String localId = UUID.randomUUID().toString();
	
	@Getter
	@Setter
	boolean active = true;
	
	@Getter
	@Setter
	AcceptThread thread;
	
	@Getter
	@Setter
	boolean closed = false;
	
	public String getDeleteLink() {
		return "/client/delete/" + localId;
	}
	
	public String getServerUrl() {
		String s= "http://" + serverHost;
		if(s.endsWith("/"))
			return s;
		else
			return s + "/";
	}
}
