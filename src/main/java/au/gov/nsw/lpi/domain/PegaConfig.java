package au.gov.nsw.lpi.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
public class PegaConfig {
    protected static final Logger logger = LoggerFactory.getLogger(PegaConfig.class);
    public static enum SyncServiceType {
        DATASYNC_DISTRICT_BASE_DATE,
        DATASYNC_PROPERTY_RELATED,
        DATASYNC_LAND_VALUE,
        DATASYNC_SUPPLEMENTARY_VALUATION,
        ATTACHMENTS_UPLOAD,
        CASES_ATTACHMENTS_LINK;
    }

    public String base_api;
    public String base_un;
    public String base_pw;

    public String datasync_api;
    public String datasync_un;
    public String datasync_pw;

    private Environment env;
    public PegaConfig(Environment env){
        this.env = env;
        init();
    };

    public void init(){
        this.base_api = env.getProperty("pega.api.base");
        this.base_un = env.getProperty("pega.api.un");
        this.base_pw = env.getProperty("pega.api.pw");
        this.datasync_api= env.getProperty("pega.datasync.api");
        this.datasync_un = env.containsProperty("pega.datasync.un")?env.getProperty("pega.datasync.un"):this.base_un;
        this.datasync_pw = env.containsProperty("pega.datasync.pw")?env.getProperty("pega.datasync.pw"):this.base_pw;
    }

    public String getApiEndpoint(SyncServiceType serviceType){
        String api_endpoint = serviceType.toString().startsWith("DATASYNC_")?this.datasync_api:this.base_api;

        switch (serviceType) {
            case ATTACHMENTS_UPLOAD:
                api_endpoint += this.env.getProperty("pega.api.endpoints.attachments_upload");
                break;
            case CASES_ATTACHMENTS_LINK:
                api_endpoint += this.env.getProperty("pega.api.endpoints.cases_attachments_link");
                break;
            case DATASYNC_DISTRICT_BASE_DATE:
                api_endpoint += this.env.getProperty("pega.datasync.endpoints.district_base_date");
                break;
            case DATASYNC_PROPERTY_RELATED:
                api_endpoint += this.env.getProperty("pega.datasync.endpoints.property_related");
                break;
            case DATASYNC_LAND_VALUE:
                api_endpoint += this.env.getProperty("pega.datasync.endpoints.land_value");
                break;
            case DATASYNC_SUPPLEMENTARY_VALUATION:
                api_endpoint += this.env.getProperty("pega.datasync.endpoints.supplementary_valuation");
                break;
            default:
                break;
        }
        return api_endpoint;
    }
}