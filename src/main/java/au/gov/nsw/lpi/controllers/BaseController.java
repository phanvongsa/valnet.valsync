package au.gov.nsw.lpi.controllers;

import au.gov.nsw.lpi.common.StandardisedResponse;
import au.gov.nsw.lpi.common.StandardisedResponseCode;
import au.gov.nsw.lpi.common.Utils;
import au.gov.nsw.lpi.dao.BaseDao;
import au.gov.nsw.lpi.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public abstract class BaseController {
    protected static final Logger logger = LoggerFactory.getLogger(ComponentController.class);
    protected BaseDao iDao;

    protected SecurityService securityService;

    protected void initialise(BaseDao iDao, SecurityService securityService){
        this.iDao = iDao;
        this.securityService = securityService;
    }

    public StandardisedResponse securityValidationRequest(String requestBody, HttpServletRequest request){
        // security check
        StandardisedResponse standardisedResponse = securityService.requestSecurityCheck(request);
        if (standardisedResponse.code != StandardisedResponseCode.SUCCESS)
            return standardisedResponse;

        if(!Utils.isValidJson(requestBody))
            standardisedResponse = new StandardisedResponse(HttpStatus.BAD_REQUEST,"Malformed JSON");

        return standardisedResponse;
    }
}
