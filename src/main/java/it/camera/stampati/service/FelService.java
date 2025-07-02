package it.camera.stampati.service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import it.camera.stampati.model.AttoModel;
import it.camera.stampati.model.StampatoFelModel;
import it.camera.stampati.model.StampatoModel;

@Service
public class FelService {

	private static final Logger logger = LoggerFactory.getLogger(FelService.class);
	
	@Value("${efel.atto}")
	private String efel;

	public StampatoFelModel load(StampatoModel stampato) throws IOException {
		try {
			AttoModel efel = calleFel(stampato.getId().getLegislatura(), stampato.getNumeroAtto());
			logger.info("Got eFel Codice Estremo atto: " + efel.getCodiceestremiattopdl() + " number: " + stampato.getNumeroAtto() + " title: " + efel.getCurrentAnnounceTitle().getDescMTitoloAttoPdl());
			StampatoFelModel model = new StampatoFelModel();	
			model.setCodiceEstremiAttoPdl(efel.getCodiceestremiattopdl());
			model.setNumeroAtto(efel.getNumeroEsteso());
			model.setTitolo(efel.getCurrentPresentationTitle().getDescMTitoloAttoPdl());
			return model;
		} catch(Exception ex) {
			logger.error(ex.getMessage());
			throw new IOException("Errore nel caricamento dei dati eFel: " + stampato.getBarcode());
		}	
    }

	public List<StampatoModel> getAttiAbbinati(String leg, String atto) throws IOException {
		try {
			List<StampatoModel> attiAbbinati = new ArrayList<>();
			AttoModel efel = calleFel(leg, atto);
			if(efel != null) {
				logger.info("Got eFel Codice Estremo atto for abbinamenti: " + efel.getCodiceestremiattopdl() + " number: " + atto);
				efel.getAbbinamenti().forEach(abb -> {
					abb.getPdlabbinati().forEach(pdl -> {
						StampatoModel model = new StampatoModel();
						model.setCodiceEstremiAttoPdl(pdl.getCodiceattopdlabbinato());
						// Da rivedere con raffaele
						model.setNumeroAtto(pdl.getNumeroattoportante());
						model.setNumeriPDL(pdl.getNumeroattoportante());
						attiAbbinati.add(model);
					});
				});
				// Removing itself
				attiAbbinati.remove(0);
				return attiAbbinati;
			} else {
				throw new NoSuchElementException("L'atto: " + atto + " non trovato sul database eFel");
			}
		} catch(Exception ex) {
			logger.error(ex.getMessage());
			throw new IOException("Errore nella ricerca degli atti abbinati eFel: " + atto);		    
		}
	}
	
	public AttoModel calleFel(String leg, String numeroAtto) {
		logger.info("Calling eFel for number act: " + numeroAtto);
		RestTemplate restTemplate = new RestTemplate();
		String url = MessageFormat.format(efel, leg, numeroAtto);		
		return restTemplate.getForObject(url, AttoModel.class);	
	}	

}
