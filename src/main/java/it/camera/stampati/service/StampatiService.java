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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.camera.stampati.domain.Stampato;
import it.camera.stampati.model.StampatoFelModel;
import it.camera.stampati.model.StampatoModel;
import it.camera.stampati.model.StampatoRelatoreModel;
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
	private ExtractorService extractorService;
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

	private final String BARCODE_REGEXP = "[1-9][0-9]*(PDL|MSG|TU)[0-9]{4,7}";

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

	public StampatoModel save(StampatoModel model) throws IOException {
		try {
			String barcode = model.getId().getBarcode();
		    if (barcode == null || !barcode.matches("(" + BARCODE_REGEXP + ")")) {
				throw new IllegalArgumentException("Il codice a barre non è scritto correttamente");
			}
		    if (model.getId().getLegislatura() == null) {
		        String lastLegislature = utlityService.getLastLegislature().getLegArabo().toString();
		        model.getId().setLegislatura(lastLegislature);
		    }
		    Stampato stampato = beanMapper.map(model, Stampato.class);
		    String stralcio = getStralcio(model.getNumeriPDL());
	        stampato.setNumeroAtto(stralcio != null ? model.getNumeriPDL().split("-")[0] + "-" + stralcio : model.getNumeriPDL().split("-")[0]);
		    stampato = stampatiRepository.save(stampato);
		    logger.info("Stampato saved successfully");
	        return beanMapper.map(stampato, StampatoModel.class);
		} catch (Exception e) {
	        logger.error("Error saving Stampato: ", e);
	        throw new IOException("Errore nel salvataggio dello Stampato " + model.getId().getBarcode());
	    }
	}

	public StampatoModel delete(StampatoModel model) throws IOException {
	    String barcode = model.getId().getBarcode();
	    if (barcode == null || !barcode.matches("(" + BARCODE_REGEXP + ")")) {
			throw new IllegalArgumentException("Il codice a barre non è scritto correttamente");
		}
	    if (model.getId().getLegislatura() == null) {
			throw new IllegalArgumentException("La legislatura non può essere vuota");
		}
	    try {
	        Optional<Stampato> stampatoOpt = stampatiRepository.findByIdLegislaturaAndIdBarcode(model.getId().getLegislatura(), model.getId().getBarcode());
	        if(stampatoOpt.isPresent()) {
	        	stampatoOpt.get().setDataDeleted(new Date());
		        Stampato stampato = stampatiRepository.save(stampatoOpt.get());
		        logger.info("Stampato deleted successfully");
		        return beanMapper.map(stampato, StampatoModel.class);
	        } else {
	        	throw new NoSuchElementException("Stampato non trovato");
	        }
	    } catch (Exception e) {
	        logger.error("Error deleting Stampato: ", e);
	        throw new IOException("Errore nella canecellazione dello Stampato " + barcode);
	    }
	}

	public StampatoModel restore(StampatoModel model) throws IOException {
	    String barcode = model.getId().getBarcode();
	    if (barcode == null || !barcode.matches("(" + BARCODE_REGEXP + ")")) {
			throw new IllegalArgumentException("Il codice a barre non è scritto correttamente");
		}
	    if (model.getId().getLegislatura() == null) {
			throw new IllegalArgumentException("La legislatura non può essere vuota");
		}
	    try {
	    	Optional<Stampato> stampatoOpt = stampatiRepository.findByIdLegislaturaAndIdBarcode(model.getId().getLegislatura(), model.getId().getBarcode());
	        if(stampatoOpt.isPresent()) {
	        	stampatoOpt.get().setDataDeleted(null);
		        Stampato stampato = stampatiRepository.save(stampatoOpt.get());
		        logger.info("Stampato restored successfully");
		        return beanMapper.map(stampato, StampatoModel.class);
	        } else {
	        	throw new NoSuchElementException("Stampato non trovato per il dato barcode " + barcode);
	        }
	    } catch (Exception e) {
	    	logger.error("Error restoring  stampato", e);
	        throw new IOException("Errore nel ripristino dello Stampato " + barcode);
	    }
	}

	public StampatoModel publish(StampatoModel model) throws IOException {
		if(model.getPdfPresente() != null && model.getPdfPresente()) {
			String pdfPublishDir = MessageFormat.format(publishPath, model.getId().getLegislatura(), "pdf");
			String pdfSource = MessageFormat.format(sourcePath, model.getId().getLegislatura(), "pdf");
			File pdfFile = new File(pdfSource + File.separator + model.getBarcode() + ".pdf");
			File pdfDestFile = new File(pdfPublishDir + File.separator + model.getNomeFile() + ".pdf");
			if (pdfFile.exists()) {
				copyFile(pdfFile, pdfDestFile);
		        updatePdfMetadata(pdfDestFile, model);
		        publishAllegato(model, pdfSource, pdfPublishDir);
		        model.setPubblicato(true);
		    } else {
		    	logger.error("PDF file not found: " + pdfFile.getAbsolutePath());
		        throw new IOException("Errore nella pubblicazione del PDF: " + model.getBarcode());
		    }
		}
		if(model.getHtmlPresente() != null && model.getHtmlPresente()) {
			String xhtmlPublishDir = MessageFormat.format(publishPath, model.getId().getLegislatura(), "xhtml");
			String xhtmlSource = MessageFormat.format(sourcePath, model.getId().getLegislatura(), "xhtml");
			File xhtmlFile = new File(xhtmlSource + File.separator + model.getBarcode() + ".html");
			File xhtmlDestFile = new File(xhtmlPublishDir + File.separator + model.getNomeFile() + ".html");
			if (xhtmlFile.exists()) {
				copyFile(xhtmlFile, xhtmlDestFile);
		        parseImageAndSave(xhtmlDestFile, model);
		        publishAllegato(model, xhtmlSource, xhtmlPublishDir);
		        model.setPubblicato(true);
		    } else {
		        logger.error("XHTML file not found: " + xhtmlFile.getAbsolutePath());
		        throw new IOException("Errore nella publicazione XHTML: " + model.getBarcode());
		    }
		}
	    save(model);
	    logger.info("Stampato published successfully");
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
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
        	if (document.isEncrypted()) {
				document.setAllSecurityToBeRemoved(true);
			}
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

    protected void publishAllegato(StampatoModel model, String source, String publishDir) throws IOException {
        copyAllegati(getAllegati(source, model.getBarcode()), publishDir, model);
    }

    private void copyAllegati(File[] allegati, String destPath, StampatoModel model) throws IOException {
        if (allegati == null) {
			return;
		}
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
        if (matchingFiles.isEmpty()) {
			logger.info("No matching files found for barcode: " + barCode);
		}
        return matchingFiles.toArray(new File[0]);
    }

    public StampatoModel unpublish(StampatoModel model) throws IOException {
        try {
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
        } catch(Exception e) {
        	logger.error("Error to delete: " + model.getBarcode());
	        throw new IOException("Errore nella sospensione della pubblicazione dello stampato: " + model.getBarcode());
        }

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
                if (deleted) {
					logger.info("Successfully deleted allegato: " + allegato.getName());
				} else {
					logger.error("Failed to delete allegato: " + allegato.getName());
				}
            }
        }
    }

    public StampatoModel rigonero(StampatoModel model) throws IOException {
        try {
        	StampatoModel rigonero = new StampatoModel();
            String barcodeRigonero = extractRigoneroBarcode(model.getId().getBarcode());
            Optional<Stampato> stampatoOpt = stampatiRepository.findByIdLegislaturaAndIdBarcode(model.getId().getLegislatura(), model.getId().getBarcode());
            Optional<Stampato> rigoneroOpt = stampatiRepository.findByIdLegislaturaAndIdBarcode(model.getId().getLegislatura(), barcodeRigonero);
            if (rigoneroOpt.isPresent()) {
				throw new IOException("Rigo nero già esiste: " + barcodeRigonero);
			}
            if (stampatoOpt.isEmpty()) {
				throw new IOException("Stampato di partenza non trovato: " +model.getId().getBarcode());
			}
            unpublish(model);
            delete(model);
            beanMapper.map(model, rigonero);
            rigonero.getId().setBarcode(barcodeRigonero);
            rigonero.setRigoNero(model.getId().getBarcode());
            rigonero.setHtmlPresente(false);
            rigonero.setPdfPresente(false);
            rigonero.setDataDeleted(null);
            Stampato originalStampato = stampatoOpt.get();
            List<StampatoRelatoreModel> newRelatoriList = originalStampato.getStampatiRelatori()
                .stream().map(relatore -> {
                    StampatoRelatoreModel newRelatore = new StampatoRelatoreModel();
                    beanMapper.map(relatore, newRelatore);
                    newRelatore.setId(null); // Ensure new entity creation
                    return newRelatore;
                }).collect(Collectors.toList());
            List<StampatoFelModel> newFelList = originalStampato.getStampatiFel()
                .stream().map(fel -> {
                    StampatoFelModel newFel = new StampatoFelModel();
                    beanMapper.map(fel, newFel);
                    newFel.setId(null); // Ensure new entity creation
                    return newFel;
                }).collect(Collectors.toList());
            rigonero.setStampatiRelatori(new ArrayList<>(newRelatoriList));
            rigonero.setStampatiFel(new ArrayList<>(newFelList));
            extractorService.updateNomeFile(rigonero);
            rigonero = save(rigonero);
            logger.info("Rigo nero created successfully");
            return beanMapper.map(rigonero, StampatoModel.class);
        } catch(Exception e) {
        	logger.error("Error to rigonero: " + model.getBarcode());
	        throw new IOException("Errore nella creazione del rigonero dello stampato: " + model.getBarcode());
        }
    }

    private String extractRigoneroBarcode(String barcode) {
    	int i = barcode.length() - 1;
        while (i >= 0 && Character.isDigit(barcode.charAt(i))) {
			i--;
		}
        String prefix = barcode.substring(0, i + 1);
        String numberPart = barcode.substring(i + 1);
        int number = Integer.parseInt(numberPart) + 1;
        String newNumberPart = String.format("%0" + numberPart.length() + "d", number);
        return prefix + newNumberPart;
    }

    public StampatoModel errataCorrige(StampatoModel model) throws IOException {
    	try {
    		StampatoModel errataCorrige = new StampatoModel();
        	Optional<Stampato> last = stampatiRepository.findLastInserted();
        	if(last.isPresent()) {
        		String lastBarcode = last.get().getId().getBarcode();
        		String barcodeAvailable = extractErrataBarcode(lastBarcode);
        		unpublish(model);
        		delete(model);
        		errataCorrige = model;
        		errataCorrige.getId().setBarcode(barcodeAvailable);
        		errataCorrige.setHtmlPresente(false);
        		errataCorrige.setPdfPresente(false);
        		errataCorrige.setDataDeleted(null);
        		errataCorrige.setErrataCorrige(true);
        		errataCorrige.setSuffisso("Errata Corrige");
        		errataCorrige.setNomeFrontespizio(errataCorrige.getNomeFrontespizio() + "-Errata Corrige");
        		extractorService.updateNomeFile(errataCorrige);
        		errataCorrige = save(errataCorrige);
    	        logger.info("Errata Corrige created successfully");
    	        return beanMapper.map(errataCorrige, StampatoModel.class);
        	}
            return null;
    	} catch(Exception e) {
        	logger.error("Error to errata corrige: " + model.getBarcode());
	        throw new IOException("Errore nella creazione dell'errata corrige dello stampato: " + model.getBarcode());
        }
    }

    private String getStralcio(String atto) {
	    Pattern pattern = Pattern.compile("(?i)(?<=\\d-)(" + String.join("|", STRALCIO) + ")");
	    Matcher matcher = pattern.matcher(atto);
	    if (matcher.find()) {
			return matcher.group(1);
		}
	    return null;
	}

    private String extractErrataBarcode(String barcode) {
        int i = barcode.length() - 1;
        while (i >= 0 && Character.isDigit(barcode.charAt(i))) {
			i--;
		}
        String prefix = barcode.substring(0, i + 1);
        String numberPart = barcode.substring(i + 1);
        int number = Integer.parseInt(numberPart);
        number = ((number / 10) + 1) * 10;
        String newNumberPart = String.format("%0" + numberPart.length() + "d", number);
        return prefix + newNumberPart;
    }

}