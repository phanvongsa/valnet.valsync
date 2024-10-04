package au.gov.nsw.lpi.domain;

import org.springframework.core.env.Environment;
public class PegaConfig {

    public static enum SyncServiceType {
        DATASYNC, ATTACHMENTS_UPLOAD, CASES_ATTACHMENTS_LINK;
    }
    public String base_api;
    public String base_un;
    public String base_pw;

    public String datasync_api;
    public String datasync_un;
    public String datasync_pw;

    public PegaConfig(){};

    public void init(Environment env){        
        this.base_api = env.getProperty("pega.api.base");
        this.base_un = env.getProperty("pega.api.un");
        this.base_pw = env.getProperty("pega.api.pw");
        
        this.datasync_api= String.format("%s%s",this.base_api, env.getProperty("pega.datasync.api"));
        this.datasync_un = env.containsProperty("pega.datasync.un")?env.getProperty("pega.datasync.un"):this.base_un;
        this.datasync_pw = env.containsProperty("pega.datasync.pw")?env.getProperty("pega.datasync.pw"):this.base_pw;
    }

    public String getApiEndpoint(SyncServiceType serviceType){
        switch (serviceType) {
            case DATASYNC:
                return this.datasync_api;
            case ATTACHMENTS_UPLOAD:
                return this.base_api+"/attachments/upload";
            case CASES_ATTACHMENTS_LINK:
                return this.base_api+"/cases/{ENTITY_ID}/attachments/";
        }
        return null;
    }
}