package raka.tunneling.server.dto;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AcceptResponse extends Response{
	@Getter
	@Setter
	String channelId;
	
	@Getter
	@Setter
	String channelPassword;
	
	@Getter
	@Setter
	String portId;
	
	@Getter
	@Setter
	boolean timeout;
}
