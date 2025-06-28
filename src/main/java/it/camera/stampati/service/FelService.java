package it.camera.stampati.service;

import java.io.IOException;
import java.text.MessageFormat;

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
			AttoModel efel = calleFel(stampato);
			logger.info("Got eFel Codice Estremo atto: " + efel.getCodiceestremiattopdl() + " number: " + stampato.getNumeroAtto() + " title: efel.getCurrentAnnounceTitle().getDescMTitoloAttoPdl()");
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

	public String[] getAttiAbbinati(String atto) throws IOException {
		throw new IOException("Errore nella ricerca degli atti abbinati eFel: " + atto);
    }
	
	public AttoModel calleFel(StampatoModel stampato) {
		logger.info("Calling eFel for number act: " + stampato.getNumeroAtto());
		RestTemplate restTemplate = new RestTemplate();
		String url = MessageFormat.format(efel, stampato.getId().getLegislatura(), stampato.getNumeroAtto());		
		return restTemplate.getForObject(url, AttoModel.class);	
	}	

}
