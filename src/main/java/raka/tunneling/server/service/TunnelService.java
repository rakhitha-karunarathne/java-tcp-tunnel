package raka.tunneling.server.service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import raka.tunneling.server.config.TunnelConfiguration;
import raka.tunneling.server.dto.AcceptResponse;
import raka.tunneling.server.dto.OpenRequest;
import raka.tunneling.server.dto.OpenResponse;
import raka.tunneling.server.dto.ReadResponse;
import raka.tunneling.server.dto.Request;
import raka.tunneling.server.dto.ValidResponse;
import raka.tunneling.server.dto.WriteRequest;
import raka.tunneling.server.dto.WriteResponse;
import raka.tunneling.server.listener.PortListener;
import raka.tunneling.server.util.TunnelException;

@Service
public class TunnelService {
	static Logger LOGGER = Logger.getLogger(TunnelService.class.getName());
	Map<String, PortListener> ports = Collections.synchronizedMap(new HashMap<>());
	
	@Autowired
	TunnelConfiguration config;
	
	public void validateRequest(Request r) {
		if(!config.getToken().equals(r.getToken()))
			throw new TunnelException("Invalid token");
	}
	
	public OpenResponse openPort(OpenRequest command) throws IOException{
		
		if(command.getPortNo() < this.config.getListenerRangeBegin() ||
		   command.getPortNo() > this.config.getListenerRangeEnd())
			throw new TunnelException("Port must be between: " + this.config.getListenerRangeBegin() + " and " + this.config.getListenerRangeEnd());
		
		PortListener l = new PortListener();
		l.open(command.getPortNo());
		ports.put(l.getPortId(), l);
		
		OpenResponse r = new OpenResponse();
		r.setPortId(l.getPortId());
		r.setPublicPort(command.getPortNo());
		r.setPortPassword(l.getPortPassword());
		return r;
	}	
	
	public AcceptResponse accept(String portId, Request r) throws IOException {
		ports.get(portId).checkPassword(r.getPortPassword());
		try {
			return ports.get(portId).accept();
		}
		catch(Exception ex) {
			if(ports.containsKey(portId)) {
				ports.get(portId).close();
				ports.remove(portId);
			}
			throw new TunnelException(ex);
		}
		
	}
	
	public ValidResponse isValid(String portId, Request r) throws IOException {
		ports.get(portId).checkPassword(r.getPortPassword());
		ValidResponse v = new ValidResponse();
		v.setValid(true);
		return v;
	}
	
	
	public ReadResponse read(String portId,String channelId, Request r) throws IOException {
		ports.get(portId).checkPassword(r.getPortPassword());
		ports.get(portId).checkChannelPassword(channelId, r.getChannelPassword());
		
		return ports.get(portId).read(channelId);
	}	
	
	
	public WriteResponse write(String portId, String channelId, WriteRequest data) throws IOException {
		ports.get(portId).checkPassword(data.getPortPassword());
		ports.get(portId).checkChannelPassword(channelId, data.getChannelPassword());
		
		return ports.get(portId).write(channelId,data);
	}	
	
	public void closePort(String portId, Request r) {
		ports.get(portId).checkPassword(r.getPortPassword());
		ports.get(portId).close();
		ports.remove(portId);
	}
	public void closeChannel(String portId, String channelId, Request r) {
		ports.get(portId).checkPassword(r.getPortPassword());
		ports.get(portId).closeChannel(channelId, r.getChannelPassword());
	}
	
	public void shutdownOutput(String portId, String channelId, Request r) throws IOException {
		ports.get(portId).checkPassword(r.getPortPassword());
		ports.get(portId).shutdownOutput(channelId, r.getChannelPassword());
	}
	
	public void shutdownInput(String portId, String channelId, Request r) throws IOException {
		ports.get(portId).checkPassword(r.getPortPassword());
		ports.get(portId).shutdownInput(channelId, r.getChannelPassword());
	}
	
	@Value("${tunneling.server.enabled}")
	boolean serverEnabled;
	
	@Scheduled(fixedDelayString = "${tunneling.server.timeout}")
	public void checkTimeout() {
		if(!serverEnabled)
			return;
		
		long lastActiveTime = System.currentTimeMillis() - this.config.getServerTimeout();
		for (String p : this.ports.keySet().toArray(new String[] {})) {
			try {
				if(this.ports.containsKey(p)) {
					if(this.ports.get(p).getTimestamp()<lastActiveTime) {
						LOGGER.info("Removing inactive port: " + p);
						this.ports.get(p).close();
						this.ports.remove(p);
					}
					else {
						this.ports.get(p).checkTimeout(lastActiveTime);
					}
				}
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
