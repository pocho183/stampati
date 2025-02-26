package it.camera.stampati.service;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.camera.stampati.domain.Stampato;
import it.camera.stampati.domain.StampatoId;
import it.camera.stampati.enumerator.StampatoFormat;
import it.camera.stampati.model.StampatoIdModel;
import it.camera.stampati.model.StampatoModel;
import it.camera.stampati.model.TypographyToProcessModel;
import it.camera.stampati.repository.StampatiRepository;
import it.esinware.mapping.BeanMapper;

@Service
public class StampatiService {
	
	private static final Logger logger = LoggerFactory.getLogger(StampatiService.class);
	
	@Autowired
	private BeanMapper beanMapper;
	@Autowired
	private UtilityService utlityService;
	@Autowired
	private StampatiRepository stampatiRepository;
	@Value("${stampati.shared.input}")
    private String sharedPath;
	
	private final String BARCODE_REGEXP = "[1-9][0-9]*(PDL|MSG)[0-9]{7}";
	
	public StampatoModel save(StampatoModel model) {
	    String barcode = model.getId().getBarcode();
	    if (barcode == null || !barcode.matches("(" + BARCODE_REGEXP + ")"))
	        throw new IllegalArgumentException("Invalid barcode format");
	    if (model.getId().getLegislatura() == null) {
	        String lastLegislature = utlityService.getLastLegislature().getLegArabo().toString();
	        model.getId().setLegislatura(lastLegislature);
	    }
	    Stampato stampato = beanMapper.map(model, Stampato.class);
	    stampato = stampatiRepository.save(stampato);
	    logger.info("Stampato saved successfully");
        return beanMapper.map(stampato, StampatoModel.class);
	}
	
	public StampatoModel delete(StampatoModel model) {
	    String barcode = model.getId().getBarcode();
	    if (barcode == null || !barcode.matches("(" + BARCODE_REGEXP + ")"))
	        throw new IllegalArgumentException("Invalid barcode format");
	    if (model.getId().getLegislatura() == null)
	        throw new IllegalArgumentException("Legislatura cannot be null");
	    try {	
	        Optional<Stampato> stampatoOpt = stampatiRepository.findByIdLegislaturaAndIdBarcode(model.getId().getLegislatura(), model.getId().getBarcode());
	        if(stampatoOpt.isPresent()) {
	        	stampatoOpt.get().setDataDeleted(new Date());
		        Stampato stampato = stampatiRepository.save(stampatoOpt.get());
		        logger.info("Stampato deleted successfully");
		        return beanMapper.map(stampato, StampatoModel.class);
	        } else {
	        	throw new NoSuchElementException("Stampato not found for the given barcode and legislatura");
	        }
	    } catch (Exception e) {
	        logger.error("Error deleting Stampato: ", e);
	        throw new RuntimeException("Error deleting Stampato", e);
	    }
	}
	
	public StampatoModel restore(StampatoModel model) {
	    String barcode = model.getId().getBarcode();
	    if (barcode == null || !barcode.matches("(" + BARCODE_REGEXP + ")"))
	        throw new IllegalArgumentException("Invalid barcode format");
	    if (model.getId().getLegislatura() == null)
	        throw new IllegalArgumentException("Legislatura cannot be null");
	    try {
	    	Optional<Stampato> stampatoOpt = stampatiRepository.findByIdLegislaturaAndIdBarcode(model.getId().getLegislatura(), model.getId().getBarcode());
	        if(stampatoOpt.isPresent()) {
	        	stampatoOpt.get().setDataDeleted(null);
		        Stampato stampato = stampatiRepository.save(stampatoOpt.get());
		        logger.info("Stampato restored successfully");
		        return beanMapper.map(stampato, StampatoModel.class);
	        } else {
	        	throw new NoSuchElementException("Stampato not found for the given barcode and legislatura");
	        }
	    } catch (Exception e) {
	        logger.error("Error deleting Stampato: ", e);
	        throw new RuntimeException("Error restoring Stampato", e);
	    }
	}
	
	public List<TypographyToProcessModel> getStampatiToProcess(String leg, StampatoFormat format) {
		logger.info("Starting to process stampati for legislatura {} and format {}", leg, format);
	    List<Stampato> barcodes = stampatiRepository.findByLegislaturaAndNotDeleted(leg);
	    logger.debug("Found {} barcodes for legislatura {}", barcodes.size(), leg);
	    List<StampatoModel> barcodeModels = beanMapper.map(barcodes, Stampato.class, StampatoModel.class);
	    List<Stampato> existingBarcodeDataDeleted = stampatiRepository.findByLegislaturaAndDeleted(leg);
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
	    return result;
	}

	
	public List<TypographyToProcessModel> getStampatiFromShared(String leg, StampatoFormat format) {	
		String formattedPath = MessageFormat.format(sharedPath, leg, format);
        File baseDir = new File(formattedPath);   
        if (!baseDir.exists() || !baseDir.isDirectory())
            return new ArrayList<>();
        String regex = "(" + BARCODE_REGEXP + ")\\." + format.name().toLowerCase();     
        String[] files = baseDir.list((dir, name) -> name.matches(regex));
        if (files == null)
            return new ArrayList<>();
        return List.of(files).stream().map(fileName -> {
        	String fileNameWithoutExtension = fileName.replaceAll("\\.[^.]+$", "");
            return new TypographyToProcessModel(fileNameWithoutExtension, leg, format, null); }).collect(Collectors.toList());
    }
}