package it.camera.stampati.service;

import java.util.List;
import java.util.Optional;

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
	private RicercaRepository searchRepository;
	
	public List<RicercaModel> searchStampato(String leg, String text) {
        logger.info("Searching stampati for legislatura: {}, text: {}", leg, text);
        List<Stampato> stampati = searchRepository.searchStampati(leg, text.toUpperCase());
        if (stampati.isEmpty())
            logger.warn("No stampati found for legislatura: {}, text: {}", leg, text);
        return beanMapper.map(stampati, Stampato.class, RicercaModel.class);
    }
	
	public Optional<StampatoModel> load(String leg, String barcode) {
		logger.info("Loading stampato for legislatura: {}, barcode: {}", leg, barcode);
		Optional<Stampato> stampato = searchRepository.findByIdLegislaturaOrderByIdBarcodeAsc(leg, barcode);
		if(stampato.isPresent()) {
			StampatoModel model = beanMapper.map(stampato.get(), StampatoModel.class);
            logger.debug("Stampato successfully loaded");
            return Optional.of(model);
		}
		logger.warn("Stampato not found for legislatura: {}, barcode: {}", leg, barcode);
        return Optional.empty();
	}
}
