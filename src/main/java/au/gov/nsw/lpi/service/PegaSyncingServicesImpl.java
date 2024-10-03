package au.gov.nsw.lpi.service;

import au.gov.nsw.lpi.domain.PegaConfig;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;

@Component
public class PegaSyncingServicesImpl implements PegaSyncingServices {

    private final HttpClient dataSyncHttpClient;
    private final HttpClient baseHttpClient;

    public PegaSyncingServicesImpl(PegaConfig cfg) {
        CredentialsProvider dataSyncCredentialsProvider = new BasicCredentialsProvider();
        dataSyncCredentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(cfg.datasync_un, cfg.datasync_pw));
        this.dataSyncHttpClient = HttpClients.custom().setDefaultCredentialsProvider(dataSyncCredentialsProvider).build();

        CredentialsProvider baseCredentialsProvider = new BasicCredentialsProvider();
        baseCredentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(cfg.datasync_un, cfg.datasync_pw));
        this.baseHttpClient = HttpClients.custom().setDefaultCredentialsProvider(baseCredentialsProvider).build();
    }
    @Override
    public HttpClient getHttpClient(PegaConfig.SyncServiceType serviceType) {
        switch (serviceType) {
            case DATASYNC:
                return dataSyncHttpClient;
            case ATTACHMENTS:
            case CASES_ATTACHMENTS:
                return baseHttpClient;
        }
        return null;
    }
}
