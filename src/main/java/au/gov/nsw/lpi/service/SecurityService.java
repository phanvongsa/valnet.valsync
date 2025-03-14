package au.gov.nsw.lpi.service;

import au.gov.nsw.lpi.common.StandardisedResponse;

import javax.servlet.http.HttpServletRequest;
public interface SecurityService {
    boolean isRequestValid(HttpServletRequest request);

    StandardisedResponse requestSecurityCheck(HttpServletRequest request);

    StandardisedResponse requestSecurityCheckYug(HttpServletRequest request, String requestBody);
}
