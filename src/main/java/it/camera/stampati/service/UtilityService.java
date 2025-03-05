package it.camera.stampati.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import it.camera.stampati.model.CommissioneModel;
import it.camera.stampati.model.LegislaturaModel;
import it.camera.stampati.util.CommissioniHandler;
import it.esinware.mapping.BeanMapper;

@Service
public class UtilityService {

private static final Logger logger = LoggerFactory.getLogger(UtilityService.class);
	
	@Autowired
	private BeanMapper beanMapper;
	@Value("${url.legislature}")
    private String urlLegislature;
	@Value("${url.commissions}")
    private String urlCommissions;
	@Value("${stampati.shared.input}")
	private String urlPreview;
	
	public LegislaturaModel getLastLegislature() {
        List<LegislaturaModel> legislatures = getLegislature();
        return legislatures.stream().max(Comparator.comparingInt(l -> l.getLegArabo())).orElse(null); 
    }
	
	public List<LegislaturaModel> getLegislature() {
	    try {
	        logger.debug("Fetching legislature data from URL: {}", urlLegislature);        
	        RestTemplate restTemplate = new RestTemplate();
	        String xmlResponse = restTemplate.getForObject(urlLegislature, String.class);
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document doc = builder.parse(new ByteArrayInputStream(xmlResponse.getBytes(StandardCharsets.UTF_8)));        	        
	        NodeList legislatures = doc.getElementsByTagName("legislatura");
	        List<LegislaturaModel> legislatureList = new ArrayList<>();
	        for (int i = 0; i < legislatures.getLength(); i++) {
	            Element legislatureElement = (Element) legislatures.item(i);
	            int legArabo = Integer.parseInt(legislatureElement.getAttribute("legArabo"));
	            if (legArabo >= 15) {
	                LegislaturaModel legislatura = new LegislaturaModel();
	                legislatura.setId(legislatureElement.getAttribute("id"));
	                legislatura.setLegRomano(legislatureElement.getAttribute("legRomano"));
	                legislatura.setLegArabo(legArabo);
	                legislatura.setDataInizio(legislatureElement.getAttribute("dataInizio"));
	                legislatura.setDataFine(legislatureElement.getAttribute("dataFine"));               
	                legislatureList.add(legislatura);
	                logger.debug("Added legislature: {}", legislatura);
	            }
	        }
	        legislatureList.sort(Comparator.comparingInt(LegislaturaModel::getLegArabo).reversed());
	        logger.info("Legislatures from 17th: {}", legislatureList);
	        return legislatureList;
	    } catch (Exception e) {
	        logger.error("Error occurred while fetching legislature data", e);
	        throw new RuntimeException("Failed to fetch legislature data", e);
	    }
	}
	
	public List<CommissioneModel> getCommissions(Long leg) {
        Set<CommissioneModel> commissioni = new HashSet<>();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            String url = MessageFormat.format(urlCommissions, leg);
            CommissioniHandler handlerCamera = new CommissioniHandler(commissioni, leg);
            saxParser.parse(url, handlerCamera);
            return commissioni.stream().filter(c -> c.isPermanent() == true)
            		.sorted(Comparator.comparing(CommissioneModel::getAulId)).collect(Collectors.toList());
        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.error("Error parsing commissions for legislature {}: {}", leg, e.getMessage());
        }
        return Collections.emptyList();
    }

	public byte[] getPreview(String filePath, String leg, String extension) throws IOException {
	    String url = MessageFormat.format(urlPreview, leg, extension) + "/" + filePath;
	    Path file = Paths.get(url);
	    if (!Files.exists(file))
	        throw new IOException("File not found: " + url);
	    try {
	        return Files.readAllBytes(file);
	    } catch (IOException ioex) {
	        throw new IOException("Error reading file: " + url, ioex);
	    }
	}
}
