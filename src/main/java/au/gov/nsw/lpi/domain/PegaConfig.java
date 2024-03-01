package au.gov.nsw.lpi.domain;

import org.springframework.core.env.Environment;
public class PegaConfig {
    public String baseurl;
    public String username;
    public String password;

    public PegaConfig(){};

    public void init(Environment env){
        this.baseurl= env.getProperty("pega.baseurl");
        this.username= env.getProperty("pega.username");
        this.password= env.getProperty("pega.password");
    }
}
