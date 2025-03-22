package it.camera.stampati.service;

import java.util.Comparator;
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
	private RicercaRepository ricercaRepository;
	@Autowired
	private StampatiService stampatoService;
	
	
	public List<RicercaModel> searchStampato(String leg, String text) {
        logger.info("Searching stampati for legislatura: {}, text: {}", leg, text);
        List<Stampato> stampati = ricercaRepository.searchStampati(leg, text);
        if (stampati.isEmpty())
            logger.warn("No stampati found for legislatura: {}, text: {}", leg, text);
        List<RicercaModel> result = beanMapper.map(stampati, Stampato.class, RicercaModel.class);
        //result.sort(Comparator.comparing(RicercaModel::getBarcode));
        result.sort(Comparator
                .comparing(RicercaModel::getProgressivo, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(RicercaModel::getBarcode, Comparator.nullsLast(Comparator.naturalOrder())));
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
	
	public List<RicercaModel> saveOrder(List<RicercaModel> models) {
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
}
