package au.gov.nsw.lpi.service;

import au.gov.nsw.lpi.common.Utils;
import au.gov.nsw.lpi.domain.PegaConfig;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.springframework.stereotype.Service;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Service
public class PegaServicesImpl implements PegaServices {
    private final String baseurl;

    private final HttpClient httpClient;;

    public PegaServicesImpl(PegaConfig cfg){
        this.baseurl = cfg.datasync_api;
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(cfg.datasync_un, cfg.datasync_pw));
        this.httpClient = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
    }

    @Override
    public Map<String, Object> property_related(String payload) {
        String api_url = String.format("%s/valsync/property/related", this.baseurl);
        return sendRequests(api_url, payload);
    }

    @Override
    public Map<String, Object> district_basedate(String payload) {
        String api_url = String.format("%s/valsync/districtbasedate", this.baseurl);
        return sendRequests(api_url, payload);
    }

    @Override
    public Map<String, Object> supplementary_valuation(String payload) {
        String api_url = String.format("%s/valsync/suppval", this.baseurl);
        return sendRequests(api_url, payload);
    }

    @Override
    public Map<String, Object> land_value(String payload) {
        String api_url = String.format("%s/valsync/landvalue", this.baseurl);
        return sendRequests(api_url, payload);
    }

    private Map<String, Object> sendRequests(String api_url, String payload){
        Map<String, Object> nfo = new HashMap<>();
        try {
            HttpPost req = new HttpPost(api_url);
            req.setEntity(new StringEntity(payload));
            setResponseMap(nfo, httpClient.execute(req));
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
        String apiUrl = String.format("%s/valsync/property/related", this.baseurl);
        Map<String, Object> nfo = new HashMap<>();
        try {
            HttpPost req = new HttpPost(apiUrl);
            HttpResponse response = httpClient.execute(req);
            nfo.put("responseStatusCode",response.getStatusLine().getStatusCode());
            nfo.put("responseBody",getResponseBody(response));
        }catch (Exception ex){
            nfo.put("responseStatusCode",500);
            nfo.put("responseBody", ex.getMessage());
        }
        return nfo;
    }
}
