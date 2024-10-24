package au.gov.nsw.lpi.service;

import au.gov.nsw.lpi.common.Utils;
import au.gov.nsw.lpi.domain.PegaConfig;
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
        HttpClient httpClient =  getHttpClient(serviceType);
        HttpPost request = createRequestsPost(serviceType, payload);
        int statusCode = 200;
        String reasonPhrase = "OK";
        String responseString = null;

        if(request==null || httpClient==null){
            statusCode = 400;
            reasonPhrase = request==null?"Invalid Request Service Type or Payload":"Invalid HttpClient";
            if (serviceType == PegaConfig.SyncServiceType.ATTACHMENTS_UPLOAD) {
                responseString = String.format("Unable to create request %s, file: %s does not exists", serviceType, payload);
            } else {
                responseString = String.format("Unable to create request %s, Payload: %s", serviceType, payload);
            }
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

    @Override
    public String saveDocumentFile(String base64String_data, String fileName) {
        return Utils.saveBase64File(base64String_data, this.cfg.documents_upload_dir+"/"+fileName);
    }

    private HttpPost createRequestsPost(PegaConfig.SyncServiceType serviceType, String payload){
        HttpPost request = null;
        String api_endpoint = this.cfg.getApiEndpoint(serviceType);
        logger.debug("Pega API Endpoint: "+api_endpoint);
        switch (serviceType) {
            case CASES_ATTACHMENTS_LINK:
                String entity_id = Utils.json2JsonObject(payload).getAsJsonObject("entity").get("id").getAsString();
                String attachments_payload = String.format("{\"attachments\":%s}",Utils.json2JsonObject(payload).getAsJsonArray("attachments").toString());
                api_endpoint = api_endpoint.replace("{ENTITY_ID}",Utils.urlEncode(entity_id));
                request = new HttpPost(api_endpoint);
                request.setHeader("Content-Type", "application/json");
                request.setEntity(new StringEntity(attachments_payload, "UTF-8"));
                break;
            case ATTACHMENTS_UPLOAD:
                File file = Utils.getFileIfExists(payload);
                if(file!=null) {
                    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                    builder.addBinaryBody("content", file, org.apache.http.entity.ContentType.APPLICATION_OCTET_STREAM, file.getName());
                    request = new HttpPost(api_endpoint);
                    request.setEntity(builder.build());
                }
                break;
            default:
                request = new HttpPost(api_endpoint);
                request.setHeader("Content-Type", "application/json");
                request.setEntity(new StringEntity(payload, "UTF-8"));
                break;
        }

        return request;
    }

    private HttpClient getHttpClient(PegaConfig.SyncServiceType serviceType) {
        return serviceType.toString().startsWith("DATASYNC_") ? this.dataSyncHttpClient : this.baseHttpClient;
    }
}
