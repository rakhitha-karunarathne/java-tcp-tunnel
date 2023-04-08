package raka.tunneling.server.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReadResponse extends Response{
	@Getter
	@Setter
	int length;
	
	@Getter
	@Setter
	byte[] data;
	
	@Getter
	@Setter
	boolean timeout;	
	
	
	public ReadResponse() {
		length = 0;
		data = new byte[]{};
	}
	
	public ReadResponse(int length, byte[] buffer) {
		this.length = length;
		if(this.length == buffer.length) {
			this.data = buffer;
		}
		else if(length == -1)
		{
			this.data = new byte[] {};
		}
		else {
			this.data = new byte[this.length];
			System.arraycopy(buffer, 0, data, 0, length);
		}
	}
}
