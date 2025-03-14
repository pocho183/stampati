package it.camera.stampati.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.camera.stampati.model.StampatoModel;
import it.camera.stampati.service.StampatiService;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/stampato")
public class StampatoController {
	
	private static final Logger logger = LoggerFactory.getLogger(StampatoController.class);

    @Autowired
    private StampatiService stampatiService;
    
    @PostMapping(path = "save")
    public ResponseEntity<?> save(@Valid @RequestBody StampatoModel model) {
        try {
            StampatoModel savedModel = stampatiService.save(model);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedModel);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid data: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping(path = "delete")
    public ResponseEntity<?> delete(@Valid @RequestBody StampatoModel model) {
        try {
            StampatoModel deletedModel = stampatiService.delete(model);
            return ResponseEntity.status(HttpStatus.OK).body(deletedModel);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid data: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
    
    @PostMapping(path = "restore")
    public ResponseEntity<?> restore(@Valid @RequestBody StampatoModel model) {
        try {
            StampatoModel restoredModel = stampatiService.restore(model);
            return ResponseEntity.status(HttpStatus.OK).body(restoredModel);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid data: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
    
    @PostMapping(path = "publish")
    public ResponseEntity<?> publish(@RequestBody StampatoModel model) {
        try {
            StampatoModel savedModel = stampatiService.publish(model);
            return ResponseEntity.status(HttpStatus.OK).body(savedModel);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid data: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
    
    @PostMapping(path = "unpublish")
    public ResponseEntity<?> unpublish(@RequestBody StampatoModel model) {
        try {
            StampatoModel savedModel = stampatiService.unpublish(model);
            return ResponseEntity.status(HttpStatus.OK).body(savedModel);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid data: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
}

	
