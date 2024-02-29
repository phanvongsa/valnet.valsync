package au.gov.nsw.lpi.service;

import au.gov.nsw.lpi.common.StandardisedResponse;
import au.gov.nsw.lpi.common.Utils;
import au.gov.nsw.lpi.domain.ServerConfig;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class SecurityService {
    public final String apiKey;
    private final String allowedIps;
    private final String apiKeyName;

    public SecurityService(ServerConfig serverConfig) {
        this.apiKey = serverConfig.apiKey;
        this.apiKeyName = serverConfig.apiKeyName;
        this.allowedIps = serverConfig.allowedIps;
    }

    public boolean isRequestValid(HttpServletRequest request) {
        // check request header api key matches expected
        if(request.getHeader(this.apiKeyName)== null || !request.getHeader(this.apiKeyName).equals(this.apiKey))
            return false;

        // check allowed ips
        return this.allowedIps.equals("*") || Utils.isInCommaDelimitedList(this.allowedIps, Utils.getRequestRemoteAddress(request));
    }

    public StandardisedResponse requestSecurityCheck(HttpServletRequest request) {
        if(isRequestValid(request))
            return new StandardisedResponse(HttpStatus.OK,null);

        return new StandardisedResponse(HttpStatus.UNAUTHORIZED, null);
    }
}
