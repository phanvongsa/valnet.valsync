package au.gov.nsw.lpi.controllers;

import au.gov.nsw.lpi.common.StandardisedResponse;
import au.gov.nsw.lpi.common.StandardisedResponseCode;
import au.gov.nsw.lpi.dao.IBaseDao;
import au.gov.nsw.lpi.dao.IComponentDao;
import au.gov.nsw.lpi.service.ISecurityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/component")
public class ComponentController extends BaseController{
    ComponentController(IComponentDao componentDao, ISecurityService securityService) {
        super((IBaseDao)componentDao, securityService);
    }

    @RequestMapping(value="/{actionName}", method = POST)
    public ResponseEntity<String> doPost(@PathVariable String actionName, @RequestBody String requestBody, HttpServletRequest request) {

        StandardisedResponse standardisedResponse = securityValidationRequest(requestBody, request);
        if (standardisedResponse.code != StandardisedResponseCode.SUCCESS)
            return standardisedResponse.getResponseEntity();

        IComponentDao dao = (IComponentDao)this.iDao;
        switch(actionName.toLowerCase()){
            case "update":
                standardisedResponse.setData(dao.update(requestBody));
                break;
            case "retrieve":
                standardisedResponse.setData(dao.retrieve(requestBody));
                break;
            default:
                standardisedResponse = new StandardisedResponse(HttpStatus.BAD_REQUEST,"Invalid Request Action Call");
                break;

        }

        return standardisedResponse.getResponseEntity();
    }

}
