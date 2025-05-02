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

import it.camera.stampati.model.CommissioneModel;
import it.camera.stampati.model.StampatoModel;
import it.camera.stampati.model.TypographyToProcessModel;
import it.camera.stampati.service.FelService;

@RestController
@RequestMapping(path = "efel")
public class FelController {

	private static final Logger logger = LoggerFactory.getLogger(FelController.class);

    @Autowired
    private FelService felService;
    
    @PostMapping(path = "/load")
    public ResponseEntity<?> loadFel(@RequestBody TypographyToProcessModel model) {
    	try {
        	StampatoModel loadModel = felService.load(model);
            return ResponseEntity.status(HttpStatus.OK).body(loadModel);
        } catch (Exception e) {
        	logger.error("Error loading eFel data ", e);
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    @GetMapping("/attiabbinatis/{atto}")
    public ResponseEntity<?> getCommissionsByLeg(@PathVariable("atto") String atto) {
        try {
            String[] attiAbbinati = felService.getAttiAbbinati(atto);  
            return ResponseEntity.ok(attiAbbinati);
        } catch (Exception e) {
        	logger.error("Error loading eFel atti abbinati ", e);
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
}
