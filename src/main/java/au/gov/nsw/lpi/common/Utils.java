package au.gov.nsw.lpi.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static ResponseEntity<String> getAsResponseEntity(Object o) {

        StandardisedResponse standardisedResponse = new StandardisedResponse(ResponseCode.SUCCESS, "Success", null);
        String raw_response = o.toString();
        Gson gson = new Gson();
        if(raw_response.toLowerCase().contains("error")){
            standardisedResponse.code= ResponseCode.ERROR;
            standardisedResponse.message = raw_response;
        }else{
            standardisedResponse.data = Utils.isValidJson(raw_response)? gson.fromJson(raw_response, Object.class) : raw_response;
        }

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(gson.toJson(standardisedResponse), headers, HttpStatus.OK);
    }

    public static boolean isValidJson(String jsonString) {
        try {
            JsonElement jsonElement = JsonParser.parseString(jsonString);
            return jsonElement.isJsonObject() || jsonElement.isJsonArray();
        } catch (JsonSyntaxException e) {
            return false;
        }
    }
}
