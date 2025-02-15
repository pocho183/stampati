package it.camera.stampati.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import it.camera.stampati.model.CommissioneModel;
import it.camera.stampati.service.UtilityService;
import jakarta.servlet.http.HttpServletResponse;

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
    
    @GetMapping(path = "/legislature")
    public ResponseEntity<List<String>> getLegislature() {
        logger.debug("Entering getLegislature method in UtilityController");
        try {
            List<String> legislature = utilityService.getLegislature();
            if (legislature == null || legislature.isEmpty()) {
                logger.warn("No legislature data found.");
                return ResponseEntity.noContent().build();
            }
            logger.info("Successfully retrieved legislature data: {}", legislature);
            return ResponseEntity.ok(legislature);
        } catch (Exception ex) {
            logger.error("Error occurred while fetching the legislature data", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonList("An error occurred while processing your request."));
        }
    }
    
    @GetMapping("/commissions/{leg}")
    public ResponseEntity<List<CommissioneModel>> getCommissionsByLeg(@PathVariable("leg") Long leg) {
        try {
            List<CommissioneModel> commissions = utilityService.getCommissions(leg);
            if (commissions.isEmpty())
            	return ResponseEntity.status(HttpStatus.NOT_FOUND).build();     
            return ResponseEntity.ok(commissions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping(value = "preview")
    @ResponseBody
    public void preview(@RequestParam(name = "filename") String path, @RequestParam(name = "leg") String leg, 
    		@RequestParam(name = "extension") String extension, HttpServletResponse response) {
    	logger.info("Request preview of: {}", path);
    	try {
    		response.getOutputStream().write(this.utilityService.getPreview(path, leg, extension));
    	 } catch (IOException ex) {
    		 logger.error("I/O Error while fetching the preview of the file: {}", path, ex);
    	     response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    	 } catch (Exception ex) {
    		 logger.error("Unexpected error occurred while fetching the preview of the file: {}", path, ex);
    	     response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    	 }
    }

}
