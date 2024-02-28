package au.gov.nsw.lpi.domain;

import org.springframework.stereotype.Component;
import org.springframework.core.env.Environment;
@Component
public class ServerConfig {
    public final String serverEnvironment;
    public final String allowedIps;

    //public final String apiKey;
    public ServerConfig(Environment env) {
        this.serverEnvironment = env.getProperty("server.env");
        this.allowedIps = env.getProperty("allowed.ips");
        //this.apiKey = env.getProperty("api.key");
    }
}
