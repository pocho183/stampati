package it.camera.stampati.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.camera.stampati.domain.Stampato;
import it.camera.stampati.model.RicercaModel;
import it.camera.stampati.model.StampatoModel;
import it.camera.stampati.repository.RicercaRepository;
import it.esinware.mapping.BeanMapper;

@Service
public class RicercaService {

private static final Logger logger = LoggerFactory.getLogger(RicercaService.class);
	
	@Autowired
	private BeanMapper beanMapper;
	@Autowired
	private RicercaRepository ricercaRepository;
	@Autowired
	private StampatiService stampatoService;
	
	public static final List<String> STRALCIO = List.of(
		"bis", "ter", "quater", "quinquies", "sexies", "septies", "octies", "novies", "decies",
	    "undecies", "duodecies", "terdecies", "quaterdecies", "quindecies", "sedecies", 
        "septiesdecies", "duodevicies", "undevicies", "vicies", "semeletvicies", "bisetvicies",
	    "teretvicies", "quateretvicies", "quinquiesetvicies", "sexiesetvicies", "septiesetvicies",
	    "octiesetvicies", "noviesetvicies", "tricies", "semelettricies", "bisettricies", "terettricies",
	    "quaterettricies", "quinquiesettricies", "sexiesettricies", "septiesettricies", "octiesettricies",
	    "noviesettricies", "quadragies", "semeletquadragies", "bisetquadragies", "teretquadragies",
	    "quateretquadragies", "quinquiesetquadragies", "sexiesetquadragies", "septiesetquadragies",
	    "octiesetquadragies", "noviesetquadragies", "quinquagies"
	);
	
	public Collection<RicercaModel> searchStampato(String leg, String text) {
        logger.info("Searching stampati for legislatura: {}, text: {}", leg, text);
        // Normalize text like "643 bis" to "643-bis"
        text = normalizeRelMinoranzaFormat(text);
        List<Stampato> stampati = ricercaRepository.searchStampati(leg, text);
        if (stampati.isEmpty())
            logger.warn("No stampati found for legislatura: {}, text: {}", leg, text);
        Collection<RicercaModel> result = beanMapper.map(stampati, RicercaModel.class);
        Collections.sort((List<RicercaModel>)result, Comparator.comparing(RicercaModel::getBarcode));
        return result;
    }
	
	public Optional<StampatoModel> load(String leg, String barcode) {
		logger.info("Loading stampato for legislatura: {}, barcode: {}", leg, barcode);
		Optional<Stampato> stampato = ricercaRepository.findByIdLegislaturaAndIdBarcode(leg, barcode);
		if(stampato.isPresent()) {
			StampatoModel model = beanMapper.map(stampato.get(), StampatoModel.class);
            logger.debug("Stampato successfully loaded");
            return Optional.of(model);
		}
		logger.warn("Stampato not found for legislatura: {}, barcode: {}", leg, barcode);
        return Optional.empty();
	}
	
	public List<RicercaModel> saveOrder(List<RicercaModel> models) throws IOException {
		for(RicercaModel result: models) {
			Optional<StampatoModel> stampatoOpt = load(result.getLegislatura(), result.getBarcode());
			if(stampatoOpt.isPresent()) {
				StampatoModel stampato = stampatoOpt.get();
				stampato.setProgressivo(result.getProgressivo());
				stampatoService.save(stampato);
			}
		}
		logger.info("Stampato order saved successfully");
		models.sort(Comparator
                .comparing(RicercaModel::getProgressivo, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(RicercaModel::getBarcode, Comparator.nullsLast(Comparator.naturalOrder()))); 
		return models;
	}
	
	private String normalizeRelMinoranzaFormat(String text) {
	    for (String stralcio : STRALCIO) {
	        String pattern = "(\\d+)\\s+" + stralcio;
	        if (text.toLowerCase().matches(pattern.toLowerCase())) {
	            return text.replaceAll("(?i)(\\d+)\\s+" + stralcio, "$1-" + stralcio);
	        }
	    }
	    return text;
	}

}
