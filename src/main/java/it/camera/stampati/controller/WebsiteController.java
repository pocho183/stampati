package it.camera.stampati.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import it.camera.stampati.model.ErrorResponseModel;
import it.camera.stampati.model.ExtractResponseModel;
import it.camera.stampati.model.ResponseModel;
import it.camera.stampati.model.SimpleResponseModel;
import it.camera.stampati.model.StampatiModel;
import it.camera.stampati.service.WebsiteService;

@RestController
public class WebsiteController {

	private static final Logger logger = LoggerFactory.getLogger(WebsiteController.class);
	public static final String NAVETTE_REGEXP = "(?i)[BDFHLNPRTV]";

	@Autowired
	WebsiteService web;

	/* Old method not more used, updated the sign with leg */
	@GetMapping(value="/numeroatto/{atto}")
	@ResponseBody
	public ResponseEntity<ResponseModel> getStampati(@PathVariable("atto") String atto) throws IOException {
		logger.info("Richiesta STAMPATO del numero atto: " + atto + " ad un metodo non più gestito");
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	}
	
	@GetMapping(value="/numeroatto/{leg}/{atto}")
	@ResponseBody
	public ResponseModel getStampati(@PathVariable("leg") Integer leg, @PathVariable("atto") String atto) throws IOException {
		logger.info("Richiesta STAMPATO/I del numero atto: " + atto);
		String elem[]= atto.split("-");
		String stralcio = null;
		Character navette = null;
		for (int i = 1; i < elem.length; i++) {
			if (elem[i].matches(NAVETTE_REGEXP)) {
				navette = elem[i].toCharArray()[0];
			} else {
				stralcio = elem[i];
			}
		}
		atto = elem[0].concat(stralcio != null? "-".concat(stralcio) : "");
		List<StampatiModel> content = web.listStampatiByAttoAndNavette(leg, atto, navette);
		if(content == null) {
			return new ErrorResponseModel("Non ci sono documenti !");
		}
		SimpleResponseModel model = new SimpleResponseModel();
		model.content = content;
		return model;
	}

	/**
	 * Extraction of last four Stampati by data
	 * */
	@GetMapping(value = "/{leg}/extract", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public ResponseEntity<ExtractResponseModel> getLastStampati(@PathVariable("leg") Integer leg) throws Exception {
		logger.info("Request of last four STAMPATI ");
		ExtractResponseModel model = new ExtractResponseModel();
		model.ultimiStampati = web.getContent(leg);
		return new ResponseEntity<ExtractResponseModel>(model, HttpStatus.OK);
	}
	
	/**
	 * Extraction of the Messages
	 * */
	@GetMapping(value="/messaggio/{atto}")
	@ResponseBody
	public ResponseModel getMessaggio(@PathVariable("atto") String atto) throws IOException {		
		logger.info("Richiesta MESSAGGIO del numero atto: " + atto);		
		String elem[]= atto.split("-");
		String stralcio = null;
		Character navette = null;
		for (int i = 1; i < elem.length; i++){
			if (elem[i].matches(NAVETTE_REGEXP))
				navette = elem[i].toCharArray()[0];
			else
				stralcio = elem[i];
		}
		atto = elem[0].concat(stralcio != null? "-".concat(stralcio) : "");
		List<StampatiModel> content = web.listMessaggiByAttoAndNavette(atto, navette);
		if(content == null || content.isEmpty())
			return new ErrorResponseModel("Non ci sono messaggi !");
		SimpleResponseModel model = new SimpleResponseModel();
		model.content = content;
		return model;
	}
	
	/**
	 * Extraction of the Messages with Legislature
	 * */
	@GetMapping(value="/messaggio/{leg}/{atto}")
	@ResponseBody
	public ResponseModel getMessaggioByLeg(@PathVariable("leg") Long leg, @PathVariable("atto") String atto) throws IOException {		
		logger.info("Richiesta MESSAGGIO del numero atto: " + atto);		
		String elem[]= atto.split("-");
		String stralcio = null;
		Character navette = null;
		for (int i = 1; i < elem.length; i++){
			if (elem[i].matches(NAVETTE_REGEXP))
				navette = elem[i].toCharArray()[0];
			else
				stralcio = elem[i];
		}
		atto = elem[0].concat(stralcio != null? "-".concat(stralcio) : "");
		List<StampatiModel> content = web.listMessaggiByLegislaturaAndAttoAndNavette(leg, atto, navette);
		if(content == null || content.isEmpty())
			return new ErrorResponseModel("Non ci sono messaggi !");
		SimpleResponseModel model = new SimpleResponseModel();
		model.content = content;
		return model;
	}

}
