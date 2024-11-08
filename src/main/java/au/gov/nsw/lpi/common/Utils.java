package au.gov.nsw.lpi.common;

import au.gov.nsw.lpi.dao.BaseDaoImp;
import com.google.gson.*;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);
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

    public static JsonObject json2JsonObject(String jsonString){
        return JsonParser.parseString(jsonString).getAsJsonObject();
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
    // check if file exists based on the passed in filepath string
    public static boolean fileExists(String file_path) {
        return Paths.get(file_path).toFile().exists();
    }

    public static File getFileIfExists(String file_path) {
        return fileExists(file_path)? Paths.get(file_path).toFile():null;
    }

    // take in a base64 string to create a file and return the file path
    public static String saveBase64File(String base64String, String file_name) {
        try {
            byte[] decodedBytes = java.util.Base64.getDecoder().decode(base64String);
            Path path = Paths.get(file_name);
            java.nio.file.Files.write(path, decodedBytes);
            return path.toString();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    // delete file based on file path
    public static String deleteFile(String file_path) {
        try {
            File file = Paths.get(file_path).toFile();
            if (file.exists()) {
                file.delete();
                return file_path;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    // get file extension from file name
    public static String getFileExtension(String file_name) {
        return file_name.substring(file_name.lastIndexOf("."));
    }

    // urlencode string or return string on error
    public static String urlEncode(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return value;
        }
    }



}
