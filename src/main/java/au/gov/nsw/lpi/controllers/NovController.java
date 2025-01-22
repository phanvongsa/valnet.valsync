package au.gov.nsw.lpi.controllers;

import au.gov.nsw.lpi.common.StandardisedResponse;
import au.gov.nsw.lpi.common.StandardisedResponseCode;
import au.gov.nsw.lpi.dao.NovDao;
import au.gov.nsw.lpi.service.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/nov")
public class NovController extends BaseController{
    NovController(NovDao novDao, SecurityService securityService){
        super();
        initialise(novDao, securityService);
    }

    @RequestMapping(value="/{entityName}/{actionName}", method = POST)
    public ResponseEntity<String> doPost(@PathVariable String entityName, @PathVariable String actionName, @RequestBody String requestBody, HttpServletRequest request) {
        return executeEntityAction(entityName+"/"+actionName, requestBody, request);
    }

    private ResponseEntity<String> executeEntityAction(String entityAction, String requestBody, HttpServletRequest request) {
        StandardisedResponse standardisedResponse = securityValidationRequest(requestBody, request);
        if (standardisedResponse.code != StandardisedResponseCode.SUCCESS)
            return standardisedResponse.getResponseEntity();

        NovDao dao = (NovDao)this.iDao;
        switch(entityAction.toLowerCase()){
            case "preferences/update":
                standardisedResponse.setData(dao.update_preferences(requestBody));
                break;
            case "preferences/get":
                standardisedResponse.setData(dao.get_preferences(requestBody));
                break;
            default:
                standardisedResponse = new StandardisedResponse(HttpStatus.BAD_REQUEST,String.format("Invalid Request Action Call (%s)",entityAction));
                break;
        }

        return standardisedResponse.getResponseEntity();
    }
}
