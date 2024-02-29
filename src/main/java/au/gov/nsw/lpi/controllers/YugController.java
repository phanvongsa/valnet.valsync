package au.gov.nsw.lpi.controllers;


import au.gov.nsw.lpi.common.StandardisedResponse;
import au.gov.nsw.lpi.common.StandardisedResponseCode;
import au.gov.nsw.lpi.common.Utils;
import au.gov.nsw.lpi.domain.AppConfig;
import au.gov.nsw.lpi.domain.ServerConfig;

import au.gov.nsw.lpi.service.SecurityService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import javax.servlet.http.HttpServletRequest;
@RestController
@RequestMapping(value = "/yug")
public class YugController{

    private static final Logger logger = LoggerFactory.getLogger(YugController.class);

    private final ServerConfig serverConfig;
    private final SecurityService securityService;

    private final DataSource dataSource;
    public YugController(ServerConfig serverConfig, DataSource dataSource,  SecurityService securityService) {
        this.serverConfig = serverConfig;
        this.dataSource = dataSource;
        this.securityService = securityService;
    }

    @RequestMapping(value="/info", method = POST)
    public ResponseEntity<String> info(@RequestBody String requestBody, HttpServletRequest request) {
        logger.info("Received request: " + requestBody);
        StandardisedResponse standardisedResponse = securityService.requestSecurityCheck(request);
        if(standardisedResponse.code!=StandardisedResponseCode.SUCCESS)
            return standardisedResponse.getResponseEntity();

        if(!requestBody.equals("iL0v3Guy!"))
            standardisedResponse = new StandardisedResponse(HttpStatus.FORBIDDEN,null);
        else{
            Map<String, Object> nfo = new HashMap<>();
            nfo.put("Server Environment", this.serverConfig.serverEnvironment);
            nfo.put("Allowed Ips", this.serverConfig.allowedIps);
            nfo.put("Api Key", this.securityService.apiKey.length());
            nfo.put("Data Source", testDataSources());
            nfo.put("Remote Address", Utils.getRequestRemoteAddress(request));
            nfo.put("Allow Access", this.securityService.isRequestValid(request) ? "Yes" : "No");
            standardisedResponse = new StandardisedResponse(HttpStatus.OK, nfo);
        }
        return standardisedResponse.getResponseEntity();
    }

    @RequestMapping(value="/retrieve", method = POST)
    public ResponseEntity<String> retrieve(@RequestBody String requestBody) {
        logger.info("Received request: " + requestBody);
        if(!Utils.isValidJson(requestBody))
            return Utils.getAsResponseEntity("Error Invalid JSON");

        return Utils.getAsResponseEntity("AOK");
    }

    private String testDataSources() {
        try {
            this.dataSource.getConnection();
            return "Connection Successful!";
        } catch (Exception e) {
            return "Connection Failed!";
        }
    }
}
