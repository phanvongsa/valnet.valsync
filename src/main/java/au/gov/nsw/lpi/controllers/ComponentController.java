package au.gov.nsw.lpi.controllers;

import au.gov.nsw.lpi.common.Utils;
import au.gov.nsw.lpi.dao.ComponentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/component")
public class ComponentController {
    private static final Logger logger = LoggerFactory.getLogger(ComponentController.class);
    private final ComponentDao componentDao;

    ComponentController(ComponentDao componentDao) {
        this.componentDao = componentDao;
    }

    @RequestMapping(value="/update", method = POST)
    public ResponseEntity<String> update(@RequestBody String requestBody){
        logger.info("Received request: " + requestBody);
        if(!Utils.isValidJson(requestBody))
            return Utils.getAsResponseEntity("Error Invalid JSON");

        return Utils.getAsResponseEntity(componentDao.update(requestBody));
    }

    @RequestMapping(value="/retrieve", method = POST)
    public ResponseEntity<String> retrieve(@RequestBody String requestBody){
        logger.info("Received request: " + requestBody);
        if(!Utils.isValidJson(requestBody))
            return Utils.getAsResponseEntity("Error Invalid JSON");

        return Utils.getAsResponseEntity(componentDao.retrieve(requestBody));
    }


}