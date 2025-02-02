package it.camera.stampati.service;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import it.camera.stampati.domain.Stampato;
import it.camera.stampati.enumerator.StampatoFormat;
import it.camera.stampati.model.StampatoModel;
import it.camera.stampati.model.TypographyToProcessModel;
import it.camera.stampati.repository.StampatiRepository;
import it.esinware.mapping.BeanMapper;

public class StampatiService {
	
	@Autowired
	private BeanMapper beanMapper;
	@Autowired
	private StampatiRepository stampatiRepository;
	@Value("${stampati.shared.input}")
    private String sharedPath;
	
	private final String BARCODE_REGEXP = "[1-9][0-9]*(PDL|MSG)[0-9]{7}";
	
	public List<TypographyToProcessModel> getStampatiToProcess(String leg, StampatoFormat format) {
	    List<Stampato> barcodes = stampatiRepository.findByLegislaturaIdAndNotDeleted(leg);
	    List<StampatoModel> barcodeModels = beanMapper.map(barcodes, Stampato.class, StampatoModel.class);
	    Set<String> existingBarcodeIds = barcodeModels.stream().map(StampatoModel::getBarcode).collect(Collectors.toSet());  
	    List<TypographyToProcessModel> stampatiFromShared = getStampatiFromShared(leg, format);
	    return stampatiFromShared.stream().filter(stampato -> !existingBarcodeIds.contains(stampato.getBarcode())).collect(Collectors.toList());
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
        return List.of(files).stream().map(fileName -> new TypographyToProcessModel(fileName, leg, format)).collect(Collectors.toList());
    }
}