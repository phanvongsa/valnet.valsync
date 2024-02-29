package au.gov.nsw.lpi.controllers;

import au.gov.nsw.lpi.common.StandardisedResponseCode;

import au.gov.nsw.lpi.common.StandardisedResponse;
import au.gov.nsw.lpi.common.Utils;
import au.gov.nsw.lpi.dao.ComponentDao;
import au.gov.nsw.lpi.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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

    private final SecurityService securityService;

    ComponentController(ComponentDao componentDao, SecurityService securityService) {
        this.componentDao = componentDao;
        this.securityService = securityService;
    }

    @RequestMapping(value="/{actionName}", method = POST)
    public ResponseEntity<String> doPost(@PathVariable String actionName, @RequestBody String requestBody, HttpServletRequest request) {

        StandardisedResponse standardisedResponse =securityService.requestSecurityCheck(request);
        if (standardisedResponse.code != StandardisedResponseCode.SUCCESS)
            return standardisedResponse.getResponseEntity();

        if(!Utils.isValidJson(requestBody))
            standardisedResponse = new StandardisedResponse(HttpStatus.BAD_REQUEST,"Malformed JSON");
        else{
            String response_db = "";
            switch(actionName.toLowerCase()){
                case "update":
                    standardisedResponse.setData(componentDao.update(requestBody));// data = ;
                    break;
                case "retrieve":
                    standardisedResponse.setData(componentDao.retrieve(requestBody));
                    break;
                default:
                    standardisedResponse = new StandardisedResponse(HttpStatus.BAD_REQUEST,"Invalid Request Action Call");
                    break;
            }
        }

        return standardisedResponse.getResponseEntity();
    }

}
