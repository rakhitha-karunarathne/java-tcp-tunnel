package raka.tunneling.server.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import raka.tunneling.server.util.TunnelException;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {
	@Getter
	@Setter
	boolean error;
	
	@Getter
	@Setter
	String errorMessage; 

	public void checkError() {
		if(error)
			throw new TunnelException(errorMessage);
	}
}
