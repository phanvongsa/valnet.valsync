package au.gov.nsw.lpi.service;

import au.gov.nsw.lpi.domain.PegaConfig;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import java.util.Map;

public interface PegaSyncingServices {
    //public HttpClient getHttpClient(PegaConfig.SyncServiceType serviceType);

    public HttpResponse executeRequest(PegaConfig.SyncServiceType serviceType, String payload);

    public String saveDocumentFile(String fileName, String base64String_data);

    public void cleanupDocumentFile(String fileName);

    //public HttpPost createRequestPost(PegaConfig.SyncServiceType serviceType, String payload);
}
