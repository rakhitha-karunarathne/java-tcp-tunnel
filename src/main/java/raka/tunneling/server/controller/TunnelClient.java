package raka.tunneling.server.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import raka.tunneling.server.client.dto.TunnelClientConnection;
import raka.tunneling.server.client.service.TunnelClientService;
import raka.tunneling.server.config.TunnelConfiguration;
import raka.tunneling.server.util.TunnelException;

@RestController
@RequestMapping("/client")
public class TunnelClient {
	
	static Logger LOGGER = Logger.getLogger(TunnelClient.class.getName());

	@Value("${tunneling.client.enabled}")
	boolean serverEnabled;
	
	@Autowired
	TunnelClientService service;
	
	private void checkEnabled() {
		if(!serverEnabled)
			throw new TunnelException("Client not enabled");
	}
	
	@GetMapping(path = "/connect", produces = "text/html")	
	public String connect(@RequestParam String publicServer,
			@RequestParam String publicPort,
			@RequestParam String token,
			@RequestParam String tunnelToHost,
			@RequestParam String tunnelToPort) throws URISyntaxException, IOException, InterruptedException {
		checkEnabled();
		LOGGER.info("connect");
		LOGGER.info("publicServer=" + publicServer);
		LOGGER.info("publicPort=" + publicPort);
		LOGGER.info("token=" + token);
		LOGGER.info("tunnelToHost=" + tunnelToHost);
		LOGGER.info("tunnelToPort=" + tunnelToPort);
		
		
		TunnelClientConnection c = new TunnelClientConnection();
		c.setPublicPort(publicPort);
		c.setServerHost(publicServer);
		c.setToken(token);
		c.setTunnelToServer(tunnelToHost);
		c.setTunnelToPort(Integer.parseInt(tunnelToPort));
		service.connect(c);
		return getUi(publicServer, publicPort, token, tunnelToHost, tunnelToPort);
	}
	
	@GetMapping(path = "/delete/{localId}", produces = "text/html")	
	public String connect(@PathVariable String localId) throws URISyntaxException, IOException, InterruptedException {
		checkEnabled();
		service.close(localId);
		return getUi(null, null, null, null, null);
	}
	
	@GetMapping(path = "/", produces = "text/html")	
	public String getUi(@RequestParam(required = false) String publicServer,
			@RequestParam(required = false) String publicPort,
			@RequestParam(required = false) String token,
			@RequestParam(required = false) String tunnelToHost,
			@RequestParam(required = false) String tunnelToPort) {
		checkEnabled();
		LOGGER.info("Show UI");
		StringBuilder sb = new StringBuilder();
		sb.append("<HTML>");
		sb.append("<BODY>");
		sb.append("<FORM action='/client/connect' method='get'>");
		sb.append("<H1>OPEN CONNECTION</H1>");
		sb.append("<P>Public Server: <INPUT TYPE='text' name='publicServer' value='"+nvl(publicServer)+"'></P>");
		sb.append("<P>Public Port: <INPUT TYPE='text' name='publicPort'></P>");
		sb.append("<P>Token: <INPUT TYPE='text' name='token' value='"+nvl(token)+"'></P>");
		sb.append("<br/>");
		sb.append("<P>Tunnel To Host: <INPUT TYPE='text' name='tunnelToHost' value='"+nvl(tunnelToHost)+"'></P>");
		sb.append("<P>Tunnel To Port: <INPUT TYPE='text' name='tunnelToPort' value='"+nvl(tunnelToPort)+"'></P>");
		sb.append("<INPUT TYPE='submit'>");
		sb.append("</FORM>");
		sb.append("<TABLE border='1'>");
		sb.append("<TR>");
		sb.append("<TD>Server Host</TD>");
		sb.append("<TD>Public Port</TD>");
		sb.append("<TD>Tunnel To Host</TD>");
		sb.append("<TD>Tunnel To Port</TD>");
		sb.append("<TD>Connections</TD>");
		sb.append("<TD>Close</TD>");
		sb.append("</TR>");
		for (TunnelClientConnection c : service.getList()) {
			sb.append("<TR>");
			sb.append("<TD>"+c.getServerHost()+"</TD>");
			sb.append("<TD>"+c.getPublicPort()+"</TD>");
			sb.append("<TD>"+c.getTunnelToServer()+"</TD>");
			sb.append("<TD>"+c.getTunnelToPort()+"</TD>");
			sb.append("<TD>"+c.getChannels().size()+"</TD>");
			sb.append("<TD><a href='"+c.getDeleteLink()+"'>Terminate</a></TD>");
			sb.append("</TR>");
		}
		sb.append("</TABLE>");
		sb.append("</BODY>");
		sb.append("</HTML>");
		
		return sb.toString();
	}
	
	public String nvl(String s) {
		if(s == null)
			return "";
		else
			return s;
	}
}
