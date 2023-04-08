package raka.tunneling.server.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import raka.tunneling.server.dto.AcceptResponse;

import raka.tunneling.server.dto.OpenRequest;
import raka.tunneling.server.dto.OpenResponse;
import raka.tunneling.server.dto.ReadResponse;
import raka.tunneling.server.dto.Request;
import raka.tunneling.server.dto.Response;
import raka.tunneling.server.dto.ValidResponse;
import raka.tunneling.server.dto.WriteRequest;
import raka.tunneling.server.dto.WriteResponse;
import raka.tunneling.server.service.TunnelService;
import raka.tunneling.server.util.TunnelException;

@RestController
@RequestMapping("/server")
public class TunnelServer {
	@Autowired 
	TunnelService service;
	
	@Value("${tunneling.server.enabled}")
	boolean serverEnabled;
	
	private void checkEnabled() {
		if(!serverEnabled)
			throw new TunnelException("Server not enabled");
	}

	@PostMapping("/port/")
	public OpenResponse openPort(@RequestBody OpenRequest command) throws IOException {
		validateRequest(command);
		return service.openPort(command);
	}
	
	@PostMapping("/port/{portId}/accept")
	public AcceptResponse accept(@PathVariable String portId, @RequestBody Request r) throws IOException {
		validateRequest(r);
		return service.accept(portId,r);
	}
	
	@PostMapping("/port/{portId}/isvalid")
	public ValidResponse isValid(@PathVariable String portId, @RequestBody Request r) throws IOException {
		try {
			validateRequest(r);
			return service.isValid(portId,r);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			ValidResponse v = new ValidResponse();
			v.setValid(false);
			return v;
		}
		
	}
	
	@PostMapping("/port/{portId}/{channelId}/read")
	public ReadResponse read(@PathVariable String portId,@PathVariable String channelId, @RequestBody Request r) throws IOException {
		validateRequest(r);
		return service.read(portId, channelId,r);
	}	
	
	@PostMapping("/port/{portId}/{channelId}/write")
	public WriteResponse write(@PathVariable String portId, @PathVariable String channelId, @RequestBody WriteRequest data) throws IOException {
		validateRequest(data);
		return service.write(portId, channelId, data);
	}
	
	@ExceptionHandler(Exception.class)
    public Response handleException(Exception ex) {
		Response r = new Response();
		r.setError(true);
		r.setErrorMessage(ex.toString());
		return r;
    }
	
	
	@PostMapping("/port/{portId}/close")
	public Response closePort(@PathVariable String portId, @RequestBody Request r) throws IOException {
		validateRequest(r);
		service.closePort(portId, r);
		return new Response();
	}
	
	@PostMapping("/port/{portId}/{channelId}/close")
	public Response closeChannel(@PathVariable String portId,@PathVariable String channelId, @RequestBody Request r) throws IOException {
		validateRequest(r);
		service.closeChannel(portId,channelId, r);
		return new Response();
	}
	
	
	@PostMapping("/port/{portId}/{channelId}/shutdown-output")
	public Response shutdownOutput(@PathVariable String portId,@PathVariable String channelId, @RequestBody Request r) throws IOException {
		validateRequest(r);
		service.shutdownOutput(portId,channelId, r);
		return new Response();
	}
	
	@PostMapping("/port/{portId}/{channelId}/shutdown-input")
	public Response shutdownInput(@PathVariable String portId,@PathVariable String channelId, @RequestBody Request r) throws IOException {
		validateRequest(r);
		service.shutdownInput(portId,channelId, r);
		return new Response();
	}
	
	
	
	
	private void validateRequest(Request r) {
		checkEnabled();
		service.validateRequest(r);
	}

}
