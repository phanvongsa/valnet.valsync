package au.gov.nsw.lpi.common;

import au.gov.nsw.lpi.controllers.ComponentController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class StandardisedResponse {
    public StandardisedResponseCode code;
    public HttpStatus httpStatus;
    public String message;
    public Object data;

    private static final Logger logger = LoggerFactory.getLogger(ComponentController.class);
    public StandardisedResponse(HttpStatus httpStatus, Object data) {
        this.httpStatus = httpStatus;
        this.data = data;
        initialiseCodeMessage();
    }

    private void initialiseCodeMessage(){
        this.code = this.httpStatus==HttpStatus.OK?StandardisedResponseCode.SUCCESS:StandardisedResponseCode.ERROR;
        switch (this.httpStatus){
            case OK:
                this.message = "";
                break;
            case INTERNAL_SERVER_ERROR:
                this.message = "Invalid JSON parameter or Database Error";
                break;
            case UNAUTHORIZED:
                this.message = "Unauthorised Access";
                break;
            case BAD_REQUEST:
                this.message = "Incorrect API URL or Malformed JSON";
                break;
            case FORBIDDEN:
                this.message = "Forbidden Access";
                break;
            default:
                this.message = "Unknown Http Status";
        }
    }

    public ResponseEntity<String> getResponseEntity(){
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(Utils.object2Json(this), headers, this.httpStatus);

    }

    public void setHttpStatus(HttpStatus httpStatus){
        this.httpStatus = httpStatus;
        initialiseCodeMessage();
    }
    public void setData(Object data){
        if(data.toString().toLowerCase().contains("error")){
            this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            initialiseCodeMessage();
        }

        if(data instanceof String && Utils.isValidJson(data.toString())){
            this.data = Utils.json2Object(data.toString());
        }
        else
            this.data = data;
    }
}