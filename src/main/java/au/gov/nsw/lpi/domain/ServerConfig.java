package au.gov.nsw.lpi.domain;

//import org.hibernate.cfg.Environment;
//import org.springframework.stereotype.Component;
//import org.springframework.core.env.Environment;
import org.springframework.core.env.Environment;
public class ServerConfig {
    public String serverEnvironment;
    public String allowedIps;

    public String apiKey;
    public String apiKeyName;

    public ServerConfig(){
    }

    public void init(Environment env){
        this.serverEnvironment = env.getProperty("server.env");
        this.allowedIps = env.getProperty("allowed.ips");
        this.apiKey = env.getProperty("api.key");
        this.apiKeyName = env.getProperty("api.keyName");
    }
}
