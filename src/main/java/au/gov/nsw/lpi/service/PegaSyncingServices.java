package au.gov.nsw.lpi.service;

import au.gov.nsw.lpi.domain.PegaConfig;
import org.apache.http.client.HttpClient;

public interface PegaSyncingServices {
    public HttpClient getHttpClient(PegaConfig.SyncServiceType serviceType);
}
