package au.gov.nsw.lpi.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.google.gson.Gson;

public class Utils {
    public static ResponseEntity<String> getAsResponseEntity(Object o) {
        final HttpHeaders headers = new HttpHeaders();
        String response_content = o.toString();
        if(o instanceof String) {
            headers.setContentType(MediaType.TEXT_PLAIN);
        } else {
            headers.setContentType(MediaType.APPLICATION_JSON);
            Gson gson = new Gson();
            response_content = gson.toJson(o);
        }

        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(response_content, headers, HttpStatus.OK);
    }

    public static boolean isValidJson(String jsonString) {
        try {
            JsonElement jsonElement = JsonParser.parseString(jsonString);
            return jsonElement.isJsonObject() || jsonElement.isJsonArray();
        } catch (JsonSyntaxException e) {
            return false;
        }
    }
/*
    public static Boolean isJson(Object o) {
        try {
            gson.fromJson(o, Object.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getAsJson(Object o) {
        return gson.toJson(o);
    }
 */
}
