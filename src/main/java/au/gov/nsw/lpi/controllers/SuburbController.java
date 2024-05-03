package au.gov.nsw.lpi.controllers;

import au.gov.nsw.lpi.common.StandardisedResponse;
import au.gov.nsw.lpi.common.StandardisedResponseCode;
import au.gov.nsw.lpi.dao.BaseDao;
import au.gov.nsw.lpi.dao.SuburbDao;
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
@RequestMapping(value = "/suburb")
public class SuburbController extends BaseController{
    SuburbController(SuburbDao suburbDao, SecurityService securityService) {
        super();
        initialise((BaseDao)suburbDao, securityService);
    }

    @RequestMapping(value="/{entityName}/{actionName}", method = POST)
    public ResponseEntity<String> doPost(@PathVariable String entityName, @PathVariable String actionName, @RequestBody String requestBody, HttpServletRequest request) {

        StandardisedResponse standardisedResponse = securityValidationRequest(requestBody, request);
        if (standardisedResponse.code != StandardisedResponseCode.SUCCESS)
            return standardisedResponse.getResponseEntity();

        String entityAction = (entityName+"/"+actionName).toLowerCase();
        standardisedResponse.setData(entityAction);
        SuburbDao dao = (SuburbDao)this.iDao;
        switch(entityAction){
            case "street/update":
                standardisedResponse.setData(dao.STREET_UPSERT(requestBody));
                break;
            case "district/update":
                standardisedResponse.setData(dao.DISTRICT_UPSERT(requestBody));
                break;
            default:
                standardisedResponse = new StandardisedResponse(HttpStatus.BAD_REQUEST,"Invalid Request Action Call");
                break;

        }

        return standardisedResponse.getResponseEntity();
    }

}
