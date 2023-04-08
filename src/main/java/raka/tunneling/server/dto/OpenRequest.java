package raka.tunneling.server.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenRequest extends Request{
	@Getter
	@Setter
	int portNo;
}
