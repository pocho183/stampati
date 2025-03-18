package it.camera.stampati.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.camera.stampati.domain.Stampato;
import it.camera.stampati.enumerator.StampatoFormat;
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
    private String sourcePath;
	@Value("${stampati.shared.output}")
    private String publishPath;
	@Value("${pdf.title}")
    private String pdfTitle;
	@Value("${pdf.subject}")
    private String pdfSubject;
	@Value("${pdf.keywords}")
    private String pdfKeywords;
	@Value("${pdf.author}")
    private String pdfAuthor;
	@Value("${img.http.prefix}")
    private String imgPrefix;
	
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
	
	public StampatoModel publish(StampatoModel model) throws IOException {
	    String xhtmlPublishDir = MessageFormat.format(publishPath, model.getId().getLegislatura(), "xhtml");
	    String pdfPublishDir = MessageFormat.format(publishPath, model.getId().getLegislatura(), "pdf");
	    String xhtmlSource = MessageFormat.format(sourcePath, model.getId().getLegislatura(), "xhtml");
	    String pdfSource = MessageFormat.format(sourcePath, model.getId().getLegislatura(), "pdf");
	    File xhtmlFile = new File(xhtmlSource + File.separator + model.getBarcode() + ".html");
	    File pdfFile = new File(pdfSource + File.separator + model.getBarcode() + ".pdf");    
	    File xhtmlDestFile = new File(xhtmlPublishDir + File.separator + model.getNomeFile() + ".html");
	    File pdfDestFile = new File(pdfPublishDir + File.separator + model.getNomeFile() + ".pdf");
	    try {
	        if (xhtmlFile.exists()) {
	            copyFile(xhtmlFile, xhtmlDestFile);
	            parseImageAndSave(xhtmlDestFile, model);
	        } else {
	            logger.error("XHTML file not found: " + xhtmlFile.getAbsolutePath());
	        }
	        if (pdfFile.exists()) {
	            copyFile(pdfFile, pdfDestFile);
	            updatePdfMetadata(pdfDestFile, model);
	        } else {
	            logger.error("PDF file not found: " + pdfFile.getAbsolutePath());
	        }
	        publishAllegato(model, xhtmlSource, pdfSource, xhtmlPublishDir, pdfPublishDir);
	        model.setPubblicato(true);
	        save(model);
	        logger.info("Stampato published successfully");
	    } catch (Exception e) {
	        logger.error("Error publishing stampato: " + model.getBarcode(), e);
	        throw new IOException("Error publishing stampato: " + model.getBarcode(), e);
	    }
	    return model;
	}

	public void copyFile(File sourceFile, File destFile) throws IOException {
	    if (!sourceFile.exists()) {
	        logger.error("The file " + sourceFile.getName() + " is not present!");
	        return;
	    }
	    try (InputStream in = new FileInputStream(sourceFile);
	        OutputStream out = new FileOutputStream(destFile)) {
	        IOUtils.copy(in, out);
	    }
	}
	
	private void parseImageAndSave(File file, StampatoModel model) throws IOException {
	    try {
	    	imgPrefix = MessageFormat.format(imgPrefix, model.getId().getLegislatura());
	        String html = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
	        String barcodePattern = model.getBarcode();
	        int index = model.getNomeFile().indexOf(barcodePattern);
	        String prefix = (imgPrefix != null) ? imgPrefix.concat(model.getNomeFile().substring(0, index)) 
	                                             : model.getNomeFile().substring(0, index);
	        html = html.replaceAll("(img.*?src=\")(" + barcodePattern + ".*?)(\")", "$1" + prefix + "$2$3");
	        Files.write(file.toPath(), html.getBytes(StandardCharsets.UTF_8));
	    } catch (Exception e) {
	        throw new IOException("Error processing XHTML file: " + file.getName(), e);
	    }
	}

    public void updatePdfMetadata(File pdfFile, StampatoModel model) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
        	if (document.isEncrypted())
                document.setAllSecurityToBeRemoved(true);
            PDDocumentInformation info = document.getDocumentInformation();
            HashMap<String, String> properties = new HashMap<>();
            properties.put("Title", MessageFormat.format(pdfTitle, model.getId().getLegislatura()));
            properties.put("Subject", MessageFormat.format(pdfSubject, model.getNumeriPDL()));
            properties.put("Keywords", pdfKeywords);
            properties.put("Author", pdfAuthor);
            info.setTitle(properties.get("Title"));
            info.setSubject(properties.get("Subject"));
            info.setKeywords(properties.get("Keywords"));
            info.setAuthor(properties.get("Author"));
            info.setCreator(properties.get("Author"));
            info.setProducer(properties.get("Author"));
            document.save(pdfFile);
        }
    }

    protected void publishAllegato(StampatoModel model, String xhtmlSource, String pdfSource, String xhtmlPublishDir, String pdfPublishDir) throws IOException {
        copyAllegati(getAllegati(xhtmlSource, model.getBarcode()), xhtmlPublishDir, model);
        copyAllegati(getAllegati(pdfSource, model.getBarcode()), pdfPublishDir, model);
    }

    private void copyAllegati(File[] allegati, String destPath, StampatoModel model) throws IOException {
        if (allegati == null) return;
        for (File allegato : allegati) {
            File destFile = new File(destPath + File.separator + model.getNomeFile() + allegato.getName().replace(model.getBarcode(), ""));
            try (InputStream in = new FileInputStream(allegato); OutputStream out = new FileOutputStream(destFile)) {
                IOUtils.copy(in, out);
            }
        }
    }

    public File[] getAllegati(String source, final String barCode) {
        File dir = new File(source);
        if (!dir.exists() || !dir.isDirectory()) {
            logger.error("Directory does not exist or is not a directory: " + source);
            return new File[0];
        }
        String escapedBarCode = barCode.replaceAll("([.^$*+?{}()|\\[\\]\\\\])", "\\\\$1");
        String regex = ".*" + escapedBarCode + "(?!\\.pdf$)(?!\\.html$).*";
        List<File> matchingFiles = new ArrayList<>();
        try (Stream<Path> paths = Files.list(dir.toPath())) {
            paths.map(Path::toFile)
                 .filter(File::isFile)  // Ignore subdirectories
                 .filter(file -> file.getName().matches(regex))
                 .forEach(matchingFiles::add);  // Collect matching files
        } catch (IOException e) {
            logger.error("Error reading directory: " + source, e);
            return new File[0];
        }
        if (matchingFiles.isEmpty())
            logger.info("No matching files found for barcode: " + barCode);
        return matchingFiles.toArray(new File[0]);
    }

    public StampatoModel unpublish(StampatoModel model) throws FileNotFoundException {
        String xhtmlPublishDir = MessageFormat.format(publishPath, model.getId().getLegislatura(), "xhtml");
        String pdfPublishDir = MessageFormat.format(publishPath, model.getId().getLegislatura(), "pdf");
        deleteFile(xhtmlPublishDir, model.getNomeFile() + ".html");
        deleteFile(pdfPublishDir, model.getNomeFile() + ".pdf");
        deleteAllegati(getAllegati(xhtmlPublishDir, model.getBarcode()));
        deleteAllegati(getAllegati(pdfPublishDir, model.getBarcode()));
        model.setPubblicato(false);
        save(model);
        logger.info("Stampato unpublished successfully");
        return model;
    }

    private void deleteFile(String dir, String filename) throws FileNotFoundException {
        File file = new File(dir + File.separator + filename);
        if (!file.exists()) {
            logger.error("The file " + file.getName() + " is not present!");
            return;
        }
        boolean deleted = file.delete();
        if (deleted) {
            logger.info("Successfully deleted file: " + file.getName());
        } else {
            logger.error("Failed to delete file: " + file.getName());
        }
    }

    private void deleteAllegati(File[] allegati) {
        if (allegati != null) {
            for (File allegato : allegati) {
                boolean deleted = allegato.delete();
                if (deleted)
                    logger.info("Successfully deleted allegato: " + allegato.getName());
                else
                    logger.error("Failed to delete allegato: " + allegato.getName());
            }
        }
    }
    
    public StampatoModel rigonero(StampatoModel model) throws IOException {
        StampatoModel rigonero = new StampatoModel();
        String BarcodeRigonero = extractRigoneroBarcode(model.getId().getBarcode());
        Optional<Stampato> stampatoOpt = stampatiRepository.findByIdLegislaturaAndIdBarcode(model.getId().getLegislatura(), BarcodeRigonero);
        if(!stampatoOpt.isPresent()) {
        	unpublish(model);
        	rigonero= model;
        	rigonero.getId().setBarcode(BarcodeRigonero);
        	rigonero.setRigoNero(model.getId().getBarcode());
        	rigonero.setHtmlPresente(false);
        	rigonero.setPdfPresente(false);
        	rigonero = save(rigonero);
	        logger.info("Rigo nero created successfully");
	        return beanMapper.map(rigonero, StampatoModel.class);
        } else {
        	throw new IOException("Rigo nero already exist !");
        }
    }
    
    private String extractRigoneroBarcode(String barcode) {
    	int i = barcode.length() - 1;
        while (i >= 0 && Character.isDigit(barcode.charAt(i)))
            i--;
        String prefix = barcode.substring(0, i + 1);
        String numberPart = barcode.substring(i + 1);
        int number = Integer.parseInt(numberPart) + 1;
        String newNumberPart = String.format("%0" + numberPart.length() + "d", number);
        return prefix + newNumberPart;
    }
    
    public StampatoModel erratacorrige(StampatoModel model) throws FileNotFoundException {
        

        return model;
    }
}