package au.gov.nsw.lpi.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.util.Objects;

public class PegaConfig {
    protected static final Logger logger = LoggerFactory.getLogger(PegaConfig.class);
    public static enum SyncServiceType {
        DATASYNC_DISTRICT_BASE_DATE,
        DATASYNC_PROPERTY_RELATED,
        DATASYNC_LAND_VALUE,
        DATASYNC_SUPPLEMENTARY_VALUATION,
        ATTACHMENTS_UPLOAD,
        CASES_ATTACHMENTS_LINK,
        OBJECTIONS_RFIDOCS_PROCESSED;
    }

    public String datasync_api;
    public String datasync_un;
    public String datasync_pw;

    public String attachments_api;
    public String attachments_un;
    public String attachments_pw;

    public String cases_api;
    public String cases_un;
    public String cases_pw;

    public String objections_api;
    public String objections_un;
    public String objections_pw;

    public String documents_upload_dir;
    public boolean documents_upload_cleanup = false;
    private Environment env;
    public PegaConfig(Environment env){
        this.env = env;
        init();
    };

    public void init(){
        this.datasync_api= env.getProperty("pega.datasync.api");
        this.datasync_un = env.getProperty("pega.datasync.un");
        this.datasync_pw = env.getProperty("pega.datasync.pw");

        this.attachments_api = env.getProperty("pega.attachments.api");
        this.attachments_un = env.getProperty("pega.attachments.un");
        this.attachments_pw = env.getProperty("pega.attachments.pw");

        this.cases_api = env.getProperty("pega.cases.api");
        this.cases_un = env.getProperty("pega.cases.un");
        this.cases_pw = env.getProperty("pega.cases.pw");

        this.objections_api = env.getProperty("pega.objections.api");
        this.objections_un = env.getProperty("pega.objections.un");
        this.objections_pw = env.getProperty("pega.objections.pw");

        this.documents_upload_dir = env.getProperty("documents.upload.dir");
        this.documents_upload_cleanup = env.containsProperty("documents.upload.cleanup")&& Objects.equals(env.getProperty("documents.upload.cleanup"), "true");
    }

    public String getApiEndpoint(SyncServiceType serviceType){
        String api_endpoint = this.datasync_api;
        switch (serviceType.toString().split("_")[0]){
            case "ATTACHMENTS":
                api_endpoint = this.attachments_api;
                break;
            case "CASES":
                api_endpoint = this.cases_api;
                break;
            case "OBJECTIONS":
                api_endpoint = this.objections_api;
                break;
            default:
                break;
        }

        switch (serviceType) {
            case ATTACHMENTS_UPLOAD:
                api_endpoint += this.env.getProperty("pega.attachments.endpoints.upload");
                break;
            case CASES_ATTACHMENTS_LINK:
                api_endpoint += this.env.getProperty("pega.cases.endpoints.attachments_link");
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
            case OBJECTIONS_RFIDOCS_PROCESSED:
                api_endpoint += this.env.getProperty("pega.objections.endpoints.rfi_docs_processed");
                break;
            default:
                break;
        }
        return api_endpoint;
    }
}