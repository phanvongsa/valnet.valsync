package au.gov.nsw.lpi.service;

import au.gov.nsw.lpi.common.StandardisedResponse;

import java.util.Map;

public interface PegaServices {

    StandardisedResponse test(String payload);

    StandardisedResponse property_related(String payload);

    StandardisedResponse district_basedate(String payload);

    StandardisedResponse supplementary_valuation(String payload);

    StandardisedResponse land_value(String payload);

    StandardisedResponse attachments_associate(String payload);

}
