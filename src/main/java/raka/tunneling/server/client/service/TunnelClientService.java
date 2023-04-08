package raka.tunneling.server.client.service;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.ArrayList;


import org.springframework.stereotype.Service;

import lombok.Getter;
import raka.tunneling.server.client.dto.ChannelConnection;
import raka.tunneling.server.client.dto.TunnelClientConnection;
import raka.tunneling.server.client.threads.AcceptThread;
import raka.tunneling.server.client.threads.ReadThread;
import raka.tunneling.server.client.threads.WriteThread;
import raka.tunneling.server.dto.AcceptResponse;
import raka.tunneling.server.dto.OpenRequest;
import raka.tunneling.server.dto.OpenResponse;
import raka.tunneling.server.dto.ReadResponse;
import raka.tunneling.server.dto.Request;
import raka.tunneling.server.dto.Response;
import raka.tunneling.server.dto.ValidResponse;
import raka.tunneling.server.dto.WriteRequest;
import raka.tunneling.server.dto.WriteResponse;
import raka.tunneling.server.util.AbortException;
import raka.tunneling.server.util.HttpUtil;

@Service
public class TunnelClientService {
	@Getter
	ArrayList<TunnelClientConnection> list = new ArrayList<>();
	
	HttpUtil http = new HttpUtil();
	public void connect(TunnelClientConnection c) throws URISyntaxException, IOException, InterruptedException {
		
		OpenRequest request = new OpenRequest();
		request.setPortNo(Integer.parseInt(c.getPublicPort()));
		request.setToken(c.getToken());
		OpenResponse response = http.post(c.getServerUrl() + "server/port/", null, request, OpenResponse.class);		
		c.setPortId(response.getPortId());
		c.setPortPassword(response.getPortPassword());
		c.setActive(true);
		c.setThread(new AcceptThread(c,this).startThread());
		list.add(c);
	}
	
	public void close(String localId) throws URISyntaxException, IOException, InterruptedException {
		for (TunnelClientConnection tunnelClientConnection : list.toArray(new TunnelClientConnection[] {})) {
			if(localId.equals(tunnelClientConnection.getLocalId()))
			{
				close(tunnelClientConnection);
			}
		}
	}

	public void close(TunnelClientConnection tunnelClientConnection)
			throws URISyntaxException, IOException, InterruptedException {
		synchronized(tunnelClientConnection)
		{
			if(!tunnelClientConnection.isClosed())
			{
				tunnelClientConnection.setActive(false);
				Request request = new Request();
				request.setToken(tunnelClientConnection.getToken());
				request.setPortPassword(tunnelClientConnection.getPortPassword());
				
				Response response = http.post(tunnelClientConnection.getServerUrl() + "server/port/" + tunnelClientConnection.getPortId() + "/close", null, request, Response.class);
				
				list.remove(tunnelClientConnection);
				tunnelClientConnection.setClosed(true);
			}
		}
	}
	
	public ChannelConnection accept(TunnelClientConnection tunnelClientConnection) throws URISyntaxException, IOException, InterruptedException {
		Request request = new Request();
		request.setToken(tunnelClientConnection.getToken());
		request.setPortPassword(tunnelClientConnection.getPortPassword());
		
		AcceptResponse response = http.post(tunnelClientConnection.getServerUrl() + "server/port/" + tunnelClientConnection.getPortId() + "/accept", null, request, AcceptResponse.class);
		
		if(response.isTimeout())
			throw new SocketTimeoutException();
		
		ChannelConnection c = new ChannelConnection();
		c.setPort(tunnelClientConnection);
		c.setChannelId(response.getChannelId());
		c.setChannelSecret(response.getChannelPassword());
		c.setActive(true);
		
		startChannelThreads(c);
		tunnelClientConnection.getChannels().add(c);
		return c;
	}

	private void startChannelThreads(ChannelConnection c) {
		try {
			c.getInputStream();
			c.getOutputStream();
			c.getThreads().add(new ReadThread(c, this).startThread());
			c.getThreads().add(new WriteThread(c, this).startThread());
		}
		catch(Exception ex) {
			try {
				close(c);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			throw new AbortException(ex);
		}
	}
	
	public boolean isValid(TunnelClientConnection tunnelClientConnection) throws URISyntaxException, IOException, InterruptedException {
		Request request = new Request();
		request.setToken(tunnelClientConnection.getToken());
		request.setPortPassword(tunnelClientConnection.getPortPassword());
		
		ValidResponse response = http.post(tunnelClientConnection.getServerUrl() + "server/port/" + tunnelClientConnection.getPortId() + "/isvalid", null, request, ValidResponse.class);
		return response.isValid();
	}	
	
	public void write(ChannelConnection channel, byte[] buffer) throws URISyntaxException, IOException, InterruptedException {
		WriteRequest request = new WriteRequest();
		request.setPortPassword(channel.getPort().getPortPassword());
		request.setChannelPassword(channel.getChannelSecret());
		request.setToken(channel.getPort().getToken());
		request.setData(buffer);
		WriteResponse response = http.post(channel.getPort().getServerUrl() + "server/port/" + channel.getPort().getPortId() + "/" + channel.getChannelId() + "/write", 
				null, request, WriteResponse.class);
	}
	public ReadResponse read(ChannelConnection channel) throws URISyntaxException, IOException, InterruptedException {
		Request request = new Request();
		request.setPortPassword(channel.getPort().getPortPassword());
		request.setChannelPassword(channel.getChannelSecret());
		request.setToken(channel.getPort().getToken());		
		ReadResponse response = http.post(channel.getPort().getServerUrl() + "server/port/" + channel.getPort().getPortId() + "/" + channel.getChannelId() + "/read", 
				null, request, ReadResponse.class);
		if(response.isTimeout())
			throw new SocketTimeoutException();
		return response;
	}
	
	public void shutdownOutput(ChannelConnection channel) throws URISyntaxException, IOException, InterruptedException {
		Request request = new Request();
		request.setPortPassword(channel.getPort().getPortPassword());
		request.setChannelPassword(channel.getChannelSecret());
		request.setToken(channel.getPort().getToken());		
		Response response = http.post(channel.getPort().getServerUrl() + "server/port/" + channel.getPort().getPortId() + "/" + channel.getChannelId() + "/shutdown-output", 
				null, request, Response.class);
	}
	
	public void shutdownInput(ChannelConnection channel) throws URISyntaxException, IOException, InterruptedException {
		Request request = new Request();
		request.setPortPassword(channel.getPort().getPortPassword());
		request.setChannelPassword(channel.getChannelSecret());
		request.setToken(channel.getPort().getToken());		
		Response response = http.post(channel.getPort().getServerUrl() + "server/port/" + channel.getPort().getPortId() + "/" + channel.getChannelId() + "/shutdown-input", 
				null, request, Response.class);
	}
	
	public void close(ChannelConnection channel) throws URISyntaxException, IOException, InterruptedException {
		synchronized(channel)
		{
			if(!channel.isClosed())
			{
				channel.setActive(false);
				Request request = new Request();
				request.setPortPassword(channel.getPort().getPortPassword());
				request.setChannelPassword(channel.getChannelSecret());
				request.setToken(channel.getPort().getToken());		
				Response response = http.post(channel.getPort().getServerUrl() + "server/port/" + channel.getPort().getPortId() + "/" + channel.getChannelId() + "/close", 
						null, request, Response.class);
				
				channel.setClosed(true);				
			}
		}
	}	
}
