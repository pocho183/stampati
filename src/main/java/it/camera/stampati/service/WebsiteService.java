package it.camera.stampati.service;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import it.camera.stampati.controller.WebsiteController;
import it.camera.stampati.domain.Stampato;
import it.camera.stampati.model.AttoModel;
import it.camera.stampati.model.ExtractModel;
import it.camera.stampati.model.LegislaturaModel;
import it.camera.stampati.model.StampatiModel;
import it.camera.stampati.repository.StampatoRepository;

@Service
public class WebsiteService {

	private static final Logger logger = LoggerFactory.getLogger(WebsiteController.class);

	@Autowired
	private StampatoRepository stampatoRepository;
	@Autowired
	private UtilityService utilityService;
	@Value("${getDocumentUrlPDF}")
	private String StampatiPDF;
	@Value("${efel.atto}")
	private String efel;

	public List<StampatiModel> listStampatiByAttoAndNavette(Integer leg, String atto, Character navette) {

		logger.info("Start get stampati ");
		List<StampatiModel> models = new ArrayList<>();
		List<Stampato> stampati = stampatoRepository.findStampatiByNumeroAttoAndNavette(String.valueOf(leg), atto, navette);

		if (stampati.isEmpty()) {
			return null;
		}

		for (Stampato stampato : stampati) {

			StampatiModel model = new StampatiModel();
			model.setCodiceabarre(stampato.getId().getBarcode());
			model.setNumeroatto(stampato.getNumeroAtto());
			model.setNumeripdl(stampato.getNumeriPDL());
			model.setPagine(stampato.getPagine().toString());
			model.setNavette(stampato.getNavette());
			model.setLettera(stampato.getLettera());
			/** adattamento nuovo modello con il vecchio **/
			boolean relazioneMagg = stampato.getRelazioneMagg();
			Character relazioneMaggChar = relazioneMagg ? 'T' : 'F';
			model.setRelazionemagg(relazioneMaggChar);
			model.setRelazioneminor(stampato.getRelazioneMin());
			model.setSuffisso(stampato.getSuffisso());
			model.setDenominsuffisso(stampato.getDenominazioneStampato());
			//model.setPrestrasm(stampato.getPrestrasm());
			model.setData(stampato.getDataStampa());
			//model.setRelazioneorale(stampato.getRelazioneorale());
			model.setDatastampa(stampato.getDataStampa());
			/** adattamento nuovo modello con il vecchio **/
			boolean errataCorrige = stampato.getErrataCorrige();
			Integer errataCorrigeInteger = errataCorrige ? 1 : 0 ;
			model.setErco(errataCorrigeInteger);
			//model.setBarreerco((stampato.getBarreerco()));
			/** adattamento nuovo modello con il vecchio **/
			String rigoNero = stampato.getRigoNero();
			Integer rigoNeroInteger = rigoNero != null ? 1 : 0 ;
			model.setRigonero(rigoNeroInteger);
			//model.setBarrerigonero((stampato.getRigoNero()));
			model.setDatacancellazione(stampato.getDataDeleted());
			model.setPdfAssente((stampato.getPdfPresente()));
			model.setTimestamp((stampato.getUpdatedAt()));
			//model.setDataTrasferimento((stampato.getDataTrasferimento()));
			model.setDateCreated(stampato.getCreatedAt());
			model.setIdlegislatura(Long.parseLong(stampato.getId().getLegislatura()));
			//model.setCodiceesposizione(stampato.getCodiceesposizione());
			model.setTitolopdl(stampato.getTitolo());
			//model.setDatarichiestaaut(stampato.getDatarichiestaaut());
			//model.setCodicetipoattoallegato((stampato.getCodicetipoattoallegato()));
			//model.setNumvaltab((stampato.getNumvaltab()));
			//model.setCodicevalstralciotab((stampato.getCodicevalstralciotab()));
			//model.setCodicevalparte((stampato.getCodicevalparte()));
			//model.setCodicevalvolume((stampato.getCodicevalvolume()));
			//model.setCodicevaltomo((stampato.getCodicevaltomo()));
			//model.setNumvalannesso((stampato.getNumvalannesso()));
			//model.setDescavalletteraallegato((stampato.getDescavalletteraallegato()));
			//model.setNumvalallegato((stampato.getNumvalallegato()));
			//model.setNumvalresiduo((stampato.getNumvalresiduo()));
			model.setProgressivo(BigInteger.valueOf(stampato.getProgressivo()));
			model.setNomefile((stampato.getNomeFile()));
			model.setRinvio((stampato.getRinvioInCommissione()));
			model.setLinkPDF(MessageFormat.format(StampatiPDF, model.getIdlegislatura(), model.getNomefile()));
			models.add(model);
		}
		return models;
	}
	
	public List<ExtractModel> getContent(Integer leg) throws Exception {
		logger.info("Extraction of last four data ");
		List<Stampato> stampatiDataList = null;
		// Clear cache, è stato inserito affinchè quando cambia il titolo di Fel, 
		// il web service non mostra gli oggetti in cache
		stampatoRepository.clear();
		stampatiDataList = stampatoRepository.findLastFourStampatiData(String.valueOf(leg));
		List<Date> dateList = lastFour(stampatiDataList);
		List<Stampato> stampatiList = listStampati(String.valueOf(leg), dateList);
		logger.info("Getting list of stampati " + leg);
		return getStampato(stampatiList);
	}
	
	/** Select last four distinct data */
	public List<Date> lastFour(List<Stampato> stampatiList) {
		List<Date> date = new ArrayList<Date>();
		List<String> dateString = new ArrayList<String>();
		for (Stampato stampati : stampatiList) {
			String formatted = new SimpleDateFormat("yyyyMMdd").format(stampati.getCreatedAt());
			if (!dateString.contains(formatted)) {
				dateString.add(formatted);
				if (!date.contains(stampati.getCreatedAt()))
					date.add(stampati.getCreatedAt());
			}
			/* Define last data to extract */
			if (dateString.size() >= 4)
				break;
		}
		return date;
	}
	
	public List<Stampato> listStampati(String leg, List<Date> dateList) {		
		List<Stampato> stampatiList = new ArrayList<Stampato>();
		for(Date date : dateList) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DATE, 1);
			List<Stampato> stampati = stampatoRepository.findStampatiData(leg, date, cal.getTime());
			/* Take the first */
			if(!stampati.isEmpty()) {
				for(Stampato stampato : stampati) {
					stampatiList.add(stampato);
				}
			}
		}
		return stampatiList;
	}
	
	/** Extraction of stampati with title from FEL DB */
	public List<ExtractModel> getStampato(List<Stampato> stampatiList) {
		List<ExtractModel> models = new ArrayList<ExtractModel>();	
		if(stampatiList.isEmpty()) 
			return null;	
		for (Stampato stampato : stampatiList) {
			try {
			// Connection to eFEL web service
			logger.info("Extraction of title from eFel web service ");
			RestTemplate restTemplate = new RestTemplate();
			String url = MessageFormat.format(efel, stampato.getId().getLegislatura(), stampato.getNumeroAtto());		
			AttoModel attoModel = restTemplate.getForObject(url, AttoModel.class);
			StringBuffer numeroatto = new StringBuffer();
			ExtractModel model = new ExtractModel();
			model.setNavette(stampato.getNavette() == null ? "" : stampato.getNavette());
			model.setLettera(stampato.getLettera() == null ? "" : stampato.getLettera());		
			if(!isEmpty(stampato.getNumeriPDL())) {
				numeroatto.append(stampato.getNumeriPDL());	
				//if(!isEmpty(model.getNavette()))
					//numeroatto.append("-" + model.getNavette());
				//if(!isEmpty(model.getLettera())) 
					//numeroatto.append("-" + model.getLettera());
				if(!isEmpty(stampato.getSuffisso()))
					if(stampato.getSuffisso().startsWith("/"))
						numeroatto.append(stampato.getSuffisso().trim());
				//if(!isEmpty(stampato.getRelazioneMin()))
					//numeroatto.append("-" + stampato.getRelazioneMin());
				
			model.setNumero(numeroatto.toString());
			model.setLink("idlegislatura=" + stampato.getId().getLegislatura() + "&codice=" + stampato.getId().getBarcode());
			model.setPubDate(stampato.getCreatedAt());
			model.setPubDateRSS(stampato.getCreatedAt());
			if(!isEmpty(stampato.getRigoNero()) && stampato.getErrataCorrige()) {
				if(isEmpty(model.getLettera()) && isEmpty(model.getSuffisso()) && attoModel != null) 
					model.setTitolo(attoModel.getCurrentAnnounceTitle().getDescMTitoloAttoPdl() + "- testo aggiornato (rigo nero) - errata corrige");
				if(!isEmpty(model.getLettera()) && isEmpty(model.getSuffisso())) {
					if(!isEmpty(stampato.getRelazioneMin())) {
						model.setTitolo("Relazione di minoranza - testo aggiornato (rigo nero) - errata corrige");
					}else {
						model.setTitolo("Relazione - testo aggiornato (rigo nero) - errata corrige");
					}
				}
				if(!isEmpty(model.getLettera()) && stampato.getSuffisso().equals("/R")) 
					model.setTitolo("Relazione riformulata - testo aggiornato (rigo nero) - errata corrige");
					} else if (!isEmpty(stampato.getRigoNero())) {
						if (isEmpty(model.getLettera()) && isEmpty(model.getSuffisso()) && attoModel != null)
							model.setTitolo(attoModel.getCurrentAnnounceTitle().getDescMTitoloAttoPdl() + "- testo aggiornato (rigo nero)");
						if (!isEmpty(model.getLettera()) && isEmpty(model.getSuffisso())) {
							if (!isEmpty(stampato.getRelazioneMin())) {
								model.setTitolo("Relazione di minoranza - testo aggiornato (rigo nero)");
							} else {
								model.setTitolo("Relazione - testo aggiornato (rigo nero)");
							}
						}
						if (!isEmpty(model.getLettera()) && stampato.getSuffisso().equals("/R"))
							model.setTitolo("Relazione riformulata - testo aggiornato (rigo nero)");
					} else if (stampato.getErrataCorrige()) {
						if (isEmpty(model.getLettera()) && isEmpty(model.getSuffisso()) && attoModel != null)
							model.setTitolo(attoModel.getCurrentAnnounceTitle().getDescMTitoloAttoPdl() + "- errata corrige");
						if (!isEmpty(model.getLettera()) && isEmpty(model.getSuffisso())) {
							if (!isEmpty(stampato.getRelazioneMin())) {
								model.setTitolo("Relazione di minoranza - errata corrige");
							} else {
								model.setTitolo("Relazione - errata corrige");
							}
						}
						if (!isEmpty(model.getLettera()) && stampato.getSuffisso().equals("/R"))
							model.setTitolo("Relazione riformulata - errata corrige");
					} else {
						if (isEmpty(model.getLettera()) && isEmpty(model.getSuffisso()) && attoModel != null)
							model.setTitolo(attoModel.getCurrentAnnounceTitle().getDescMTitoloAttoPdl());
						if (!isEmpty(model.getLettera()) && isEmpty(model.getSuffisso())) {
							if (!isEmpty(stampato.getRelazioneMin())) {
								model.setTitolo("Relazione di minoranza");
							} else {
								model.setTitolo("Relazione");
							}
						}
						if (!isEmpty(model.getLettera()) && stampato.getSuffisso().equals("/R") || !isEmpty(stampato.getSuffisso()))
							model.setTitolo("Relazione riformulata");
						if (!stampato.getSuffisso().equals("/R") && !isEmpty(stampato.getSuffisso()))
							model.setTitolo(stampato.getSuffisso());
					}
					logger.info("Stampato Added to the List ");
					models.add(model);
				}
			} catch (Exception e) {
				logger.warn("Error occurs while get a stampato: " + e);
			}
		}
		return models;
	}

	public boolean isEmpty(Integer original) {
		if (original == null || original == 0)
			return true;
		else if (original == 1)
			return false;
		else
			return true;
	}

	public boolean isEmpty(String original) {
		if (original == null)
			return true;
		return original.trim().equals("");
	}
	
	public List<StampatiModel> listMessaggiByAttoAndNavette(String atto, Character navette) {		
		logger.info("Start get messaggi ");
		List<StampatiModel> models = new ArrayList<StampatiModel>();
		LegislaturaModel legislature = utilityService.getLastLegislature();
		List<Stampato> stampati = stampatoRepository.findStampatiByNumeroAttoAndNavette(String.valueOf(legislature.getLegArabo()), atto, navette);

		if(stampati.isEmpty()) {	
			return null;			
		}
		
		for(Stampato stampato : stampati) {		
			StampatiModel model = new StampatiModel();
			model.setCodiceabarre(stampato.getId().getBarcode());			
			// Discard Stampati PDL and get only MSG
			if(model.getCodiceabarre().contains("PDL")) {
				System.out.println("It was discarded this barcode: " + model.getCodiceabarre());
				break;
			}
			model.setNumeroatto(stampato.getNumeroAtto());
			model.setNumeripdl(stampato.getNumeriPDL());
			model.setPagine(stampato.getPagine().toString());
			model.setNavette(stampato.getNavette());
			model.setLettera(stampato.getLettera());
			/** adattamento nuovo modello con il vecchio **/
			boolean relazioneMagg = stampato.getRelazioneMagg();
			Character relazioneMaggChar = relazioneMagg ? 'T' : 'F';
			model.setRelazionemagg(relazioneMaggChar);			
			model.setRelazioneminor(stampato.getRelazioneMin());
			model.setSuffisso(stampato.getSuffisso());
			model.setDenominsuffisso(stampato.getDenominazioneStampato());
			//model.setPrestrasm(stampato.getPrestrasm());	
			model.setData(stampato.getDataStampa());
			//model.setRelazioneorale(stampato.getRelazioneorale());
			model.setDatastampa(stampato.getDataStampa());
			/** adattamento nuovo modello con il vecchio **/
			boolean errataCorrige = stampato.getErrataCorrige();
			Integer errataCorrigeInteger = errataCorrige ? 1 : 0 ;
			model.setErco(errataCorrigeInteger);
			//model.setBarreerco((stampato.getBarreerco()));
			/** adattamento nuovo modello con il vecchio **/
			String rigoNero = stampato.getRigoNero();
			Integer rigoNeroInteger = rigoNero != null ? 1 : 0 ;
			model.setRigonero(rigoNeroInteger);
			//model.setBarrerigonero((stampato.getRigoNero()));
			model.setDatacancellazione(stampato.getDataDeleted());
			model.setPdfAssente((stampato.getPdfPresente()));
			model.setTimestamp((stampato.getUpdatedAt()));
			//model.setDataTrasferimento((stampato.getDataTrasferimento()));
			model.setDateCreated(stampato.getCreatedAt());
			model.setIdlegislatura(Long.parseLong(stampato.getId().getLegislatura()));
			//model.setCodiceesposizione(stampato.getCodiceesposizione());
			model.setTitolopdl(stampato.getTitolo());
			//model.setDatarichiestaaut(stampato.getDatarichiestaaut());
			//model.setCodicetipoattoallegato((stampato.getCodicetipoattoallegato()));
			//model.setNumvaltab((stampato.getNumvaltab()));
			//model.setCodicevalstralciotab((stampato.getCodicevalstralciotab()));
			//model.setCodicevalparte((stampato.getCodicevalparte()));
			//model.setCodicevalvolume((stampato.getCodicevalvolume()));
			//model.setCodicevaltomo((stampato.getCodicevaltomo()));
			//model.setNumvalannesso((stampato.getNumvalannesso()));
			//model.setDescavalletteraallegato((stampato.getDescavalletteraallegato()));
			//model.setNumvalallegato((stampato.getNumvalallegato()));
			//model.setNumvalresiduo((stampato.getNumvalresiduo()));
			model.setProgressivo(BigInteger.valueOf(stampato.getProgressivo()));
			model.setNomefile((stampato.getNomeFile()));
			model.setRinvio((stampato.getRinvioInCommissione()));
			model.setLinkPDF(MessageFormat.format(StampatiPDF, model.getIdlegislatura(), model.getNomefile()));
			models.add(model);
		}
		return models;
	}
	
	public List<StampatiModel> listMessaggiByLegislaturaAndAttoAndNavette(Long leg, String atto, Character navette) {
		logger.info("Start get messaggi by leg " + leg);
		List<StampatiModel> models = new ArrayList<StampatiModel>();
		LegislaturaModel legislature = utilityService.getLastLegislature();
		List<Stampato> stampati = stampatoRepository.findStampatiByIdLegislaturaAndNumeroAttoAndNavette(String.valueOf(legislature.getLegArabo()), atto, navette);

		if(stampati.isEmpty()) {	
			return null;			
		}
		
		for(Stampato stampato : stampati) {			
			StampatiModel model = new StampatiModel();
			model.setCodiceabarre(stampato.getId().getBarcode());		
			// Discard Stampati PDL and get only MSG
			if(model.getCodiceabarre().contains("PDL")) {
				System.out.println("It was discarded this barcode: " + model.getCodiceabarre());
				break;
			}
			model.setNumeroatto(stampato.getNumeroAtto());
			model.setNumeripdl(stampato.getNumeriPDL());
			model.setPagine(stampato.getPagine().toString());
			model.setNavette(stampato.getNavette());
			model.setLettera(stampato.getLettera());
			/** adattamento nuovo modello con il vecchio **/
			boolean relazioneMagg = stampato.getRelazioneMagg();
			Character relazioneMaggChar = relazioneMagg ? 'T' : 'F';
			model.setRelazionemagg(relazioneMaggChar);
			model.setRelazioneminor(stampato.getRelazioneMin());
			model.setSuffisso(stampato.getSuffisso());
			model.setDenominsuffisso(stampato.getDenominazioneStampato());
			//model.setPrestrasm(stampato.getPrestrasm());	
			model.setData(stampato.getDataStampa());
			//model.setRelazioneorale(stampato.getRelazioneorale());
			model.setDatastampa(stampato.getDataStampa());
			/** adattamento nuovo modello con il vecchio **/
			boolean errataCorrige = stampato.getErrataCorrige();
			Integer errataCorrigeInteger = errataCorrige ? 1 : 0 ;
			model.setErco(errataCorrigeInteger);
			//model.setBarreerco((stampato.getBarreerco()));
			/** adattamento nuovo modello con il vecchio **/
			String rigoNero = stampato.getRigoNero();
			Integer rigoNeroInteger = rigoNero != null ? 1 : 0 ;
			model.setRigonero(rigoNeroInteger);
			//model.setBarrerigonero((stampato.getRigoNero()));
			model.setDatacancellazione(stampato.getDataDeleted());
			model.setPdfAssente((stampato.getPdfPresente()));
			model.setTimestamp((stampato.getUpdatedAt()));
			//model.setDataTrasferimento((stampato.getDataTrasferimento()));
			model.setDateCreated(stampato.getCreatedAt());
			model.setIdlegislatura(Long.parseLong(stampato.getId().getLegislatura()));
			//model.setCodiceesposizione(stampato.getCodiceesposizione());
			model.setTitolopdl(stampato.getTitolo());
			//model.setDatarichiestaaut(stampato.getDatarichiestaaut());
			//model.setCodicetipoattoallegato((stampato.getCodicetipoattoallegato()));
			//model.setNumvaltab((stampato.getNumvaltab()));
			//model.setCodicevalstralciotab((stampato.getCodicevalstralciotab()));
			//model.setCodicevalparte((stampato.getCodicevalparte()));
			//model.setCodicevalvolume((stampato.getCodicevalvolume()));
			//model.setCodicevaltomo((stampato.getCodicevaltomo()));
			//model.setNumvalannesso((stampato.getNumvalannesso()));
			//model.setDescavalletteraallegato((stampato.getDescavalletteraallegato()));
			//model.setNumvalallegato((stampato.getNumvalallegato()));
			//model.setNumvalresiduo((stampato.getNumvalresiduo()));
			model.setProgressivo(BigInteger.valueOf(stampato.getProgressivo()));
			model.setNomefile((stampato.getNomeFile()));
			model.setRinvio((stampato.getRinvioInCommissione()));
			model.setLinkPDF(MessageFormat.format(StampatiPDF, model.getIdlegislatura(), model.getNomefile()));
			models.add(model);
		}
		return models;
	}

}
