package it.camera.stampati.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import it.esinware.mapping.BeanMapper;

@Service
public class UtilityService {

private static final Logger logger = LoggerFactory.getLogger(UtilityService.class);
	
	@Autowired
	private BeanMapper beanMapper;
	@Value("${url.legislature}")
    private String urlLegislature;
	@Value("${stampati.shared.input}")
	private String urlPreview;
	
	public String getLastLegislature() {
        try {
            logger.debug("Fetching legislature data from URL: {}", urlLegislature);        
            RestTemplate restTemplate = new RestTemplate();
            String xmlResponse = restTemplate.getForObject(urlLegislature, String.class);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new java.io.ByteArrayInputStream(xmlResponse.getBytes()));
            NodeList legislatures = doc.getElementsByTagName("legislatura");
            int maxLegArabo = -1;
            String latestLegislature = "";
            for (int i = 0; i < legislatures.getLength(); i++) {
                Element legislatureElement = (Element) legislatures.item(i);
                int legArabo = Integer.parseInt(legislatureElement.getAttribute("legArabo"));
                logger.debug("Processing legislature: legArabo={}, id={}", legArabo, legislatureElement.getAttribute("id"));
                if (legArabo > maxLegArabo) {
                    maxLegArabo = legArabo;
                    latestLegislature = legislatureElement.getAttribute("legArabo");
                }
            }
            logger.info("Latest legislature found: {}", latestLegislature);
            return latestLegislature;
        } catch (Exception e) {
            logger.error("Error occurred while fetching the latest legislature", e);
            throw new RuntimeException("Failed to fetch legislature data", e);
        }
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
