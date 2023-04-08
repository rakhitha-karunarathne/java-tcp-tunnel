package raka.tunneling.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TunnelingServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TunnelingServerApplication.class, args);
	}

}
