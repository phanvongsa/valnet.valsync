package au.gov.nsw.lpi.controllers;

import au.gov.nsw.lpi.common.ResponseCode;
import au.gov.nsw.lpi.common.Security;
import au.gov.nsw.lpi.common.StandardisedResponse;
import au.gov.nsw.lpi.common.Utils;
import au.gov.nsw.lpi.dao.ComponentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/component")
public class ComponentController {
    private static final Logger logger = LoggerFactory.getLogger(ComponentController.class);
    private final ComponentDao componentDao;

    private final Security security;

    ComponentController(ComponentDao componentDao, Security security) {
        this.componentDao = componentDao;
        this.security = security;
    }

    @RequestMapping(value="/{actionName}", method = POST)
    public ResponseEntity<String> doPost(@PathVariable String actionName, @RequestBody String requestBody, HttpServletRequest request) {
        logger.info("Received request: " + requestBody);
        StandardisedResponse securityResponse = securityRequestCheck(request);
        if(securityResponse.code != ResponseCode.SUCCESS)
            return Utils.getAsResponseEntity(securityResponse.message);

        if(!Utils.isValidJson(requestBody))
            return Utils.getAsResponseEntity("Error Invalid JSON");

        String response_db = "";
        switch(actionName.toLowerCase()){
            case "update":
                response_db = componentDao.update(requestBody);
                break;
            case "retrieve":
                response_db = componentDao.retrieve(requestBody);
                break;
            default:
                response_db = "Error: Invalid Action";
                break;
        }
        return Utils.getAsResponseEntity(response_db);
    }

    private StandardisedResponse securityRequestCheck(HttpServletRequest request) {
        if(security.isRequestValid(request))
            return new StandardisedResponse(ResponseCode.SUCCESS, "Security Pass", null);

        return new StandardisedResponse(ResponseCode.ERROR, "Error: Security Fail", null);

    }
}
