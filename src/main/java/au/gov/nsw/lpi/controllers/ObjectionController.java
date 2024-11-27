package au.gov.nsw.lpi.controllers;

import au.gov.nsw.lpi.common.StandardisedResponse;
import au.gov.nsw.lpi.common.StandardisedResponseCode;
import au.gov.nsw.lpi.dao.BaseDao;
import au.gov.nsw.lpi.dao.ObjectionDao;
import au.gov.nsw.lpi.service.PegaServices;
import au.gov.nsw.lpi.service.SecurityService;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static au.gov.nsw.lpi.common.Utils.json2JsonObject;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/objection")
public class ObjectionController extends BaseController {
    private final PegaServices ps;
    ObjectionController(ObjectionDao objectionDao, SecurityService securityService, PegaServices pegaServices) {
        super();
        this.ps = pegaServices;
        initialise((BaseDao) objectionDao, securityService);
    }

    @RequestMapping(value = "/{actionName}", method = POST)
    public ResponseEntity<String> doPost(@PathVariable String actionName, @RequestBody String requestBody, HttpServletRequest request) {

        StandardisedResponse standardisedResponse = securityValidationRequest(requestBody, request);
        if (standardisedResponse.code != StandardisedResponseCode.SUCCESS)
            return standardisedResponse.getResponseEntity();

        ObjectionDao dao = (ObjectionDao) this.iDao;
        switch (actionName.toLowerCase()) {
            case "rfikit":
                // this is a request for information kit AND also generates and associates the objection
                standardisedResponse.setData(dao.request_for_information_kit(requestBody));
                // if successful, the  set the objection to  processed
                if(standardisedResponse.code == StandardisedResponseCode.SUCCESS){
                    JsonObject jo_request_body = new JsonObject();
                    jo_request_body.add("pega_caseid", json2JsonObject(requestBody).get("pega_caseinskey"));
                    jo_request_body.add("docs_processed", new JsonPrimitive("true"));
                    standardisedResponse = ps.objections_rfidocs_proccessed(jo_request_body.toString());
                }
                break;
            case "getsalesanalysisreport":
                standardisedResponse.setData(dao.get_sales_analysis_report(requestBody));
                break;
            default:
                standardisedResponse = new StandardisedResponse(HttpStatus.BAD_REQUEST, "Invalid Request Action Call");
                break;
        }

        return standardisedResponse.getResponseEntity();
    }
}