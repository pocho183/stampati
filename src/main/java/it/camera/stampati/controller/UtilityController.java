package it.camera.stampati.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.camera.stampati.service.UtilityService;

@RestController
@RequestMapping(path = "/utility")
public class UtilityController {

	private static final Logger logger = LoggerFactory.getLogger(UtilityController.class);

    @Autowired
    private UtilityService utilityService;
    
    @GetMapping(path = "/legislature/last")
    public ResponseEntity<String> getLastLegislature() {
        logger.debug("Entering UtilityController to process getLastLegislature");
        try {
            String legislature = utilityService.getLastLegislature();
            if (legislature == null || legislature.isEmpty()) {
                logger.warn("No legislature data found.");
                return ResponseEntity.noContent().build();
            }
            logger.info("Successfully retrieved the latest legislature: {}", legislature);
            return ResponseEntity.ok(legislature);
        } catch (Exception ex) {
            logger.error("Error occurred while fetching the latest legislature", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request.");
        }
    }

}
