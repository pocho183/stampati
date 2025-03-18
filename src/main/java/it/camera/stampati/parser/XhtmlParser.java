package it.camera.stampati.parser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.camera.stampati.model.StampatoIdModel;
import it.camera.stampati.model.StampatoModel;
import it.camera.stampati.model.StampatoRelatoreModel;

public class XhtmlParser {
	
	private static final Logger logger = LoggerFactory.getLogger(XhtmlParser.class);


	public StampatoModel parse(File file) {
		try {
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document document = builder.parse(file);
	        document.getDocumentElement().normalize();
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	        StampatoIdModel stampatoId = new StampatoIdModel();
	        StampatoModel stampato = new StampatoModel();
	        // Extract metadata
	        stampatoId.setLegislatura(getMetaContent(document, "legislatura.a"));
	        stampatoId.setBarcode(getMetaContent(document, "codiceABarre"));
	        stampato.setId(stampatoId);
	        //stampato.setRigoNero("0".equals(getMetaContent(document, "rigoNero")) ? false : true);
	        if(!("0".equals(getMetaContent(document, "rigoNero"))))
	        	stampato.setRigoNero(extractParent(stampatoId.getBarcode()));
	        stampato.setErrataCorrige("0".equals(getMetaContent(document, "errataCorrige")) ? false : getMetaContent(document, "errataCorrige") != null);
	        stampato.setHtmlPresente(true);
	        // Extract number of the act
	        String atto = getAtto(document);
	        stampato.setLettera(getLettera(atto));
	        stampato.setNavette(getNavette(atto));
	        stampato.setRelazioneMin(getMinoranza(atto));
	        stampato.setRinvioInCommissione(getRinvio(atto));
	        stampato.setNumeroAtto(atto.split("-")[0]);
	        stampato.setNumeriPDL(atto);
	        // Extract the first match title
	        NodeList titleNodes = document.getElementsByTagName("p");
	        for (int i = 0; i < titleNodes.getLength(); i++) {
	            Element p = (Element) titleNodes.item(i);
	            if ("titolo".equals(p.getAttribute("class"))) {
	                stampato.setTitolo(p.getTextContent().trim());
	                break;
	            }
	        }
	        // Extract data presentazione (es: ac.64.data.presentazione.20130315)
	        NodeList presentazioneList = document.getElementsByTagName("p");
	        for (int i = 0; i < presentazioneList.getLength(); i++) {
	            Element p = (Element) presentazioneList.item(i);
	            if ("dataPresentazione".equals(p.getAttribute("class"))) {
	                String presentazioneId = p.getAttribute("id");
	                if (presentazioneId != null && !presentazioneId.isEmpty()) {
	                    String[] fields = presentazioneId.split("\\.");
	                    if (fields.length > 4) {
	                        stampato.setDataPresentazione(sdf.parse(fields[4]));
	                        stampato.setTipoPresentazione(getTipoPresentazione(fields[3]));
	                    }
	                }
	            }
	        }
	        // Extract relatori
	        NodeList relatoriXml = document.getElementsByTagName("a");
	        List<StampatoRelatoreModel> relatori = new ArrayList<StampatoRelatoreModel>();
	        for (int i = 0; i < relatoriXml.getLength(); i++) {
	            Node node = relatoriXml.item(i);
	            if (node.getAttributes() != null && node.getAttributes().getNamedItem("class") != null && "relatore".equals(node.getAttributes().getNamedItem("class").getNodeValue()))
	                relatori.add(getRelatore(node, stampatoId.getLegislatura(), stampatoId.getBarcode()));
	        }
	        stampato.setStampatiRelatori(relatori);
	        return stampato;
		} catch (Exception e) {
			logger.error("Error parsing Xhtml {}", e.getMessage());
            throw new RuntimeException("Error parsing file: " + e.getLocalizedMessage(), e);
        }	
		
	}
	
	private String extractParent(String barcode) {
        int i = barcode.length() - 1;
        while (i >= 0 && Character.isDigit(barcode.charAt(i)))
            i--;
        String prefix = barcode.substring(0, i + 1);
        String numberPart = barcode.substring(i + 1);
        int number = Integer.parseInt(numberPart) - 1;
        String newNumberPart = String.format("%0" + numberPart.length() + "d", number);
        return prefix + newNumberPart;
	}
	
	private String getMetaContent(Document document, String metaName) {
        NodeList metaNodes = document.getElementsByTagName("meta");
        for (int i = 0; i < metaNodes.getLength(); i++) {
            Element meta = (Element) metaNodes.item(i);
            if (metaName.equals(meta.getAttribute("name")))
                return meta.getAttribute("content");
        }
        return null;
    }

    private String getTipoPresentazione(String tipo) {
        switch (tipo.toLowerCase()) {
            case "presentazione": return "presentato";
            case "trasmissione": return "trasmesso";
            case "stralcio": return "stralciato";
            default: return "Unknown";
        }
    }
    
    private String getAtto(Document doc) {
        NodeList attoNodes = doc.getElementsByTagName("p");
        for (int i = 0; i < attoNodes.getLength(); i++) {
            Element p = (Element) attoNodes.item(i);
            if ("numeroAtto".equals(p.getAttribute("class"))) {
                String id = p.getAttribute("id");
                if (id != null && id.contains("."))
                    return id.split("\\.")[1];
            }
        }
        return "";
    }
    
    private String getLettera(String atto) {
        String lettera = null;
        Pattern pattern = Pattern.compile("A|C|E|G|I|M|O|Q|S|U|Z");
        Matcher matcher = pattern.matcher(atto);
        if (matcher.find())
            lettera = matcher.group(0);
        return lettera;
    }
    
    private String getNavette(String atto) {
		String navette = null;
		Pattern pattern = Pattern.compile("\\b(B|D|F|H|L|N|P|R|T|V)\\b"); 
		Matcher matcher = pattern.matcher(atto);
		if (matcher.find())
			navette = matcher.group(0);
		return navette;
	}

    private String getMinoranza(String atto) {
        String minoranza = null;
        Pattern pattern = Pattern.compile("bis|ter|quater|quinquies|sexies|septies|octies|novies|decies"
        		+ "|undecies|duodecies|terdecies|quaterdecies|quindecies|sedecies|septiesdecies|duodevicies|undevicies|vicies"
        		+ "|semeletvicies|bisetvicies|teretvicies|quateretvicies|quinquiesetvicies|sexiesetvicies|septiesetvicies|octiesetvicies|noviesetvicies|tricies"
        		+ "|semelettricies|bisettricies|terettricies|quaterettricies|quinquiesettricies|sexiesettricies|septiesettricies|octiesettricies|noviesettricies|quadragies"
        		+ "|semeletquadragies|bisetquadragies|teretquadragies|quateretquadragies|quinquiesetquadragies|sexiesetquadragies|septiesetquadragies|octiesetquadragies|noviesetquadragies|quinquagies");
        Matcher matcher = pattern.matcher(atto);
        if (matcher.find())
        	minoranza = matcher.group(0);
        return minoranza;
    }
    
    private Boolean getRinvio(String atto) {
		Boolean rinvio = null;
		Pattern pattern = Pattern.compile("\\b[A-Z]R\\b");
		Matcher matcher = pattern.matcher(atto);
		if (matcher.find())
			return true;
		return false;
	}
    
    /**
	 * Estra le persone dal tracciato stringa coi seguenti formati: 
	 * 		relatore.idpersona.<99999>
	 * 		relatore.<maggioranza|minoranza>.idpersona.<99999>
	 * 		relatore.idpersona.<99999>.commissione.<99>
	 * 		relatore.<maggioranza|minoranza>.idpersona.<99999>.commissione.<99>
	 */
    private StampatoRelatoreModel getRelatore(Node node, String leg, String barCode) {
		String id = node.getAttributes().getNamedItem("id").getNodeValue();
		StampatoRelatoreModel relatore = new StampatoRelatoreModel();
		String[] fields = id.split("\\.");
		int endIndex = id.contains("commissione") ? id.indexOf(".commissione") : id.length();
		String idPersona = id.substring(id.indexOf(".idpersona", 0) + 11, endIndex);
		relatore.setIdPersona(Long.valueOf(idPersona));
		relatore.setCognome(node.getTextContent());
		if (fields[1].toLowerCase().equals("minoranza"))
			relatore.setMaggioranza(false);
		else 
			relatore.setMaggioranza(true);
		if(id.contains("commissione"))
			relatore.setIdCommissione(Integer.valueOf(id.substring(id.lastIndexOf(".") + 1)));
		return relatore;
	}
}
