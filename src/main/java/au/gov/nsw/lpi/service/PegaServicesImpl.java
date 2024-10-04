package au.gov.nsw.lpi.service;

import au.gov.nsw.lpi.common.StandardisedResponse;
import au.gov.nsw.lpi.common.StandardisedResponseCode;
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

//    private final String dataSync_api;
////    private final String attachments_api;
//
////        private final String cases_api;
//    private final HttpClient dataSyncHttpClient;
//    private final HttpClient apiBaseHttpClient;

    private final PegaSyncingServices pegaSyncingServices;
    private final String endpoint_property_related = "/valsync/property/related";
    private final String endpoint_district_basedate = "/valsync/districtbasedate";
    private final String endpoint_supplementary_valuation = "/valsync/suppval";
    private final String endpoint_land_value = "/valsync/landvalue";

    private final String endpoint_attachments_upload = "/attachments/upload";
    private final String endpoint_cases_attachments = "/cases/{entityID}/attachments";


    public PegaServicesImpl(PegaSyncingServices pss) {
        this.pegaSyncingServices = pss;
        //this.dataSyncHttpClient = pegaSyncingServices.getHttpClient(PegaConfig.SyncServiceType.DATASYNC);
        //this.apiBaseHttpClient = pegaSyncingServices.getHttpClient(PegaConfig.SyncServiceType.CASES_ATTACHMENTS);
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
        Map<String, Object> nfo = new HashMap<>();// pegaSyncingServices.executeRequest(PegaConfig.SyncServiceType.ATTACHMENTS_UPLOAD, payload);

        boolean attachmentHasError = false;
        StringBuilder errorMessage = new StringBuilder();

        // 0. upload attachments and get ids
        JsonArray ajo = Utils.json2JsonObject(payload).getAsJsonArray("attachments");
        ArrayList<Map<String, String>> attachments = new ArrayList<>();
        for(int i=0;i<ajo.size();i++){
            StandardisedResponse standardisedResponse = new StandardisedResponse(pegaSyncingServices.executeRequest(PegaConfig.SyncServiceType.ATTACHMENTS_UPLOAD, ajo.get(i).getAsJsonObject().get("file").getAsString()));
            if(standardisedResponse.code == StandardisedResponseCode.SUCCESS){
                Map<String, String> uploaded_attachment = new HashMap<>();
                uploaded_attachment.put("type","File");
                uploaded_attachment.put("category","File");
                uploaded_attachment.put("name",ajo.get(i).getAsJsonObject().get("name").getAsString());
                uploaded_attachment.put("ID",Utils.json2JsonObject(standardisedResponse.data.toString()).get("ID").getAsString());
                attachments.add(uploaded_attachment);
            }
            else{
                attachmentHasError = true;
                errorMessage.append(String.format("Attachment %d: %s\n",i+1,standardisedResponse.data==null?standardisedResponse.message:standardisedResponse.data.toString()));
            }
        }
        // 1. associate attachments to entity
        if(!attachmentHasError){
            Map<String, Object> entity_payload = new HashMap<>();
            entity_payload.put("entity",Utils.json2JsonObject(payload).getAsJsonObject("entity"));
            entity_payload.put("attachments",attachments);
            StandardisedResponse standardisedResponse = new StandardisedResponse(pegaSyncingServices.executeRequest(PegaConfig.SyncServiceType.CASES_ATTACHMENTS_LINK, Utils.object2Json(entity_payload)));
            logger.debug(Utils.object2Json(standardisedResponse));
            nfo.put("responseStatusCode", standardisedResponse.httpStatus.value());
            nfo.put("responseBody", standardisedResponse.data.toString());
        }else{
            nfo.put("responseStatusCode", 500);
            nfo.put("responseBody", errorMessage.toString());
        }

        return nfo;
    }
/*
        // 0. upload attachments and get ids
        JsonArray ajo = Utils.json2JsonObject(payload).getAsJsonArray("attachments");
        Map<String, Object> upload_response = upload_attachments(ajo);
        logger.debug(Utils.object2Json(upload_response));
        Map<String, Object> nfo = new HashMap<>();
        nfo.put("responseStatusCode",upload_response.get("responseStatusCode"));
        nfo.put("responseBody", "");
        if(!upload_response.get("responseStatusCode").equals(200)) {
            nfo.put("responseBody", upload_response.get("responseBody"));
            return nfo;
        }

        ArrayList<Map<String, String>> attachments = (ArrayList<Map<String, String>>)upload_response.get("data");
        // 1. associate attachments to entity
        JsonObject ejo = Utils.json2JsonObject(payload).getAsJsonObject("entity");
        String entity_type = ejo.get("type").getAsString().toUpperCase();
        String entity_id = ejo.get("id").getAsString();

        String api_end_point = "";
        Map<String,Object> entity_payload = new HashMap<>();
        entity_payload.put("attachments",attachments);
/*
        /*
        if (entity_type.equals("CASE")) {
            api_end_point = this.endpoint_cases_attachments.replace("{entityID}", entity_id);
            nfo = sendRequest(apiBaseHttpClient, api_end_point, Utils.object2Json(entity_payload));
        } else {
            nfo.put("responseStatusCode", 500);
            nfo.put("responseBody", "Invalid Entity Type");
        }
        */
        //Map<String, Object> nfo = new HashMap<>();
        //nfo.put("responseStatusCode",200);
        //nfo.put("responseBody", "TO DO");


    private Map<String, Object> sendRequests(String api_end_point, String payload){
        HttpClient httpClient = null;
/*
        if(api_end_point.toLowerCase().startsWith("/attachments/"))
            httpClient = this.pegaSyncingServices.getHttpClient(PegaConfig.SyncServiceType.ATTACHMENTS_UPLOAD);
        else if(api_end_point.toLowerCase().startsWith("/cases/") && api_end_point.toLowerCase().endsWith("/attachments"))
            httpClient = this.pegaSyncingServices.getHttpClient(PegaConfig.SyncServiceType.CASES_ATTACHMENTS_LINK);
        else
            httpClient = this.pegaSyncingServices.getHttpClient(PegaConfig.SyncServiceType.DATASYNC);
*/
        return null;
        //return sendRequest(dataSyncHttpClient, this.dataSync_api+api_end_point, payload);
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

    private Map<String, Object> upload_attachments(JsonArray ajo){
/*
        ArrayList<Map<String, String>> attachments = new ArrayList<>();
        StringBuilder upload_errorMessage = new StringBuilder();
        String upload_attachments_api = this.endpoint_attachments_upload;
        for(int i=0;i<ajo.size();i++){
            JsonObject attachment = ajo.get(i).getAsJsonObject();
            String file_path = attachment.getAsJsonObject().get("file").getAsString();
            File file = Utils.getFileIfExists(file_path);
            Map<String, Object> upload_response = new HashMap<>();
            if(file!=null){
                Map<String, String> uploaded_attachment = new HashMap<>();
                uploaded_attachment.put("type","File");
                uploaded_attachment.put("category","File");
                uploaded_attachment.put("name",attachment.getAsJsonObject().get("name").getAsString());
                HttpPost req = new HttpPost(upload_attachments_api);
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.addBinaryBody("content", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
                HttpEntity multipart = builder.build();
                req.setEntity(multipart);
                try {
//                    setResponseMap(upload_response, this.apiBaseHttpClient.execute(req));
//                    uploaded_attachment.put("ID",Utils.json2JsonObject(upload_response.get("responseBody").toString()).get("ID").getAsString());
                    attachments.add(uploaded_attachment);
                } catch (Exception ex) {
                    upload_errorMessage.append(String.format("Attachment %d: %s\n",i+1,ex.getMessage()));
                }
            } else {
                upload_errorMessage.append(String.format("Attachment %d: Invalid File %s\n",i+1,file_path));
            }
        }
*/
        Map<String, Object> nfo = new HashMap<>();
//        if(upload_errorMessage.length() == 0) {
            nfo.put("responseStatusCode",200);
            nfo.put("responseBody", "SUCCESS");
//            nfo.put("data", attachments);
//        }else {
//            nfo.put("responseStatusCode",500);
//            nfo.put("responseBody", upload_errorMessage.toString());
//        }
        return nfo;
    }
    @Override
    public Map<String, Object> test(String payload) {
        String apiUrl = String.format("%s/valsync/property/related", "https://nswpe-valnet-dt1.pega.net/prweb/api/DataSync/v1");
        //HttpClient httpClient = this.pegaSyncingServices.getHttpClient(PegaConfig.SyncServiceType.DATASYNC);
        Map<String, Object> nfo = new HashMap<>();
//        try {
//            HttpPost req = new HttpPost(apiUrl);
//            HttpResponse response = httpClient.execute(req);
//            nfo.put("responseStatusCode",response.getStatusLine().getStatusCode());
//            nfo.put("responseBody",getResponseBody(response));
//        }catch (Exception ex){
//            nfo.put("responseStatusCode",500);
//            nfo.put("responseBody", ex.getMessage());
//            logger.error(ex.getMessage());
//        }
        return nfo;
    }
}
