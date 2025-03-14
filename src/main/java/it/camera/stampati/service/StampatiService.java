package it.camera.stampati.service;

import java.io.File;
import java.text.MessageFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.camera.stampati.domain.Stampato;
import it.camera.stampati.model.StampatoModel;
import it.camera.stampati.repository.StampatoRepository;
import it.esinware.mapping.BeanMapper;

@Service
public class StampatiService {
	
	private static final Logger logger = LoggerFactory.getLogger(StampatiService.class);
	
	@Autowired
	private BeanMapper beanMapper;
	@Autowired
	private UtilityService utlityService;
	@Autowired
	private StampatoRepository stampatiRepository;
	@Value("${stampati.shared.input}")
    private String sharedPath;
	@Value("${stampati.shared.output}")
    private String publishPath;
	
	//private final String BARCODE_REGEXP = "[1-9][0-9]*(PDL|MSG)[0-9]{7}";
	private final String BARCODE_REGEXP = "[1-9][0-9]*(PDL|MSG|TU)[0-9]{4,7}";
	
	public StampatoModel save(StampatoModel model) {
	    String barcode = model.getId().getBarcode();
	    if (barcode == null || !barcode.matches("(" + BARCODE_REGEXP + ")"))
	        throw new IllegalArgumentException("Invalid barcode format");
	    if (model.getId().getLegislatura() == null) {
	        String lastLegislature = utlityService.getLastLegislature().getLegArabo().toString();
	        model.getId().setLegislatura(lastLegislature);
	    }
	    Stampato stampato = beanMapper.map(model, Stampato.class);
	    stampato.setNumeroAtto(model.getNumeriPDL().split("-")[0]);
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
	
	public StampatoModel publish(StampatoModel model) {
		try {
			String pdfPublishDir = MessageFormat.format(publishPath, model.getId().getLegislatura(), "xhtml");
			String xhtmlPublishDir = MessageFormat.format(publishPath, model.getId().getLegislatura(), "pdf");
			
			

	        String fileXHTML = MessageFormat.format(sharedPath, model.getId().getLegislatura(), "xhtml") + "/" + model.getBarcode() + ".html";
	        String filePDF = MessageFormat.format(sharedPath, model.getId().getLegislatura(), "pdf") + "/" + model.getBarcode() + ".pdf";
	        File xhtml = new File(fileXHTML);
	        File pdf = new File(filePDF);
	        if (xhtml.exists()) {
	            System.out.println("File 1 does not exist.");
	        } else {
	            System.out.println("File 2 does not exist.");
	        }
	        if (pdf.exists()) {
	            System.out.println("File 1 does not exist.");
	        } else {
	            System.out.println("File 2 does not exist.");
	        }
			
			//File destdir = getBasedir(stampato.getLeg(), this.stampatiFormat);
			//File source = stampatiProvider.getStampato(getInputFormat(), stampato.getBarCode(), stampato.getLeg().intValue());
			//if (source == null) {
			//	throw new FileNotFoundException("file " + stampato.getBarCode() + "." + getInputFormat().name().toLowerCase() + " non presente");
			//}
			//String extension = FilenameUtils.getExtension(source.getName());
			//String filename = destdir.getAbsolutePath() + File.separator + stampato.getFilename();
			//IOUtils.copy(new FileInputStream(source), new FileOutputStream(filename + "." + extension));
			//publishAllegato(stampato, destdir);
	        return null;
		}
		catch (Exception e) {
			return null;
		}
	}
	
	public StampatoModel unpublish(StampatoModel model) {
		
		return null;
	}
}