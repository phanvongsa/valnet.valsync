package au.gov.nsw.lpi.controllers;


import au.gov.nsw.lpi.common.StandardisedResponse;
import au.gov.nsw.lpi.common.StandardisedResponseCode;
import au.gov.nsw.lpi.common.Utils;
import au.gov.nsw.lpi.dao.ObjectionDao;
import au.gov.nsw.lpi.dao.PropertyDao;
import au.gov.nsw.lpi.domain.PegaConfig;
import au.gov.nsw.lpi.domain.ServerConfig;

import au.gov.nsw.lpi.service.SecurityServiceImpl;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final SecurityServiceImpl securityService;

    private final PegaConfig pegaConfig;

    private final DataSource dataSource;

    private final ObjectionDao objectionDao;

    public YugController(ServerConfig serverConfig, DataSource dataSource, PegaConfig pegaConfig, SecurityServiceImpl securityService, ObjectionDao objectionDao) {
        this.serverConfig = serverConfig;
        this.dataSource = dataSource;
        this.securityService = securityService;
        this.pegaConfig = pegaConfig;
        this.objectionDao = objectionDao;
    }

    @RequestMapping(value="/info", method = POST)
    public ResponseEntity<String> info(@RequestBody String requestBody, HttpServletRequest request) {
        StandardisedResponse standardisedResponse = securityService.requestSecurityCheckYug(request, requestBody);

        if(standardisedResponse.code!=StandardisedResponseCode.SUCCESS)
            return standardisedResponse.getResponseEntity();

        Map<String, Object> nfo = new HashMap<>();
        nfo.put("Server Environment", this.serverConfig.serverEnvironment);
        nfo.put("Allowed Ips", this.serverConfig.allowedIps);
        nfo.put("Api Key", this.securityService.apiKey.length());
        nfo.put("Data Source", testDataSources());
        nfo.put("Remote Address", Utils.getRequestRemoteAddress(request));
        nfo.put("Allow Access", this.securityService.isRequestValid(request) ? "Yes" : "No");
        nfo.put("Pega API Attachments", this.pegaConfig.attachments_api);
        nfo.put("Pega API Cases", this.pegaConfig.cases_api);
        nfo.put("Pega API DataSync", this.pegaConfig.datasync_api);
        nfo.put("Pega API Objections", this.pegaConfig.objections_api);
        nfo.put("Documents Upload Directory", this.pegaConfig.documents_upload_dir);
        nfo.put("Documents Upload Cleanup", this.pegaConfig.documents_upload_cleanup);
        standardisedResponse = new StandardisedResponse(HttpStatus.OK, nfo);

        return standardisedResponse.getResponseEntity();
    }

    @RequestMapping(value = "/test-{actionName}", method = POST)
    public ResponseEntity<String> doPost(@PathVariable String actionName, @RequestBody String requestBody, HttpServletRequest request) {
        StandardisedResponse standardisedResponse = securityService.requestSecurityCheckYug(request, requestBody);
        if(standardisedResponse.code!=StandardisedResponseCode.SUCCESS)
            return standardisedResponse.getResponseEntity();

        String payload = Utils.json2JsonObject(requestBody).get("payload").toString();
        switch (actionName.toLowerCase()){
            case "objectiondao.request_for_information_kit":
                standardisedResponse.setData(this.objectionDao.request_for_information_kit(payload));
                break;
            case "objectiondao.get_sales_analysis_report":
                standardisedResponse.setData(this.objectionDao.get_sales_analysis_report(payload));
                break;
            default:
                standardisedResponse = new StandardisedResponse(HttpStatus.BAD_REQUEST, "Invalid Request Action Call");
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
