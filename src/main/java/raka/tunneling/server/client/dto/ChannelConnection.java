package raka.tunneling.server.client.dto;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import raka.tunneling.server.client.threads.ChannelThread;

public class ChannelConnection {
	@Getter
	@Setter
	String channelId;
	
	@Getter
	@Setter
	String channelSecret;
	
	
	Socket socket;
	InputStream in;
	OutputStream out;
	
	@Getter
	@Setter
	TunnelClientConnection port;
	
	@Getter
	@Setter
	boolean active = true;
	
	@Getter
	List<ChannelThread> threads = new ArrayList<>();
	
	@Getter	
	boolean closed = false;
	
	@Getter
	List<byte[]> writeToClientBuffer = new ArrayList<>();
	
	private synchronized void createSocket() throws UnknownHostException, IOException {
		if(socket != null)
			return;
		socket = new Socket(this.getPort().getTunnelToServer(), this.getPort().getTunnelToPort());
		socket.setSoTimeout(20000);
		socket.setReceiveBufferSize(1024*128);
	}
	public Socket getSocket() throws UnknownHostException, IOException {
		if(socket == null) {
			createSocket();
		}
		return socket;
	}
	
	private synchronized void createInputStream() throws UnknownHostException, IOException {
		if(in != null)
			return;
		in = new BufferedInputStream(this.getSocket().getInputStream(),1024*128);
	}
	public InputStream getInputStream() throws UnknownHostException, IOException {
		if(in == null) {
			createInputStream();
		}
		return in;
	}
	
	private synchronized void createOutputStream() throws UnknownHostException, IOException {
		if(out != null)
			return;
		out = new BufferedOutputStream(this.getSocket().getOutputStream(),1024*128);
	}
	
	public OutputStream getOutputStream() throws UnknownHostException, IOException {
		if(out == null) {
			createOutputStream();
		}
		return out;
	}
	
	public void shutdownOutput() {
		closeOut();
		if(socket != null && !socket.isOutputShutdown()) {
			try {
				socket.shutdownOutput();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}			
		}
	}
	private void closeOut() {
		if(out != null)
		{
			try {
				out.flush();
				out.close();
				out = null;
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	public void shutdownInout() {
		closeIn();
		if(socket != null && !socket.isInputShutdown()) {
			try {
				socket.shutdownInput();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}			
		}
	}
	private void closeIn() {
		if(in != null)
		{
			try {
				in.close();
				in = null;
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	public void closeSocket() {
		this.closeOut();
		this.closeIn();
		if(socket != null) {
			try {
				socket.close();
				socket = null;
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}			
		}
	}
	
	public synchronized int threadStopped(ChannelThread t) {
		this.getThreads().remove(t);
		return this.getThreads().size();
	}
	
	public void setClosed(boolean b) {
		this.closed = b;
		if(this.closed)
		{
			this.getPort().getChannels().remove(this);			
		}
		
	}
}
