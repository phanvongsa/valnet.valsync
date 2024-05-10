package au.gov.nsw.lpi.controllers;

import au.gov.nsw.lpi.common.StandardisedResponse;
import au.gov.nsw.lpi.common.StandardisedResponseCode;
import au.gov.nsw.lpi.dao.ApportionmentDao;
import au.gov.nsw.lpi.service.SecurityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/apportionment")
public class ApportionmentController extends BaseController{
    ApportionmentController(ApportionmentDao apportionmentDao, SecurityService securityService) {
        super();
        initialise(apportionmentDao, securityService);
    }

    @RequestMapping(value="/{actionName}", method = POST)
    public ResponseEntity<String> doPost(@PathVariable String actionName, @RequestBody String requestBody, HttpServletRequest request) {
        return executeEntityAction(actionName, requestBody, request);
    }

//    @RequestMapping(value="/{entityName}/{actionName}", method = POST)
//    public ResponseEntity<String> doPost(@PathVariable String entityName, @PathVariable String actionName, @RequestBody String requestBody, HttpServletRequest request) {
//        return executeEntityAction(entityName+"/"+actionName, requestBody, request);
//    }


    public ResponseEntity<String> executeEntityAction(String entityAction, String requestBody, HttpServletRequest request) {
        StandardisedResponse standardisedResponse = securityValidationRequest(requestBody, request);
        if (standardisedResponse.code != StandardisedResponseCode.SUCCESS)
            return standardisedResponse.getResponseEntity();

        ApportionmentDao dao = (ApportionmentDao)this.iDao;
        switch(entityAction.toLowerCase()){
            case "update":
                standardisedResponse.setData(dao.upsert(requestBody));
                break;
//            case "suppval/update":
//                standardisedResponse.setData(dao.upsert_supplementary_value(requestBody));
//                break;
            default:
                standardisedResponse = new StandardisedResponse(HttpStatus.BAD_REQUEST,String.format("Invalid Request Action Call (%s)",entityAction));
                break;
        }

        return standardisedResponse.getResponseEntity();
    }

}

