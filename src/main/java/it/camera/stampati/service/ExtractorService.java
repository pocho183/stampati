package it.camera.stampati.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.camera.stampati.domain.Stampato;
import it.camera.stampati.enumerator.StampatoFormat;
import it.camera.stampati.model.RicercaModel;
import it.camera.stampati.model.StampatoModel;
import it.camera.stampati.model.TypographyToProcessModel;
import it.camera.stampati.parser.PDFParser;
import it.camera.stampati.parser.XhtmlParser;
import it.camera.stampati.repository.RicercaRepository;
import it.camera.stampati.repository.StampatoRepository;
import it.esinware.mapping.BeanMapper;

@Service
public class ExtractorService {

private static final Logger logger = LoggerFactory.getLogger(ExtractorService.class);
	
	@Autowired
	private BeanMapper beanMapper;
	@Autowired
	private RicercaRepository ricercaRepository;
	@Autowired
	private StampatoRepository stampatoRepository;
	@Value("${stampati.shared.input}")
    private String sharedPath;
	
	private final String BARCODE_REGEXP = "[1-9][0-9]*(PDL|MSG|TU)[0-9]{4,7}";
	
	public StampatoModel getStampato(TypographyToProcessModel model) throws FileNotFoundException {
        File file = loadFile(model);
        StampatoModel stampato = parseContent(file, model);
        validate(stampato);
        updateNomeFrontespizio(stampato);
        updateNomeFile(stampato);
        stampato = save(stampato);
        return stampato;
    }
	
	public File loadFile(TypographyToProcessModel model) throws FileNotFoundException {
        String formattedPath = MessageFormat.format(sharedPath, model.getLegislaturaId(), model.getFormat().name().toLowerCase());
        File baseDir = new File(formattedPath);
        if (!baseDir.exists() || !baseDir.isDirectory())
            throw new IllegalStateException("Directory does not exist or is not valid: " + formattedPath);
        String regex = Pattern.quote(model.getBarcode()) + "\\." + model.getFormat().name().toLowerCase().replace("x", "");
        String[] files = baseDir.list((dir, name) -> name.matches(regex));
        if (files == null || files.length == 0)
            throw new FileNotFoundException("No matching file found for barcode: " + model.getBarcode());
        return new File(baseDir, files[0]);
    }
	
	public StampatoModel parseContent(File file, TypographyToProcessModel model) {
		if(model.getFormat() == StampatoFormat.XHTML) {
        	XhtmlParser xhtmlParser = new XhtmlParser();
            return xhtmlParser.parse(file);
        } else if(model.getFormat() == StampatoFormat.PDF) {
            PDFParser pdfParser = new PDFParser();
            return pdfParser.parse(model);
        } else {
        	logger.error("Unsupported content type: {}", model.getFormat());
            throw new IllegalArgumentException("Unsupported format: " + model.getFormat());
        }
    }
	
	public void validate(StampatoModel model) {
		Optional<Stampato> stampato = ricercaRepository.findByIdLegislaturaAndIdBarcode(model.getId().getLegislatura(), model.getId().getBarcode());
		if(stampato.isPresent()) {
			throw new IllegalStateException("Stampato with barcode " + model.getId().getBarcode() + " already exists.");
		}
	}
	
	public void updateNomeFrontespizio(StampatoModel stampato) {
	    //String lettera = trimValue(stampato.getLettera());
	    //String minoranza = trimValue(stampato.getRelazioneMin());
	    //String suffisso = trimValue(stampato.getSuffisso());
	    //String navette = trimValue(stampato.getNavette());
	    String numeriPDL = trimValue(stampato.getNumeriPDL());
	    numeriPDL = numeriPDL.replaceAll("\\b([A-Z])R\\b", "$1/R");
	    //String rinvio = Boolean.TRUE.equals(stampato.getRinvioInCommissione()) ? "/R" : "";
	    //String parts = Arrays.asList(lettera, navette).stream().filter(part -> !part.isEmpty()).collect(Collectors.joining("-"));
        //String rest = Arrays.asList(minoranza, suffisso).stream().filter(part -> !part.isEmpty()).collect(Collectors.joining("-"));
        //String finalPart = (!parts.isEmpty() ? parts : "") + (!rinvio.isEmpty() ? rinvio : "") +
          //      (!rest.isEmpty() ? (!parts.isEmpty() || !rinvio.isEmpty() ? "-" + rest : rest) : "");
        //String nomeFrontespizio = numeriPDL + (!finalPart.isEmpty() ? "_" + finalPart : "");
        stampato.setNomeFrontespizio(numeriPDL);
	}

	private String trimValue(String value) {
	    return Optional.ofNullable(value).map(String::trim).orElse("");
	}
	
	public void updateNomeFile(StampatoModel stampato) {
		if (stampato != null && stampato.getId() != null && stampato.getId().getBarcode() != null) {
            String type = extractTypeStampato(stampato.getId().getBarcode());
            String numeriPDL = (stampato.getNumeriPDL() != null && !stampato.getNumeriPDL().isEmpty()) ? stampato.getNumeriPDL().split("-")[0] : "unknown";
            String filename = "leg." + stampato.getId().getLegislatura() + "." + (type != null ? type : "") + ".camera." + numeriPDL;
            if(stampato.getRelazioneMin() != null && !stampato.getRelazioneMin().trim().isEmpty())
                filename += "-" + stampato.getRelazioneMin();
            if(stampato.getNavette() != null && !stampato.getNavette().trim().isEmpty())
                filename += "-" + stampato.getNavette();
            String relazione = (stampato.getLettera() != null) ? stampato.getLettera() : "";
            if(stampato.getRinvioInCommissione() != null && stampato.getRinvioInCommissione())
                relazione += "R";
            if (!relazione.trim().isEmpty())
                filename = filename + "_" + relazione;
            filename = filename + "." + stampato.getId().getBarcode();
            stampato.setNomeFile(filename);
        }
	}
	
	public static String extractTypeStampato(String input) {
	    Pattern pattern = Pattern.compile("(PDL|MSG|TU)");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find())
            return matcher.group(1).toLowerCase();
        return null;
	}


	private StampatoModel save(StampatoModel model) {
        Stampato entity = beanMapper.map(model, Stampato.class);
        entity = stampatoRepository.save(entity);
        return beanMapper.map(entity, StampatoModel.class);
    }	

	public List<TypographyToProcessModel> getStampatiToProcess(String leg, StampatoFormat format) {
		logger.info("Starting to process stampati for legislatura {} and format {}", leg, format);
	    List<Stampato> barcodes = stampatoRepository.findByLegislaturaAndNotDeleted(leg);
	    logger.debug("Found {} barcodes for legislatura {}", barcodes.size(), leg);
	    List<StampatoModel> barcodeModels = beanMapper.map(barcodes, Stampato.class, StampatoModel.class);
	    List<Stampato> existingBarcodeDataDeleted = stampatoRepository.findByLegislaturaAndDeleted(leg);
	    List<StampatoModel> existingBarcodeDataDeletedModels = beanMapper.map(existingBarcodeDataDeleted, Stampato.class, StampatoModel.class);
	    Map<String, Date> deletedBarcodeDataDeletedMap = existingBarcodeDataDeletedModels.stream().collect(Collectors.toMap(StampatoModel::getBarcode, StampatoModel::getDataDeleted));  
	    Set<String> existingBarcodeIds = barcodeModels.stream().map(st -> st.getBarcode()).collect(Collectors.toSet());  
	    List<TypographyToProcessModel> stampatiFromShared = getStampatiFromShared(leg, format);
	    logger.debug("Found {} stampati from shared path", stampatiFromShared.size());    
	    List<TypographyToProcessModel> result = stampatiFromShared.stream()
	  		.filter(stampato -> !existingBarcodeIds.contains(stampato.getBarcode()))
	  		.map(stampato -> {
	  			Date dataDeleted = deletedBarcodeDataDeletedMap.get(stampato.getBarcode());
	            TypographyToProcessModel model = new TypographyToProcessModel(stampato.getBarcode(), leg, format, dataDeleted);
	            return model; }).collect(Collectors.toList());
	    logger.info("Processed {} stampati to process", result.size());
	    result.sort(Comparator.comparing(TypographyToProcessModel::getBarcode));
	    return result;
	}

	public List<TypographyToProcessModel> getStampatiFromShared(String leg, StampatoFormat format) {	
		String formattedPath = MessageFormat.format(sharedPath, leg, format);
        File baseDir = new File(formattedPath);   
        if (!baseDir.exists() || !baseDir.isDirectory())
            return new ArrayList<>();
        String regex = "(" + BARCODE_REGEXP + ")\\." + format.name().toLowerCase().replace("x", "");     
        String[] files = baseDir.list((dir, name) -> name.matches(regex));
        if (files == null)
            return new ArrayList<>();
        return List.of(files).stream().map(fileName -> {
        	String fileNameWithoutExtension = fileName.replaceAll("\\.[^.]+$", "");
            return new TypographyToProcessModel(fileNameWithoutExtension, leg, format, null); }).collect(Collectors.toList());
    }
}

