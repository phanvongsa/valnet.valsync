package au.gov.nsw.lpi.domain;

import org.springframework.core.env.Environment;
public class PegaConfig {
    // public String baseurl;
    // public String username;
    // public String password;

    public String api_base;
    public String api_un;
    public String api_pw;

    public String datasync_api;
    public String datasync_un;
    public String datasync_pw;

    public String attachments_api;
    public String attachments_un;
    public String attachments_pw;

    public String cases_api;
    public String cases_un;
    public String cases_pw;

    public PegaConfig(){};

    public void init(Environment env){        
        this.api_base = env.getProperty("pega.api.base");        
        this.api_un = env.getProperty("pega.api.un");        
        this.api_pw = env.getProperty("pega.api.pw");        
        
        this.datasync_api= String.format("%s%s",api_base, env.getProperty("pega.datasync.api"));
        this.datasync_un = env.containsProperty("pega.datasync.un")?env.getProperty("pega.datasync.un"):this.api_un;
        this.datasync_pw = env.containsProperty("pega.datasync.pw")?env.getProperty("pega.datasync.pw"):this.api_pw;

        this.cases_api= String.format("%s%s",api_base, env.getProperty("pega.cases.api"));
        this.cases_un = env.containsProperty("pega.cases.un")?env.getProperty("pega.cases.un"):this.api_un;
        this.cases_pw = env.containsProperty("pega.cases.pw")?env.getProperty("pega.cases.pw"):this.api_pw;

        this.attachments_api= String.format("%s%s",api_base, env.getProperty("pega.attachments.api"));
        this.attachments_un = env.containsProperty("pega.attachments.un")?env.getProperty("pega.attachments.un"):this.api_un;
        this.attachments_pw = env.containsProperty("pega.attachments.pw")?env.getProperty("pega.attachments.pw"):this.api_pw;
        
    }
}
