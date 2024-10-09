package au.gov.nsw.lpi.controllers;

import au.gov.nsw.lpi.common.StandardisedResponse;
import au.gov.nsw.lpi.common.StandardisedResponseCode;
import au.gov.nsw.lpi.dao.BaseDao;
import au.gov.nsw.lpi.dao.ObjectionDao;
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
@RequestMapping(value = "/objection")
public class ObjectionController extends BaseController {

    ObjectionController(ObjectionDao objectionDao, SecurityService securityService) {
        super();
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
                standardisedResponse.setData(dao.request_for_information_kit(requestBody));
                break;
            default:
                standardisedResponse = new StandardisedResponse(HttpStatus.BAD_REQUEST, "Invalid Request Action Call");
                break;
        }

        return standardisedResponse.getResponseEntity();
    }
}