package au.gov.nsw.lpi.controllers;

import au.gov.nsw.lpi.common.StandardisedResponse;
import au.gov.nsw.lpi.common.StandardisedResponseCode;
import au.gov.nsw.lpi.common.Utils;
import au.gov.nsw.lpi.dao.PropertyDao;
import au.gov.nsw.lpi.service.PegaServices;
import au.gov.nsw.lpi.service.SecurityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/pega")
public class PegaController extends BaseController {
    private final PegaServices pegaServices;
    PegaController(PegaServices pegaServices, SecurityService securityService) {
        super();
        initialise(null, securityService);
        this.pegaServices = pegaServices;
    }

    @RequestMapping(value="/test", method = POST)
    public ResponseEntity<String> doPost(@RequestBody String requestBody, HttpServletRequest request) {
        StandardisedResponse standardisedResponse = securityValidationRequest(requestBody, request);
        if (standardisedResponse.code != StandardisedResponseCode.SUCCESS)
            return standardisedResponse.getResponseEntity();

        standardisedResponse = pegaServices.test(requestBody);
        return standardisedResponse.getResponseEntity();
    }

    @RequestMapping(value="/{entityName}/{actionName}", method = POST)
    public ResponseEntity<String> doPost(@PathVariable String entityName, @PathVariable String actionName, @RequestBody String requestBody, HttpServletRequest request) {
        StandardisedResponse standardisedResponse = securityValidationRequest(requestBody, request);
        if (standardisedResponse.code != StandardisedResponseCode.SUCCESS)
            return standardisedResponse.getResponseEntity();

        String entityAction = (entityName+"/"+actionName).toLowerCase();
        switch(entityAction){
            case "property/related":
                standardisedResponse = pegaServices.property_related(requestBody);
                break;
            case "district/basedate":
                standardisedResponse = pegaServices.district_basedate(requestBody);
                break;
            case "supplementary/valuation":
                standardisedResponse = pegaServices.supplementary_valuation(requestBody);
                break;
            case "land/value":
                standardisedResponse = pegaServices.land_value(requestBody);
                break;
            case "attachments/associate":
                standardisedResponse = pegaServices.attachments_associate(requestBody);
                break;
            case "objections/rfidocs_processed":
                standardisedResponse = pegaServices.objections_rfidocs_proccessed(requestBody);
                break;
            default:
                standardisedResponse = new StandardisedResponse(HttpStatus.BAD_REQUEST,String.format("Invalid Request Action Call (%s)",entityAction));
                break;
        }
        return standardisedResponse.getResponseEntity();
    }


//    private void setWithPegaResponse(StandardisedResponse standardisedResponse, Map<String,Object> pegaResponse){
//        logger.debug(Utils.object2Json(pegaResponse));
//        // updated to accept 200, 201, 202
//        if((int)pegaResponse.get("responseStatusCode")>= 200 && (int)pegaResponse.get("responseStatusCode")<= 202)
//            standardisedResponse.setData(pegaResponse.get("responseBody").toString());
//        else{
//            standardisedResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
//            standardisedResponse.setData(pegaResponse.get("responseBody").toString());
//        }
//    }
}
