package raka.tunneling.server.listener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.UUID;
import java.util.logging.Logger;

import lombok.Getter;
import lombok.Setter;
import raka.tunneling.server.dto.ReadResponse;
import raka.tunneling.server.dto.WriteRequest;
import raka.tunneling.server.dto.WriteResponse;
import raka.tunneling.server.util.PasswordUtil;
import raka.tunneling.server.util.TunnelException;

public class SocketHandler {
	static Logger LOGGER = Logger.getLogger(SocketHandler.class.getName());
	
	Socket socket;
	InputStream in;
	OutputStream out;
	
	@Getter
	@Setter
	String chanelId;
	
	@Getter
	@Setter
	String channelPassword;	
	
	@Getter
	@Setter
	long timestamp;
	
	public SocketHandler(Socket s) throws IOException {
		this.socket = s;
		this.socket.setSoTimeout(20000);
		this.socket.setReceiveBufferSize(1024*128);
		this.in = new BufferedInputStream(this.socket.getInputStream(),1024*128);
		this.out = new BufferedOutputStream(this.socket.getOutputStream(),1024*128);
		
		this.chanelId = UUID.randomUUID().toString();
		this.channelPassword = PasswordUtil.generatePassayPassword();
	}
	
	public void refresh() {
		timestamp = System.currentTimeMillis();
	}
	
	public ReadResponse read() throws IOException {
		try {
			refresh();
			byte[] buffer = new byte[1024*128];
			int i = in.read(buffer);
			return new ReadResponse(i,buffer);
		}
		catch(SocketTimeoutException timeout) {
			ReadResponse r= new ReadResponse();
			r.setTimeout(true);
			return r;
		}
	}	
	
	
	public WriteResponse write(WriteRequest data) throws IOException {
		refresh();
		out.write(data.getData());
		out.flush();
		return new WriteResponse();
	}
	
	public void shutdownInput() throws IOException
	{
		if(socket == null)
			return;
		
		if(!socket.isInputShutdown())
		{
			closeIn();
			socket.shutdownInput();
		}
	}
	public void shutdownOutput() throws IOException
	{
		if(socket == null)
			return;
		
		if(!socket.isOutputShutdown())
		{
			closeOut();
			socket.shutdownOutput();
		}
	}
	
	public void close() {
		LOGGER.info("close: " + this.chanelId);
		refresh();
		closeOut();
		closeIn();
		closeSocket();
	}

	private void closeSocket() {
		if(socket != null)
		{
			try {
				socket.close();
				socket = null;
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void closeIn() {
		try {
			if(in != null)
			{
				in.close();
				in = null;
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private void closeOut() {
		try {
			if(out != null)
			{
				out.flush();
				out.close();
				out = null;
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void checkPassword(String password) {
		refresh();
		if(!this.channelPassword.equals(password))
			throw new TunnelException("Invalid channel password");
	}
	

}
