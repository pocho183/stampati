package it.camera.stampati.controller;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.camera.stampati.model.RicercaModel;
import it.camera.stampati.model.StampatoModel;
import it.camera.stampati.service.RicercaService;

@RestController
@RequestMapping(path = "/search")
public class RicercaController {

	private static final Logger logger = LoggerFactory.getLogger(RicercaController.class);

    @Autowired
    private RicercaService searchService;

    @GetMapping("stampato")
    public Collection<RicercaModel> searchStampato(@RequestParam(name = "leg") String leg, @RequestParam(name = "text") String text) {
    	return searchService.searchStampato(leg, text);
    }

    @GetMapping(path = "/stampato/{leg}/{barcode}")
    public ResponseEntity<StampatoModel> load(@PathVariable("leg") String leg, @PathVariable("barcode") String barcode) {
        logger.debug("Entering RicercaController load method with leg: {}, barcode: {}", leg, barcode);
        try {
        	Optional<StampatoModel> model = searchService.load(leg, barcode);
        	if(model.isPresent()) {
        		return new ResponseEntity<>(model.get(), HttpStatus.OK);
        	} else {
        		logger.warn("Stampato not found for legislatura: {}, barcode: {}", leg, barcode);
        		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        	}
        } catch (Exception e) {
            logger.error("Error in RicercaController load: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(path = "saveorder")
    public ResponseEntity<?> saveOrder(@RequestBody List<RicercaModel> models) {
    	try {
    		List<RicercaModel> results = searchService.saveOrder(models);
    		return new ResponseEntity<>(results, HttpStatus.OK);
        } catch (Exception e) {
        	logger.error("Error in RicercaController saveOrder: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
