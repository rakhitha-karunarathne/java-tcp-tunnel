package raka.tunneling.server.client.executor;

import lombok.Getter;
import lombok.Setter;

public class ChainableTask{
	@Getter
	@Setter
	Runnable task;
	
	@Getter
	@Setter
	ChainableTask next;
	
	@Getter
	@Setter	
	boolean executed;
	
	@Getter
	@Setter	
	boolean nextQueued;
	
	@Getter
	@Setter
	Throwable throwable;
	
	public ChainableTask(Runnable r) {
		task = r;
	}
	


}
