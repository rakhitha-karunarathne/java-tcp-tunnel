package raka.tunneling.server.listener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import lombok.Getter;
import lombok.Setter;
import raka.tunneling.server.dto.AcceptResponse;
import raka.tunneling.server.dto.ReadResponse;
import raka.tunneling.server.dto.WriteRequest;
import raka.tunneling.server.dto.WriteResponse;
import raka.tunneling.server.util.PasswordUtil;
import raka.tunneling.server.util.TunnelException;

public class PortListener {
	static Logger LOGGER = Logger.getLogger(PortListener.class.getName());
	@Getter
	@Setter
	String portId;
	
	@Getter
	@Setter
	String portPassword;
	
	
	ServerSocket serverSocket;
	Map<String,SocketHandler> sockets = Collections.synchronizedMap(new HashMap<>());
	
	
	@Getter
	@Setter
	long timestamp;
	
	public PortListener() {
		this.portId = UUID.randomUUID().toString();		 
		this.portPassword = PasswordUtil.generatePassayPassword();
		refresh();
	}
	
	public void refresh() {
		timestamp = System.currentTimeMillis();
	}
	
	public void open(int port) throws IOException {		
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(20000);
		LOGGER.info("Port Open: " + port);
		refresh();
	}
	
	public AcceptResponse accept() throws IOException {
		try {			
			AcceptResponse r = new AcceptResponse();
			Socket s = serverSocket.accept();			
			SocketHandler h = new SocketHandler(s);
			sockets.put(h.getChanelId(), h);
			r.setChannelId(h.getChanelId());
			r.setPortId(portId);
			r.setChannelPassword(h.getChannelPassword());
			refresh();
			LOGGER.info("New Connection: " + serverSocket.getLocalPort());
			return r;
		}
		catch(SocketTimeoutException ex) {
			AcceptResponse a = new AcceptResponse();
			a.setTimeout(true);
			return a;
		}
	}
	
	public ReadResponse read(String channelId) {
		try {
			refresh();
			return sockets.get(channelId).read();			
		}
		catch(Exception ex) {
			handleException(channelId, ex);
			return null;
		}
	}

	
	
	
	public WriteResponse write(String channelId, WriteRequest data){
		try {
			refresh();
			return sockets.get(channelId).write(data);
		}
		catch(Exception ex) {
			handleException(channelId, ex);
			return null;
		}
	}	
	
	private void handleException(String channelId, Exception ex) {
		LOGGER.info("handleException: " + ex.toString());
		if(sockets.containsKey(channelId)) {
			sockets.get(channelId).close();
			sockets.remove(channelId);
		}
		throw new TunnelException(ex);
	}
	
	public void close() {
		LOGGER.info("close");
		refresh();
		for (String k : this.sockets.keySet().toArray(new String[] {})) {
			if(sockets.containsKey(k))
			{
				closeChannel(k,sockets.get(k).getChannelPassword());
			}
		}
		try {
			serverSocket.close();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void closeChannel(String channelId, String channelPassword) {		
		refresh();
		sockets.get(channelId).checkPassword(channelPassword);
		sockets.get(channelId).close();
		sockets.remove(channelId);
	}
	
	public void shutdownInput(String channelId, String channelPassword) throws IOException {		
		refresh();
		sockets.get(channelId).checkPassword(channelPassword);
		sockets.get(channelId).shutdownInput();
	}
	
	public void shutdownOutput(String channelId, String channelPassword) throws IOException {		
		refresh();
		sockets.get(channelId).checkPassword(channelPassword);
		sockets.get(channelId).shutdownOutput();
	}
	
	public void checkPassword(String password) {
		refresh();
		if(!this.portPassword.equals(password))
			throw new TunnelException("Invalid port password");
	}
	
	public void checkChannelPassword(String channelId, String password) {
		refresh();
		this.sockets.get(channelId).checkPassword(password);
	}
	
	public void checkTimeout(long lastActiveTime) {		
		for (String channelId : this.sockets.keySet().toArray(new String[] {})) {
			try {
				if(this.sockets.containsKey(channelId)) {
					if(this.sockets.get(channelId).getTimestamp()<lastActiveTime) {
						LOGGER.info("Removing inactive channel: " + channelId);
						sockets.get(channelId).close();
						sockets.remove(channelId);
					}
				}
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
