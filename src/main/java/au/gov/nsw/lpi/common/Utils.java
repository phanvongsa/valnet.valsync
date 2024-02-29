package au.gov.nsw.lpi.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;

public class Utils {
    public static ResponseEntity<String> getAsResponseEntity(Object o) {
        StandardisedResponse standardisedResponse;
        Gson gson = new Gson();
        String raw_response = o.toString();
        if(raw_response.toLowerCase().contains("error"))
            standardisedResponse = new StandardisedResponse(HttpStatus.INTERNAL_SERVER_ERROR,raw_response);
        else
            standardisedResponse = new StandardisedResponse(HttpStatus.OK,Utils.isValidJson(raw_response)? gson.fromJson(raw_response, Object.class) : raw_response);

        return standardisedResponse.getResponseEntity();
    }

    public static boolean isValidJson(String jsonString) {
        try {
            JsonElement jsonElement = JsonParser.parseString(jsonString);
            return jsonElement.isJsonObject() || jsonElement.isJsonArray();
        } catch (JsonSyntaxException e) {
            return false;
        }
    }

    public static Object json2Object(String jsonString){
        Gson gson = new Gson();
        return gson.fromJson(jsonString,Object.class);
    }

    public static String object2Json(Object object){
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public static String getRequestRemoteAddress(HttpServletRequest request) {
        String remoteAddress = request.getHeader("X-FORWARDED-FOR");
        if (remoteAddress == null || "".equals(remoteAddress)) {
            remoteAddress = request.getRemoteAddr();
        }
        return remoteAddress;
    }

    public static boolean isInCommaDelimitedList(String commaDelimitedList, String targetString) {
        // Split the comma-delimited list
        String[] items = commaDelimitedList.split(",");

        // Search for the target string in the array
        for (String item : items) {
            if (item.trim().equals(targetString)) {
                return true; // String found
            }
        }
        return false; // String not found
    }

}
