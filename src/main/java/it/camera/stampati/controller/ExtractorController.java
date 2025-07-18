package it.camera.stampati.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.camera.stampati.enumerator.StampatoFormat;
import it.camera.stampati.model.StampatoModel;
import it.camera.stampati.model.TypographyToProcessModel;
import it.camera.stampati.service.ExtractorService;

@RestController
@RequestMapping(path = "/extractor")
public class ExtractorController {

	private static final Logger logger = LoggerFactory.getLogger(ExtractorController.class);

    @Autowired
    private ExtractorService extractorService;

    @PostMapping(path = "/load")
    public ResponseEntity<StampatoModel> getStampato(@RequestBody TypographyToProcessModel model) {
        try {
            StampatoModel stampato = extractorService.getStampato(model);
            return ResponseEntity.ok(stampato);
        } catch (Exception e) {
        	logger.error("Invalid etract: {}", model.getBarcode());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new StampatoModel());
        }
    }

	@GetMapping(path = "/newtoprocess/{leg}/{format}")
    public ResponseEntity<?> newToProcess(@PathVariable("leg") String leg, @PathVariable("format") String format) {
        logger.debug("Entering StampatoController newToProcess method with leg: {}, format: {}", leg, format);
        try {
            StampatoFormat stampatoFormat = StampatoFormat.fromString(format);
            List<TypographyToProcessModel> stampatiToProcess = extractorService.getStampatiToProcess(leg, stampatoFormat);
            logger.debug("Found {} stampati to process.", stampatiToProcess.size());
            if (stampatiToProcess == null || stampatiToProcess.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
            return new ResponseEntity<>(stampatiToProcess, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error in StampatoController newToProcess: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
