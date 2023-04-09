package raka.tunneling.server.config;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties("tunneling.client")
@Component
public class ClientConfig {
	@Getter
	@Setter
	boolean enabled;
	
	@Getter
	List<ClientConnectionConfig> connections = new ArrayList<>();
	
	

	
	@PostConstruct
	public void init() {
		for (ClientConnectionConfig clientConnectionConfig : connections) {
			if (clientConnectionConfig.getId() == null)
				clientConnectionConfig.setId(UUID.randomUUID().toString());
		}
	}
	
	
}
