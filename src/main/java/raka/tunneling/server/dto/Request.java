package raka.tunneling.server.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Request {
	@Getter
	@Setter
	String token;
	
	@Getter
	@Setter
	String portPassword;
	
	@Getter
	@Setter
	String channelPassword;
}
