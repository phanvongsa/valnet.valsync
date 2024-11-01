package au.gov.nsw.lpi.common;

import au.gov.nsw.lpi.controllers.ComponentController;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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

    public StandardisedResponse(HttpResponse httpResponse) {
        this.httpStatus = HttpStatus.valueOf(httpResponse.getStatusLine().getStatusCode());
        String raw_response = getResponseBody(httpResponse);
logger.debug("raw_response: " + raw_response);
        // check the type of response, if json whether its from datasync or pega based response
        if(raw_response!=null && !raw_response.isEmpty() && Utils.isValidJson(raw_response)){
            JsonObject jo = Utils.json2JsonObject(raw_response);
            // check if standardised response {"code":"","message":"","data":"Object"}
            if(jo.has("code") || jo.has("message") || jo.has("data")){
                if(jo.has("code") && !jo.get("code").isJsonNull())
                    this.httpStatus = jo.get("code").toString().contains("ERROR")?HttpStatus.INTERNAL_SERVER_ERROR:HttpStatus.OK;

                if(jo.has("message") && !jo.get("message").isJsonNull())
                    this.message = jo.get("message").getAsString();

                if(jo.has("data") && !jo.get("data").isJsonNull())
                    this.data = jo.get("data").getAsString();
            }else if (jo.has("errorClassification") && jo.has("errorDetails")){
                // standard pega based error response
                this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                StringBuilder errormessages = new StringBuilder();
                jo.getAsJsonArray("errorDetails").forEach(e -> {
                    errormessages.append(e.getAsJsonObject().get("message").getAsString()).append("\n");
                    errormessages.append(e.getAsJsonObject().get("localizedValue").getAsString());
                });
                this.message = errormessages.toString();
                this.data = raw_response;
            } else
                this.data = raw_response;
        } else // return as raw string response
            this.data = raw_response;

        initialiseCodeMessage();
    }

    private void initialiseCodeMessage(){
        this.code = this.httpStatus.value()>=200 && this.httpStatus.value()<300?StandardisedResponseCode.SUCCESS:StandardisedResponseCode.ERROR;
        if(this.message!=null && !this.message.isEmpty())
            return;
        switch (this.httpStatus){
            case CREATED:
                this.message = "Resource Created";
                break;
            case OK:
                this.message = "OK";
                break;
            case INTERNAL_SERVER_ERROR:
                this.message = "Invalid JSON parameter or Database Error";
                break;
            case UNAUTHORIZED:
                this.message = "Unauthorised Access";
                break;
            case BAD_REQUEST:
                this.message = "Incorrect API URL, Malformed JSON or Invalid Payload Data";
                break;
            case FORBIDDEN:
                this.message = "Forbidden Access";
                break;
            case NOT_FOUND:
                this.message = "Resource Not Found";
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

    private String getResponseBody(HttpResponse response){
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder responseBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBody.append(line);
            }
            reader.close();
            return responseBody.toString();
        }catch (Exception ex){
            return ex.getMessage();
        }
    }
}
