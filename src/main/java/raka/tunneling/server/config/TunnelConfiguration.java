package raka.tunneling.server.config;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import lombok.Getter;
import lombok.Setter;


@Component
public class TunnelConfiguration {
	static Logger LOGGER = Logger.getLogger(TunnelConfiguration.class.getName());
	

	
	@Getter
	@Setter
	@Value("${tunneling.server.listener.begin}")
	int listenerRangeBegin;
	@Getter
	@Setter
	@Value("${tunneling.server.listener.end}")
	int listenerRangeEnd;
	
	@Getter
	@Setter
	@Value("${tunneling.server.timeout}")
	int serverTimeout;
	
	
	@Getter
	@Setter
	@Value("${tunneling.server.token}")
	String token;
	
	@PostConstruct
	public void init() {
		LOGGER.info("Timepout: " + serverTimeout);
		LOGGER.info("Listener Range Begin: " + listenerRangeBegin);
		LOGGER.info("Listener Range End: " + listenerRangeEnd);
	}
}
