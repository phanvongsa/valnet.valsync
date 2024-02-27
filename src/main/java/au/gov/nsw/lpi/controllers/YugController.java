package au.gov.nsw.lpi.controllers;

import au.gov.nsw.lpi.common.Utils;
import au.gov.nsw.lpi.domain.AppConfig;
import au.gov.nsw.lpi.domain.ServerConfig;
import au.gov.nsw.lpi.service.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/yug")
public class YugController{

    private static final Logger logger = LoggerFactory.getLogger(YugController.class);

    private final ServerConfig serverConfig;

    private final DataSource dataSource;
    public YugController(ServerConfig serverConfig,DataSource dataSource) {
        this.serverConfig = serverConfig;
        this.dataSource = dataSource;
    }

    @RequestMapping(value="/info", method = POST)
    public ResponseEntity<String> info(@RequestBody String requestBody) {
        logger.info("Received request: " + requestBody);
        if(!requestBody.equals("iL0v3Guy!")) {
            return Utils.getAsResponseEntity("Invalid Request!");
        }

        Map<String, Object> nfo = new HashMap<>();
        nfo.put("Server Environment", this.serverConfig.serverEnvironment);
        nfo.put("Allowed Ips", this.serverConfig.allowedIps);
        nfo.put("Data Source", testDataSources());

        return Utils.getAsResponseEntity(nfo);
    }

    @RequestMapping(value="/retrieve", method = POST)
    public ResponseEntity<String> retrieve(@RequestBody String requestBody) {
        logger.info("Received request: " + requestBody);
        if(!Utils.isValidJson(requestBody))
            return Utils.getAsResponseEntity("Error Invalid JSON");

        return Utils.getAsResponseEntity("AOK");
    }

    private String testDataSources() {
        try {
            this.dataSource.getConnection();
            return "Connection Successful!";
        } catch (Exception e) {
            return "Connection Failed!";
        }
    }
}
