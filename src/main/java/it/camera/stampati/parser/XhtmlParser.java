package it.camera.stampati.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import it.camera.stampati.model.StampatoIdModel;
import it.camera.stampati.model.StampatoModel;
import it.camera.stampati.model.StampatoRelatoreModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XhtmlParser {

    private static final Logger logger = LoggerFactory.getLogger(XhtmlParser.class);
    public static final List<String> STRALCIO = List.of( "bis", "ter", "quater",
    	"quinquies", "sexies", "septies", "octies", "novies", "decies", "undecies",
    	"duodecies", "terdecies", "quaterdecies", "quindecies", "sedecies",
    	"septiesdecies", "duodevicies", "undevicies", "vicies", "semeletvicies",
    	"bisetvicies", "teretvicies", "quateretvicies", "quinquiesetvicies",
    	"sexiesetvicies", "septiesetvicies", "octiesetvicies", "noviesetvicies",
    	"tricies", "semelettricies", "bisettricies", "terettricies",
    	"quaterettricies", "quinquiesettricies", "sexiesettricies",
    	"septiesettricies", "octiesettricies", "noviesettricies", "quadragies",
    	"semeletquadragies", "bisetquadragies", "teretquadragies",
    	"quateretquadragies", "quinquiesetquadragies", "sexiesetquadragies",
    	"septiesetquadragies", "octiesetquadragies", "noviesetquadragies",
    	"quinquagies" );

    public StampatoModel parse(File file) {
        try {
            Document doc = Jsoup.parse(file, "UTF-8");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            StampatoIdModel stampatoId = new StampatoIdModel();
            StampatoModel stampato = new StampatoModel();
            // Metadata
            stampatoId.setLegislatura(getMeta(doc, "legislatura.a"));
            stampatoId.setBarcode(getMeta(doc, "codiceABarre"));
            stampato.setId(stampatoId);
            if(!"0".equals(getMeta(doc, "rigoNero"))) stampato.setRigoNero(extractRigoNeroParent(stampatoId.getBarcode()));
            //stampato.setErrataCorrige(getMeta(doc, "errataCorrige") == null ? null : !"0".equals(getMeta(doc, "errataCorrige")));
            String errata = getMeta(doc, "errataCorrige");
            stampato.setErrataCorrige(errata != null && !"0".equals(errata));
            if(stampato.getErrataCorrige()) stampato.setSuffisso("-Errata Corrige");
            stampato.setHtmlPresente(true);
            String atto = getAtto(doc);
            stampato.setLettera(getLettera(atto));
            stampato.setNavette(getNavette(atto));
            stampato.setRelazioneMin(getMinoranza(atto));
            stampato.setRinvioInCommissione(getRinvio(atto));
            String stralcio = getStralcio(atto);
            stampato.setNumeroAtto(stralcio != null ? atto.split("-")[0] + "-" + stralcio : atto.split("-")[0]);
            stampato.setNumeriPDL(atto);
            stampato.setNomeFrontespizio(getNomeFrontespizio(doc));
            stampato.setSuffisso(getSuffisso(doc));

            // Title
            Element title = doc.selectFirst("p.titolo");
            if (title != null) stampato.setTitolo(title.text());

            // Presentation
            Elements pres = doc.select("p.dataPresentazione");
            for (Element p : pres) {
                String id = p.id();
                if (!id.isEmpty()) {
                    String[] fields = id.split("\\.");
                    if (fields.length > 4) {
                        stampato.setDataPresentazione(sdf.parse(fields[4]));
                        stampato.setTipoPresentazione(getTipoPresentazione(fields[3]));
                    }
                }
            }

            // Relatori
            List<StampatoRelatoreModel> relatori = new ArrayList<>();
            Elements relatoriNodes = doc.select("a.relatore");
            for (Element a : relatoriNodes) {
                relatori.add(getRelatore(a, stampatoId.getLegislatura(), stampatoId.getBarcode()));
            }
            stampato.setStampatiRelatori(relatori);
            return stampato;

        } catch (Exception e) {
            logger.error("Error parsing XHTML: {}", e.getMessage());
            throw new RuntimeException("Parse failed", e);
        }
    }

    private String getMeta(Document doc, String name) {
        Element meta = doc.selectFirst("meta[name=" + name + "]");
        return meta != null ? meta.attr("content") : null;
    }

    private String extractRigoNeroParent(String barcode) {
        int i = barcode.length() - 1;
        while (i >= 0 && Character.isDigit(barcode.charAt(i))) i--;
        String prefix = barcode.substring(0, i + 1);
        String numberPart = barcode.substring(i + 1);
        int number = Integer.parseInt(numberPart) - 1;
        return prefix + String.format("%0" + numberPart.length() + "d", number);
    }

    private String getTipoPresentazione(String tipo) {
        return switch (tipo.toLowerCase()) {
            case "presentazione" -> "presentato";
            case "trasmissione" -> "trasmesso";
            case "stralcio" -> "stralciato";
            default -> "Unknown";
        };
    }

    private String getAtto(Document doc) {
        Element atto = doc.selectFirst("p.numeroAtto");
        if (atto != null) {
            String id = atto.id();
            if (id.contains(".")) return id.split("\\.")[1];
        }
        return "";
    }

    private String getNomeFrontespizio(Document doc) {
        Element p = doc.selectFirst("p.numeroAtto");
        if (p != null) {
            String text = p.text().replaceAll("\\s+", " ").trim();
            if (text.startsWith("N. ")) text = text.substring(3).trim();
            return text;
        }
        return "";
    }

    private String getSuffisso(Document doc) {
        Element p = doc.selectFirst("p.numeroAtto");
        if (p != null) {
            Element next = p.nextElementSibling();
            if (next != null && next.hasClass("center")) {
                return getTextWithSpaces(next);
            }
        }
        return "";
    }

    private String getTextWithSpaces(Element e) {
        return e.text().replaceAll("\\s+", " ").trim();
    }

    private String getLettera(String atto) {
        Matcher m = Pattern.compile("A|C|E|G|I|M|O|Q|S|U|Z").matcher(atto);
        return m.find() ? m.group() : null;
    }

    private String getNavette(String atto) {
        Matcher m = Pattern.compile("\\b(B|D|F|H|L|N|P|R|T|V)\\b").matcher(atto);
        return m.find() ? m.group() : null;
    }

    private String getStralcio(String atto) {
        Matcher matcher = Pattern.compile("-(?i)(" + String.join("|", XhtmlParser.STRALCIO) + ")").matcher(atto);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String getMinoranza(String atto) {
        Matcher matcher = Pattern.compile("_(?i)(" + String.join("|", XhtmlParser.STRALCIO) + ")").matcher(atto);
        return matcher.find() ? matcher.group(1) : null;
    }

    private Boolean getRinvio(String atto) {
        Matcher matcher = Pattern.compile("\\b[A-Z]R\\b").matcher(atto);
        return matcher.find();
    }

    private StampatoRelatoreModel getRelatore(Element node, String leg, String barCode) {
        String id = node.id();
        StampatoRelatoreModel relatore = new StampatoRelatoreModel();
        String[] fields = id.split("\\.");
        int endIndex = id.contains("commissione") ? id.indexOf(".commissione") : id.length();
        String idPersona = id.substring(id.indexOf(".idpersona") + 11, endIndex);
        relatore.setIdPersona(Long.valueOf(idPersona));
        relatore.setCognome(node.text());
        relatore.setMaggioranza(!fields[1].equalsIgnoreCase("minoranza"));
        if (id.contains("commissione"))
            relatore.setIdCommissione(Integer.parseInt(id.substring(id.lastIndexOf(".") + 1)));
        return relatore;
    }
}

/*
 * public class XhtmlParser {
 * 
 * private static final Logger logger =
 * LoggerFactory.getLogger(XhtmlParser.class);
 * 
 * public static final List<String> STRALCIO = List.of( "bis", "ter", "quater",
 * "quinquies", "sexies", "septies", "octies", "novies", "decies", "undecies",
 * "duodecies", "terdecies", "quaterdecies", "quindecies", "sedecies",
 * "septiesdecies", "duodevicies", "undevicies", "vicies", "semeletvicies",
 * "bisetvicies", "teretvicies", "quateretvicies", "quinquiesetvicies",
 * "sexiesetvicies", "septiesetvicies", "octiesetvicies", "noviesetvicies",
 * "tricies", "semelettricies", "bisettricies", "terettricies",
 * "quaterettricies", "quinquiesettricies", "sexiesettricies",
 * "septiesettricies", "octiesettricies", "noviesettricies", "quadragies",
 * "semeletquadragies", "bisetquadragies", "teretquadragies",
 * "quateretquadragies", "quinquiesetquadragies", "sexiesetquadragies",
 * "septiesetquadragies", "octiesetquadragies", "noviesetquadragies",
 * "quinquagies" );
 * 
 * public StampatoModel parse(File file) { try { DocumentBuilderFactory factory
 * = DocumentBuilderFactory.newInstance(); // Prevent loading external DTDs but
 * allow DOCTYPE declaration //factory.setFeature(
 * "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
 * //factory.setFeature("http://xml.org/sax/features/validation", false);
 * //factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl",
 * false); DocumentBuilder builder = factory.newDocumentBuilder(); // Ignore
 * DTD-related errors //builder.setEntityResolver((publicId, systemId) -> new
 * InputSource(new StringReader(""))); Document document = builder.parse(file);
 * document.getDocumentElement().normalize(); SimpleDateFormat sdf = new
 * SimpleDateFormat("yyyyMMdd"); StampatoIdModel stampatoId = new
 * StampatoIdModel(); StampatoModel stampato = new StampatoModel(); // Extract
 * metadata stampatoId.setLegislatura(getMetaContent(document,
 * "legislatura.a")); stampatoId.setBarcode(getMetaContent(document,
 * "codiceABarre")); stampato.setId(stampatoId);
 * if(!("0".equals(getMetaContent(document, "rigoNero"))))
 * stampato.setRigoNero(extractRigoNeroParent(stampatoId.getBarcode()));
 * stampato.setErrataCorrige("0".equals(getMetaContent(document,
 * "errataCorrige")) ? false : getMetaContent(document, "errataCorrige") !=
 * null);
 * 
 * // Da provare con un caso di errata corrige perchè legge l'html e in questo
 * caso // potrebbe inserirlo due volte if(stampato.getErrataCorrige())
 * stampato.setSuffisso("-Errata Corrige"); stampato.setHtmlPresente(true); //
 * Extract number of the act String atto = getAtto(document);
 * stampato.setLettera(getLettera(atto)); stampato.setNavette(getNavette(atto));
 * 
 * // Da rivedere, la minoranza è quando c'è _bis, non il caso -bis
 * stampato.setRelazioneMin(getMinoranza(atto));
 * stampato.setRinvioInCommissione(getRinvio(atto)); String stralcio =
 * getStralcio(atto); stampato.setNumeroAtto(stralcio != null ?
 * atto.split("-")[0] + "-" + stralcio : atto.split("-")[0]);
 * stampato.setNumeriPDL(atto);
 * stampato.setNomeFrontespizio(getNomeFrontespizio(document));
 * stampato.setSuffisso(getSuffisso(document)); // Extract the first match title
 * NodeList titleNodes = document.getElementsByTagName("p"); for (int i = 0; i <
 * titleNodes.getLength(); i++) { Element p = (Element) titleNodes.item(i); if
 * ("titolo".equals(p.getAttribute("class"))) {
 * stampato.setTitolo(p.getTextContent().trim()); break; } } // Extract data
 * presentazione (es: ac.64.data.presentazione.20130315) NodeList
 * presentazioneList = document.getElementsByTagName("p"); for (int i = 0; i <
 * presentazioneList.getLength(); i++) { Element p = (Element)
 * presentazioneList.item(i); if
 * ("dataPresentazione".equals(p.getAttribute("class"))) { String
 * presentazioneId = p.getAttribute("id"); if (presentazioneId != null &&
 * !presentazioneId.isEmpty()) { String[] fields = presentazioneId.split("\\.");
 * if (fields.length > 4) { stampato.setDataPresentazione(sdf.parse(fields[4]));
 * stampato.setTipoPresentazione(getTipoPresentazione(fields[3])); } } } } //
 * Extract relatori NodeList relatoriXml = document.getElementsByTagName("a");
 * List<StampatoRelatoreModel> relatori = new
 * ArrayList<StampatoRelatoreModel>(); for (int i = 0; i <
 * relatoriXml.getLength(); i++) { Node node = relatoriXml.item(i); if
 * (node.getAttributes() != null && node.getAttributes().getNamedItem("class")
 * != null &&
 * "relatore".equals(node.getAttributes().getNamedItem("class").getNodeValue()))
 * relatori.add(getRelatore(node, stampatoId.getLegislatura(),
 * stampatoId.getBarcode())); } stampato.setStampatiRelatori(relatori); return
 * stampato; } catch (Exception e) { logger.error("Error parsing Xhtml {}",
 * e.getMessage()); throw new RuntimeException("Error parsing file: " +
 * e.getLocalizedMessage(), e); }
 * 
 * }
 * 
 * private String extractRigoNeroParent(String barcode) { int i =
 * barcode.length() - 1; while (i >= 0 && Character.isDigit(barcode.charAt(i)))
 * i--; String prefix = barcode.substring(0, i + 1); String numberPart =
 * barcode.substring(i + 1); int number = Integer.parseInt(numberPart) - 1;
 * String newNumberPart = String.format("%0" + numberPart.length() + "d",
 * number); return prefix + newNumberPart; }
 * 
 * private String getMetaContent(Document document, String metaName) { NodeList
 * metaNodes = document.getElementsByTagName("meta"); for (int i = 0; i <
 * metaNodes.getLength(); i++) { Element meta = (Element) metaNodes.item(i); if
 * (metaName.equals(meta.getAttribute("name"))) return
 * meta.getAttribute("content"); } return null; }
 * 
 * private String getTipoPresentazione(String tipo) { switch
 * (tipo.toLowerCase()) { case "presentazione": return "presentato"; case
 * "trasmissione": return "trasmesso"; case "stralcio": return "stralciato";
 * default: return "Unknown"; } }
 * 
 * private String getAtto(Document doc) { NodeList attoNodes =
 * doc.getElementsByTagName("p"); for (int i = 0; i < attoNodes.getLength();
 * i++) { Element p = (Element) attoNodes.item(i); if
 * ("numeroAtto".equals(p.getAttribute("class"))) { String id =
 * p.getAttribute("id"); if (id != null && id.contains(".")) return
 * id.split("\\.")[1]; } } return ""; }
 * 
 * private String getNomeFrontespizio(Document doc) { NodeList attoNodes =
 * doc.getElementsByTagName("p"); for (int i = 0; i < attoNodes.getLength();
 * i++) { Element p = (Element) attoNodes.item(i); if
 * ("numeroAtto".equals(p.getAttribute("class"))) { String text =
 * p.getTextContent().replaceAll("\\s+", " ").trim(); if
 * (text.startsWith("N. ")) { text = text.substring(3).trim(); } return text; }
 * } return ""; }
 * 
 * private String getSuffisso(Document doc) { NodeList attoNodes =
 * doc.getElementsByTagName("p"); for (int i = 0; i < attoNodes.getLength();
 * i++) { Element p = (Element) attoNodes.item(i); if
 * ("numeroAtto".equals(p.getAttribute("class"))) { Node nextNode =
 * p.getNextSibling(); while (nextNode != null && nextNode.getNodeType() ==
 * Node.TEXT_NODE) { nextNode = nextNode.getNextSibling(); } if (nextNode
 * instanceof Element) { Element nextElement = (Element) nextNode; if
 * ("center".equals(nextElement.getAttribute("class"))) return
 * getTextWithSpaces(nextElement).trim(); } break; } } return ""; }
 * 
 * private String getTextWithSpaces(Element element) { StringBuilder text = new
 * StringBuilder(); NodeList childNodes = element.getChildNodes(); for (int i =
 * 0; i < childNodes.getLength(); i++) { Node node = childNodes.item(i); if
 * (node.getNodeType() == Node.TEXT_NODE) {
 * text.append(node.getTextContent().trim()).append(" "); } else if
 * (node.getNodeType() == Node.ELEMENT_NODE) {
 * text.append(getTextWithSpaces((Element) node)).append(" "); } } return
 * text.toString().replaceAll("\\s+", " ").trim(); }
 * 
 * 
 * private String getNomeFrontespizio(Document document) { NodeList attoNodes =
 * document.getElementsByTagName("p"); for (int i = 0; i <
 * attoNodes.getLength(); i++) { Element p = (Element) attoNodes.item(i); if
 * ("numeroAtto".equals(p.getAttribute("class"))) { // Extract text content of
 * the element String text = p.getTextContent().replaceAll("\\s+", " ").trim();
 * // Remove "N. " prefix if present if (text.startsWith("N. ")) text =
 * text.substring(3).trim(); // Check for an additional <p> tag with class
 * "center" (e.g., TOMO I case) Node nextNode = p.getNextSibling(); while
 * (nextNode != null && nextNode.getNodeType() == Node.TEXT_NODE) { nextNode =
 * nextNode.getNextSibling(); } if (nextNode instanceof Element) { Element
 * nextElement = (Element) nextNode; if
 * ("center".equals(nextElement.getAttribute("class"))) text += "-" +
 * nextElement.getTextContent().trim(); } return text; } } return ""; }
 * 
 * private String getLettera(String atto) { String lettera = null; Pattern
 * pattern = Pattern.compile("A|C|E|G|I|M|O|Q|S|U|Z"); Matcher matcher =
 * pattern.matcher(atto); if (matcher.find()) lettera = matcher.group(0); return
 * lettera; }
 * 
 * private String getNavette(String atto) { String navette = null; Pattern
 * pattern = Pattern.compile("\\b(B|D|F|H|L|N|P|R|T|V)\\b"); Matcher matcher =
 * pattern.matcher(atto); if (matcher.find()) navette = matcher.group(0); return
 * navette; }
 * 
 * private String getStralcio(String atto) { String minoranza = null; Pattern
 * pattern = Pattern.compile(
 * "bis|ter|quater|quinquies|sexies|septies|octies|novies|decies" +
 * "|undecies|duodecies|terdecies|quaterdecies|quindecies|sedecies|septiesdecies|duodevicies|undevicies|vicies"
 * +
 * "|semeletvicies|bisetvicies|teretvicies|quateretvicies|quinquiesetvicies|sexiesetvicies|septiesetvicies|octiesetvicies|noviesetvicies|tricies"
 * +
 * "|semelettricies|bisettricies|terettricies|quaterettricies|quinquiesettricies|sexiesettricies|septiesettricies|octiesettricies|noviesettricies|quadragies"
 * +
 * "|semeletquadragies|bisetquadragies|teretquadragies|quateretquadragies|quinquiesetquadragies|sexiesetquadragies|septiesetquadragies|octiesetquadragies|noviesetquadragies|quinquagies"
 * ); Matcher matcher = pattern.matcher(atto); if (matcher.find()) minoranza =
 * matcher.group(0); return minoranza; }
 * 
 * private String getMinoranza(String atto) { String minoranza = null; Pattern
 * pattern = Pattern.compile(
 * "bis|ter|quater|quinquies|sexies|septies|octies|novies|decies" +
 * "|undecies|duodecies|terdecies|quaterdecies|quindecies|sedecies|septiesdecies|duodevicies|undevicies|vicies"
 * +
 * "|semeletvicies|bisetvicies|teretvicies|quateretvicies|quinquiesetvicies|sexiesetvicies|septiesetvicies|octiesetvicies|noviesetvicies|tricies"
 * +
 * "|semelettricies|bisettricies|terettricies|quaterettricies|quinquiesettricies|sexiesettricies|septiesettricies|octiesettricies|noviesettricies|quadragies"
 * +
 * "|semeletquadragies|bisetquadragies|teretquadragies|quateretquadragies|quinquiesetquadragies|sexiesetquadragies|septiesetquadragies|octiesetquadragies|noviesetquadragies|quinquagies"
 * ); Matcher matcher = pattern.matcher(atto); if (matcher.find()) minoranza =
 * matcher.group(0); return minoranza; }
 * 
 * private String getStralcio(String atto) { Pattern pattern =
 * Pattern.compile("-(?i)(" + String.join("|", STRALCIO) + ")"); Matcher matcher
 * = pattern.matcher(atto); if (matcher.find()) { return matcher.group(1); }
 * return null; }
 * 
 * private String getMinoranza(String atto) { Pattern pattern =
 * Pattern.compile("_(?i)(" + String.join("|", STRALCIO) + ")"); Matcher matcher
 * = pattern.matcher(atto); if (matcher.find()) { return matcher.group(1); }
 * return null; }
 * 
 * private Boolean getRinvio(String atto) { Pattern pattern =
 * Pattern.compile("\\b[A-Z]R\\b"); Matcher matcher = pattern.matcher(atto); if
 * (matcher.find()) return true; return false; }
 * 
 *//**
	 * Estra le persone dal tracciato stringa coi seguenti formati:
	 * relatore.idpersona.<99999> relatore.<maggioranza|minoranza>.idpersona.<99999>
	 * relatore.idpersona.<99999>.commissione.<99>
	 * relatore.<maggioranza|minoranza>.idpersona.<99999>.commissione.<99>
	 *//*
		 * private StampatoRelatoreModel getRelatore(Node node, String leg, String
		 * barCode) { String id =
		 * node.getAttributes().getNamedItem("id").getNodeValue(); StampatoRelatoreModel
		 * relatore = new StampatoRelatoreModel(); String[] fields = id.split("\\.");
		 * int endIndex = id.contains("commissione") ? id.indexOf(".commissione") :
		 * id.length(); String idPersona = id.substring(id.indexOf(".idpersona", 0) +
		 * 11, endIndex); relatore.setIdPersona(Long.valueOf(idPersona));
		 * relatore.setCognome(node.getTextContent()); if
		 * (fields[1].toLowerCase().equals("minoranza")) relatore.setMaggioranza(false);
		 * else relatore.setMaggioranza(true); if(id.contains("commissione"))
		 * relatore.setIdCommissione(Integer.valueOf(id.substring(id.lastIndexOf(".") +
		 * 1))); return relatore; } }
		 */