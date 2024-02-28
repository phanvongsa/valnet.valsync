package au.gov.nsw.lpi.common;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class Security {
    //private static final Logger logger = LoggerFactory.getLogger(Security.class);
    public final String apiKey;
    private final String allowedIps;
    private final String apiKeyName;

    public Security(Environment env) {
        this.apiKey = env.getProperty("api.key");
        this.allowedIps = env.getProperty("allowed.ips");
        this.apiKeyName = env.getProperty("api.keyName");
    }

    public boolean isRequestValid(HttpServletRequest request) {
        // check request header api key matches expected
        if(request.getHeader(apiKeyName)== null || !request.getHeader(apiKeyName).equals(apiKey)){
            return false;
        }

        // check allowed ips
        return allowedIps.equals("*") || Utils.isInCommaDelimitedList(allowedIps, Utils.getRequestRemoteAddress(request));
    }
}
