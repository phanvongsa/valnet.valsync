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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.HttpEntity;
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

    private final String endpoint_attachments_upload = "/upload";

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
//        Object ejo = Utils.json2JsonObject(payload).getAsJsonObject("entity");

        Map<String, Object> nfo = new HashMap<>();
        ArrayList<Map<String, String>> attachments = new ArrayList<>();
        // upload attachments
        StringBuilder uploadErrorMessage = new StringBuilder();
        JsonArray ajo = Utils.json2JsonObject(payload).getAsJsonArray("attachments");
        for(int i=0;i<ajo.size();i++){
            JsonObject attachment = ajo.get(i).getAsJsonObject();
            Map<String, String> uploaded_attachment = new HashMap<>();
            uploaded_attachment.put("type","File");
            uploaded_attachment.put("category","File");
            uploaded_attachment.put("name",attachment.getAsJsonObject().get("name").getAsString());

            Map<String, Object> upload_response = new HashMap<>();
            String file_path = attachment.getAsJsonObject().get("file").getAsString();
            File file = Utils.getFileIfExists(file_path);
            if(file!=null){
                HttpPost req = new HttpPost(api_url);
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.addBinaryBody("content", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
                HttpEntity multipart = builder.build();
                req.setEntity(multipart);
                try {
                    setResponseMap(upload_response, this.attachmentsHttpClient.execute(req));
                } catch (Exception ex) {
                    setResponseMap(upload_response, ex);
                }
                if(upload_response.get("responseStatusCode").equals(201)){
                    uploaded_attachment.put("ID",Utils.json2JsonObject(upload_response.get("responseBody").toString()).get("ID").getAsString());
                    attachments.add(uploaded_attachment);
                }
            }else{
                uploadErrorMessage.append(String.format("Attachment %d: Invalid File %s\n",i+1,file_path));
            }
        }

        // associate to case
        if(uploadErrorMessage.length() == 0) {
            System.out.println(Utils.object2Json(attachments));
            // to do: associate attachments to case

            nfo.put("responseStatusCode",200);
            nfo.put("responseBody", "SUCCESS");
        }else{
            nfo.put("responseStatusCode",500);
            nfo.put("responseBody", uploadErrorMessage.toString());
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
