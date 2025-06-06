package it.camera.stampati.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import it.camera.stampati.model.StampatoModel;
import it.camera.stampati.model.TypographyToProcessModel;

@Service
public class FelService {

	private static final Logger logger = LoggerFactory.getLogger(FelService.class);
	
	public StampatoModel load(TypographyToProcessModel model) throws IOException {
		throw new IOException("Errore nel caricamento dei dati eFel: " + model.getBarcode());
    }
	
	public String[] getAttiAbbinati(String atto) throws IOException {
		throw new IOException("Errore nella ricerca degli atti abbinati eFel: " + atto);
    }

}
