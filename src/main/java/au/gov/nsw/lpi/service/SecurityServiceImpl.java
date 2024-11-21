package au.gov.nsw.lpi.service;

import au.gov.nsw.lpi.common.StandardisedResponse;
import au.gov.nsw.lpi.common.Utils;
import au.gov.nsw.lpi.controllers.BaseController;
import au.gov.nsw.lpi.domain.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;

@Service
public class SecurityServiceImpl implements SecurityService {
    public final String apiKey;
    private final String allowedIps;
    private final String apiKeyName;
    protected static final Logger logger = LoggerFactory.getLogger(SecurityService.class);

    public SecurityServiceImpl(ServerConfig serverConfig) {
        this.apiKey = serverConfig.apiKey;
        this.apiKeyName = serverConfig.apiKeyName;
        this.allowedIps = serverConfig.allowedIps;
    }

    @Override
    public boolean isRequestValid(HttpServletRequest request) {

        // check request header api key matches expected
        if(request.getHeader(this.apiKeyName)== null || !request.getHeader(this.apiKeyName).equals(this.apiKey))
            return false;

        // check allowed ips
        return this.allowedIps.equals("*") || Utils.isInCommaDelimitedList(this.allowedIps, Utils.getRequestRemoteAddress(request));
    }

    @Override
    public StandardisedResponse requestSecurityCheck(HttpServletRequest request) {
        requestInfo(request);
        if(isRequestValid(request))
            return new StandardisedResponse(HttpStatus.OK,null);

        return new StandardisedResponse(HttpStatus.UNAUTHORIZED, null);
    }

    private void requestInfo(HttpServletRequest request){
        // Get all the header names
        Enumeration<String> headerNames = request.getHeaderNames();
        logger.debug("===== Request Info "+request.getRequestURL().toString()+" =====");
        // Iterate through all the header names and get their values
        logger.debug("Remote Host: "+request.getRemoteHost());
        logger.debug("Remote Address: "+request.getRemoteAddr());
        logger.debug("Referer: "+request.getHeader("Referer"));
        logger.debug("Origin: "+request.getHeader("Origin"));
        logger.debug("Forward For: "+request.getHeader("X-FORWARDED-FOR"));
        logger.debug("User-Agent: "+request.getHeader("User-Agent"));
        logger.debug("Content-Type: "+request.getHeader("Content-Type"));
        try {
            InetAddress addr = InetAddress.getByName(request.getRemoteAddr());
            logger.debug("Remote Host InetAddress: "+ addr.getHostName());
            logger.debug("Remote Address InetAddress: "+addr.getHostAddress());
        } catch (UnknownHostException ex) {
            logger.error(ex.getMessage());
        }

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if(!headerName.equalsIgnoreCase("sync-key")){
                Enumeration<String> headerValues = request.getHeaders(headerName);
                while (headerValues.hasMoreElements()) {
                    String headerValue = headerValues.nextElement();
                    logger.debug(headerName + ": " + headerValue);
                }
            }

        }
    }
}
