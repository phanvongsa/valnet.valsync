package au.gov.nsw.lpi.service;

import au.gov.nsw.lpi.common.Utils;
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
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class PegaSyncingServicesImpl implements PegaSyncingServices {
    protected static final Logger logger = LoggerFactory.getLogger(PegaSyncingServices.class);
    private final HttpClient dataSyncHttpClient;
    private final HttpClient baseHttpClient;
    private final PegaConfig cfg;
    public PegaSyncingServicesImpl(PegaConfig pegaConfig) {
        this.cfg = pegaConfig;
        CredentialsProvider dataSyncCredentialsProvider = new BasicCredentialsProvider();
        dataSyncCredentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(cfg.datasync_un, cfg.datasync_pw));
        this.dataSyncHttpClient = HttpClients.custom().setDefaultCredentialsProvider(dataSyncCredentialsProvider).build();

        CredentialsProvider baseCredentialsProvider = new BasicCredentialsProvider();
        baseCredentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(cfg.datasync_un, cfg.datasync_pw));
        this.baseHttpClient = HttpClients.custom().setDefaultCredentialsProvider(baseCredentialsProvider).build();
    }

    @Override
    public HttpResponse executeRequest(PegaConfig.SyncServiceType serviceType, String payload) {
        HttpPost request = createRequestsPost(serviceType, payload);
        HttpClient httpClient =  getHttpClient(serviceType);
        int statusCode = 200;
        String reasonPhrase = "OK";
        String responseString = null;
        if(request==null || httpClient==null){
            statusCode = 400;
            reasonPhrase = request==null?"Invalid Request Service Type or Payload":"Invalid HttpClient";
            switch (serviceType) {
                case ATTACHMENTS_UPLOAD:
                    responseString = String.format("Unable to create request %s, file: %s does not exists",serviceType,payload);
                    break;
                default:
                    responseString = String.format("Unable to create request %s, Payload: %s",serviceType, payload);
                    break;
            }
            //responseString = request==null?String.format("Unable to create request, ",serviceType==PegaConfig.SyncServiceType.ATTACHMENTS_UPLOAD?"file: "+payload+ "does not exists":"Payload: "+payload):null;
        }else{
            try {
                return httpClient.execute(request);
            } catch (Exception ex) {
                statusCode = 500;
                reasonPhrase = ex.getMessage();
                responseString = ex.getMessage();
            }
        }

        BasicHttpResponse response = new BasicHttpResponse(null, statusCode, reasonPhrase);
        if(responseString!=null){
            try {
                response.setEntity(new StringEntity(responseString));
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return response;
    }

    private HttpPost createRequestsPost(PegaConfig.SyncServiceType serviceType, String payload){
        HttpPost request = null;
        logger.debug(String.format("Creating request for %s",serviceType));
        switch (serviceType) {
//            case DATASYNC:
//                request = new HttpPost(getApiUrl(serviceType));
//                request.setHeader("Content-Type", "application/json");
//                request.setEntity(new StringEntity(payload, "UTF-8"));
//                break;
//            case CASES_ATTACHMENTS_LINK:
//                request = new HttpPost(getApiUrl(serviceType).replace("{ENTITY_ID}",payload));
//                break;
            case ATTACHMENTS_UPLOAD:
                File file = Utils.getFileIfExists(payload);
                if(file!=null) {
                    request = new HttpPost(getApiUrl(serviceType));
                    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                    builder.addBinaryBody("content", file, org.apache.http.entity.ContentType.APPLICATION_OCTET_STREAM, file.getName());
                    request.setEntity(builder.build());
                }
                break;
        }
//        if(serviceType == PegaConfig.SyncServiceType.ATTACHMENTS_UPLOAD){
//            File file = Utils.getFileIfExists(payload);
//            if(file!=null){
//                request = new HttpPost(getApiUrl(serviceType));
//                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//                builder.addBinaryBody("content", file, org.apache.http.entity.ContentType.APPLICATION_OCTET_STREAM, file.getName());
//                request.setEntity(builder.build());
//            }else{
//                logger.error(String.format("Unable to create request, file doesnt exists: %s",payload));
//            }
//        }

        return request;
    }
    private String getApiUrl(PegaConfig.SyncServiceType serviceType){
        switch (serviceType) {
            case DATASYNC:
                return this.cfg.datasync_api;
            case ATTACHMENTS_UPLOAD:
                return this.cfg.base_api+"/attachments/upload";
            case CASES_ATTACHMENTS_LINK:
                return this.cfg.base_api+"/cases/{ENTITY_ID}/attachments/";
        }
        return null;
    }

    private HttpClient getHttpClient(PegaConfig.SyncServiceType serviceType) {
        switch (serviceType) {
            case DATASYNC:
                return this.dataSyncHttpClient;
            case ATTACHMENTS_UPLOAD:
            case CASES_ATTACHMENTS_LINK:
                return this.baseHttpClient;
        }
        return null;
    }
}
