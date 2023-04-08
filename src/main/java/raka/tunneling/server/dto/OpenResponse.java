package raka.tunneling.server.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenResponse extends Response{
	@Getter
	@Setter
	int publicPort;
	
	@Getter
	@Setter
	String portId;
	
	@Getter
	@Setter
	String portPassword;
}
