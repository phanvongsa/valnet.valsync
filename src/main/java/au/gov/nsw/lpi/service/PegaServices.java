package au.gov.nsw.lpi.service;

import java.util.Map;

public interface PegaServices {

    Map<String, Object> test(String payload);

    Map<String, Object> property_related(String payload);

    Map<String, Object> district_basedate(String payload);

    Map<String, Object> supplementary_valuation(String payload);

    Map<String, Object> land_value(String payload);
}
