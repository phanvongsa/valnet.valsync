package au.gov.nsw.lpi.service;

import au.gov.nsw.lpi.common.StandardisedResponse;
import au.gov.nsw.lpi.common.StandardisedResponseCode;
import au.gov.nsw.lpi.common.Utils;
import au.gov.nsw.lpi.domain.PegaConfig;
import com.google.gson.JsonArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PegaServicesImpl implements PegaServices {
    protected static final Logger logger = LoggerFactory.getLogger(PegaServices.class);

    private final PegaSyncingServices pegaSyncingServices;

    public PegaServicesImpl(PegaSyncingServices pss) {
        this.pegaSyncingServices = pss;
    }

    @Override
    public StandardisedResponse test(String payload) {
        return new StandardisedResponse(pegaSyncingServices.executeRequest(PegaConfig.SyncServiceType.DATASYNC_PROPERTY_RELATED, payload));
    }

    @Override
    public StandardisedResponse property_related(String payload) {
        return new StandardisedResponse(pegaSyncingServices.executeRequest(PegaConfig.SyncServiceType.DATASYNC_PROPERTY_RELATED, payload));
    }

    @Override
    public StandardisedResponse district_basedate(String payload) {
        return new StandardisedResponse(pegaSyncingServices.executeRequest(PegaConfig.SyncServiceType.DATASYNC_DISTRICT_BASE_DATE, payload));
    }

    @Override
    public StandardisedResponse supplementary_valuation(String payload) {
        return new StandardisedResponse(pegaSyncingServices.executeRequest(PegaConfig.SyncServiceType.DATASYNC_SUPPLEMENTARY_VALUATION, payload));
    }

    @Override
    public StandardisedResponse land_value(String payload) {
        return new StandardisedResponse(pegaSyncingServices.executeRequest(PegaConfig.SyncServiceType.DATASYNC_LAND_VALUE, payload));
    }

    @Override
    public StandardisedResponse attachments_associate(String payload) {

        boolean attachmentHasError = false;
        StringBuilder errorMessage = new StringBuilder();
        StandardisedResponse standardisedResponse = new StandardisedResponse(HttpStatus.NO_CONTENT,"PENDING ACTION");
        // 0. upload attachments and get ids
        JsonArray ajo = Utils.json2JsonObject(payload).getAsJsonArray("attachments");
        ArrayList<Map<String, String>> attachments = new ArrayList<>();
        logger.debug("Processing Attachments");
        for(int i=0;i<ajo.size();i++){
            String attachment_name = ajo.get(i).getAsJsonObject().get("name").getAsString();
            logger.debug("  "+i+": "+ attachment_name);
            String attachment_filepath = pegaSyncingServices.saveDocumentFile(ajo.get(i).getAsJsonObject().get("data").getAsString(),UUID.randomUUID().toString()+Utils.getFileExtension(attachment_name));
            logger.debug("      ==> Saving as "+attachment_filepath);
            standardisedResponse = new StandardisedResponse(pegaSyncingServices.executeRequest(PegaConfig.SyncServiceType.ATTACHMENTS_UPLOAD, attachment_filepath));
            logger.debug("      ==> Uploading "+(standardisedResponse.code==StandardisedResponseCode.SUCCESS?"OK":"ERROR"));
            if(standardisedResponse.code == StandardisedResponseCode.SUCCESS){
                Map<String, String> uploaded_attachment = new HashMap<>();
                uploaded_attachment.put("type","File");
                uploaded_attachment.put("category","File");
                uploaded_attachment.put("name",attachment_name);
//logger.debug("standardisedResponse");
//logger.debug(Utils.object2Json(standardisedResponse));
                uploaded_attachment.put("ID",Utils.json2JsonObject(standardisedResponse.data.toString()).get("ID").getAsString());
                attachments.add(uploaded_attachment);
            }
            else{
                attachmentHasError = true;
                errorMessage.append(String.format("Attachment %d: %s\n",i+1,standardisedResponse.data==null?standardisedResponse.message:standardisedResponse.data.toString()));
            }

        }
        // 1. associate attachments to entity
        if(!attachmentHasError){
            Map<String, Object> entity_payload = new HashMap<>();
            entity_payload.put("entity",Utils.json2JsonObject(payload).getAsJsonObject("entity"));
            entity_payload.put("attachments",attachments);
            standardisedResponse = new StandardisedResponse(pegaSyncingServices.executeRequest(PegaConfig.SyncServiceType.CASES_ATTACHMENTS_LINK, Utils.object2Json(entity_payload)));
        }else
            standardisedResponse = new StandardisedResponse(HttpStatus.BAD_REQUEST,errorMessage.toString());
        return standardisedResponse;
    }

    @Override
    public StandardisedResponse objections_rfidocs_proccessed(String payload) {
        return new StandardisedResponse(pegaSyncingServices.executeRequest(PegaConfig.SyncServiceType.OBJECTION_RFIDOCS_PROCESSED, payload));
    }

}
