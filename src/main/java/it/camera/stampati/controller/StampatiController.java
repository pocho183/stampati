package it.camera.stampati.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.camera.stampati.enumerator.StampatoFormat;
import it.camera.stampati.model.TypographyToProcessModel;
import it.camera.stampati.service.StampatiService;

@RestController
@RequestMapping(path = "/stampato")
public class StampatiController {
	
	private static final Logger logger = LoggerFactory.getLogger(StampatiController.class);

    @Autowired
    private StampatiService stampatiService;

    @GetMapping(path = "/newtoprocess/{leg}/{format}")
    public ResponseEntity<List<TypographyToProcessModel>> newToProcess(@PathVariable("leg") String leg, @PathVariable("format") String format) {
        logger.debug("Entering StampatoController newToProcess method with leg: {}, format: {}", leg, format);
        try {
            StampatoFormat stampatoFormat = StampatoFormat.fromString(format);
            List<TypographyToProcessModel> stampatiToProcess = stampatiService.getStampatiToProcess(leg, stampatoFormat);
            logger.debug("Found {} stampati to process.", stampatiToProcess.size());
            if (stampatiToProcess == null || stampatiToProcess.isEmpty())
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            return new ResponseEntity<>(stampatiToProcess, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid format: {}", format);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error in StampatoController newToProcess: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

	
