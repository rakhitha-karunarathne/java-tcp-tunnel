package raka.tunneling.server.config;

import lombok.Getter;
import lombok.Setter;

public class ClientConnectionConfig {
	@Getter
	@Setter
	String id;
	@Getter
	@Setter
	String name = "";
	@Getter
	@Setter
	String serverHost;
	@Getter
	@Setter
    int publicPort;
	@Getter
	@Setter
    String token;
	@Getter
	@Setter
    String tunnelToServer;
	@Getter
	@Setter
    int tunnelToPort;
}
