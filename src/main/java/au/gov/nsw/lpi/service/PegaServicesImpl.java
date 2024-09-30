package au.gov.nsw.lpi.service;

import au.gov.nsw.lpi.common.Utils;
import au.gov.nsw.lpi.dao.BaseDaoImp;
import au.gov.nsw.lpi.domain.PegaConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


@Service
public class PegaServicesImpl implements PegaServices {
    protected static final Logger logger = LoggerFactory.getLogger(PegaServices.class);

    private final String dataSync_api;
    private final String attachments_api;
    private final HttpClient dataSyncHttpClient;
    private final HttpClient attachmentsHttpClient;

    private final String endpoint_property_related = "/valsync/property/related";
    private final String endpoint_district_basedate = "/valsync/districtbasedate";
    private final String endpoint_supplementary_valuation = "/valsync/suppval";
    private final String endpoint_land_value = "/valsync/landvalue";

    private final String endpoint_attachments_upload = "/attachments/upload";

    public PegaServicesImpl(PegaConfig cfg){
        this.dataSync_api = cfg.datasync_api;
        CredentialsProvider dataSyncCredentialsProvider = new BasicCredentialsProvider();
        dataSyncCredentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(cfg.datasync_un, cfg.datasync_pw));
        this.dataSyncHttpClient = HttpClients.custom().setDefaultCredentialsProvider(dataSyncCredentialsProvider).build();

        this.attachments_api = cfg.attachments_api;
        CredentialsProvider attachmentsCredentialsProvider = new BasicCredentialsProvider();
        attachmentsCredentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(cfg.datasync_un, cfg.datasync_pw));
        this.attachmentsHttpClient = HttpClients.custom().setDefaultCredentialsProvider(attachmentsCredentialsProvider).build();
    }

    @Override
    public Map<String, Object> property_related(String payload) {
        return sendRequests(this.endpoint_property_related, payload);
    }

    @Override
    public Map<String, Object> district_basedate(String payload) {
        return sendRequests(this.endpoint_district_basedate, payload);
    }

    @Override
    public Map<String, Object> supplementary_valuation(String payload) {
        return sendRequests(this.endpoint_supplementary_valuation, payload);
    }

    @Override
    public Map<String, Object> land_value(String payload) {
        return sendRequests(this.endpoint_land_value, payload);
    }

    @Override
    public Map<String, Object> attachments_associate(String payload) {
        String api_url = this.attachments_api+this.endpoint_attachments_upload;
        Object ejo = Utils.json2JsonObject(payload).getAsJsonObject("entity");
        Utils.json2JsonObject(payload).getAsJsonArray("attachments").forEach(attachment -> {
            String file_path = attachment.getAsJsonObject().get("file").getAsString();
//            String file_title = attachment.getAsJsonObject().get("title").getAsString();
            File file = Utils.getFileIfExists(file_path);
            if(file!=null){
                HttpPost req = new HttpPost(api_url);
            }else{
                logger.error(file_path+" file does not exist");
            }
        });

//        Utils.json2JsonObject(payload).getAsJsonArray("attachments").forEach(attachment -> {
//            JsonObject ajo = attachment.getAsJsonObject();
//            logger.debug(attachment.getAsString());
//        });
            //ajo.addProperty("entityId", ejo.get("entityId").getAsString());
            //ajo.addProperty("entityType", ejo.get("entityType").getAsString());

//        Utils.json2JsonObject(payload).getAsJsonObject("attachments").getAsJsonArray("attachment").forEach(attachment -> {
//            //JsonObject ajo = attachment.getAsJsonObject();
//            logger.debug(attachment.getAsString());
////            ajo.addProperty("entityId", ejo.get("entityId").getAsString());
////            ajo.addProperty("entityType", ejo.get("entityType").getAsString());
//        });
//        logger.debug("attachments_associate");
//        logger.debug(api_url);
//        logger.debug(ejo.toString());

        Map<String, Object> nfo = new HashMap<>();
        try {
            //HttpPost req = new HttpPost(api_url);
            nfo.put("responseStatusCode",200);
            nfo.put("responseBody",payload);
            //System.out.println("attachments_associate");
        }catch (Exception ex){
            setResponseMap(nfo, ex);
        }
        return nfo;
    }

    private Map<String, Object> sendRequests(String api_end_point, String payload){
        return sendRequest(dataSyncHttpClient, this.dataSync_api+api_end_point, payload);
//        switch (api_end_point){
//            case endpoint_property_related:
//            case endpoint_district_basedate:
//            case endpoint_supplementary_valuation:
//            case endpoint_land_value:
//                return sendRequest(dataSyncHttpClient, this.dataSync_api+api_end_point, payload);
//            default:
//                return sendRequest(attachmentsHttpClient, this.attachments_api+api_end_point, payload);
//        }
    }

    private Map<String, Object> sendRequest(HttpClient client, String api_url, String payload){
        Map<String, Object> nfo = new HashMap<>();
        try {
            HttpPost req = new HttpPost(api_url);
            req.setEntity(new StringEntity(payload));
            setResponseMap(nfo, client.execute(req));
        }catch (Exception ex){
            setResponseMap(nfo, ex);
        }
        return nfo;
    }

    private void setResponseMap(Map<String, Object> nfo, HttpResponse response){
        nfo.put("responseStatusCode",response.getStatusLine().getStatusCode());
        nfo.put("responseBody",getResponseBody(response));
    }

    private void setResponseMap(Map<String, Object> nfo, Exception ex){
        nfo.put("responseStatusCode",500);
        nfo.put("responseBody", ex.getMessage());
    }

    private String getResponseBody(HttpResponse response){
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder responseBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBody.append(line);
            }
            reader.close();
            return responseBody.toString();
        }catch (Exception ex){
            return ex.getMessage();
        }
    }

    @Override
    public Map<String, Object> test(String payload) {
        String apiUrl = String.format("%s/valsync/property/related", this.dataSync_api);
        Map<String, Object> nfo = new HashMap<>();
        try {
            HttpPost req = new HttpPost(apiUrl);
            HttpResponse response = dataSyncHttpClient.execute(req);
            nfo.put("responseStatusCode",response.getStatusLine().getStatusCode());
            nfo.put("responseBody",getResponseBody(response));
        }catch (Exception ex){
            nfo.put("responseStatusCode",500);
            nfo.put("responseBody", ex.getMessage());
            logger.error(ex.getMessage());
        }
        return nfo;
    }
}
