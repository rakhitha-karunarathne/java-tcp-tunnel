package raka.tunneling.server.client.dto;

import lombok.Getter;
import lombok.Setter;

public class BufferData {
	@Getter
	@Setter
	byte[] buffer;
	
	@Getter
	@Setter
	long time;
	public BufferData(byte[] b) {
		buffer = b;
		time = System.currentTimeMillis();
	}

}
