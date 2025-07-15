package it.camera.stampati.parser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.camera.stampati.model.StampatoIdModel;
import it.camera.stampati.model.StampatoModel;
import it.camera.stampati.model.StampatoRelatoreModel;

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
            if(!"0".equals(getMeta(doc, "rigoNero"))) {
				stampato.setRigoNero(extractRigoNeroParent(stampatoId.getBarcode()));
			}
            //stampato.setErrataCorrige(getMeta(doc, "errataCorrige") == null ? null : !"0".equals(getMeta(doc, "errataCorrige")));
            String errata = getMeta(doc, "errataCorrige");
            stampato.setErrataCorrige(errata != null && !"0".equals(errata));
            if(stampato.getErrataCorrige()) {
				stampato.setSuffisso("-Errata Corrige");
			}
            stampato.setHtmlPresente(true);
            String atto = getAtto(doc);
            stampato.setLettera(getLettera(atto));
            stampato.setNavette(getNavette(atto));
            stampato.setRelazioneMin(getMinoranza(atto));
            stampato.setRinvioInCommissione(getRinvio(atto));
            String stralcio = getStralcio(atto);
            stampato.setNumeroAtto(stralcio != null ? atto.split("-")[0] + "-" + stralcio : atto.split("-")[0]);
            stampato.setNumeriPDL(getNumeriPDL(doc));
            stampato.setNomeFrontespizio(getNomeFrontespizio(doc));
            stampato.setSuffisso(getSuffisso(doc));

            // Title
            Element title = doc.selectFirst("p.titolo");
            if (title != null) {
				stampato.setTitolo(title.text());
			}

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
        while (i >= 0 && Character.isDigit(barcode.charAt(i))) {
			i--;
		}
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
            if (id.contains(".")) {
				return id.split("\\.")[1];
			}
        }
        return "";
    }

    private String getNomeFrontespizio(Document doc) {
        Element p = doc.selectFirst("p.numeroAtto");
        if (p != null) {
            String text = p.text().replaceAll("\\s+", " ").trim();
            if (text.startsWith("N. ")) {
				text = text.substring(3).trim();
			}
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
        String cleaned = e.text().replaceAll("\\s+", " ").trim().toLowerCase();
        String[] words = cleaned.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
				result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
			}
        }
        return result.toString().trim();
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
        Matcher matcher = Pattern.compile("(?i)(?<=\\b[A-Z]-)(" + String.join("|", XhtmlParser.STRALCIO) + ")").matcher(atto);
        return matcher.find() ? matcher.group(1) : null;
    }

    private Boolean getRinvio(String atto) {
        Matcher matcher = Pattern.compile("\\b[A-Z]R\\b").matcher(atto);
        return matcher.find();
    }
    
    public String getNumeriPDL(Document doc) {
    	Element p = doc.selectFirst("p.numeroAtto");
        if (p != null) {
        	String cleanedAtto = p.text().trim();
            cleanedAtto = cleanedAtto.replaceAll("(?i)^N\\.\\s*", "");
            //Pattern pattern = Pattern.compile("^(\\d+(?:-\\d+)*)");
            Pattern pattern = Pattern.compile("^(\\d+(?:-\\d+)*(?:-[a-zA-Z]+)?)");            
            Matcher matcher = pattern.matcher(cleanedAtto);
            if (matcher.find()) {
                return matcher.group(1);
            } else {
            	logger.error("Error parsing XHTML: {}", cleanedAtto);
                throw new RuntimeException("Parse failed " + cleanedAtto);
            }
        }
        return null;
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
        if (id.contains("commissione")) {
			relatore.setIdCommissione(Integer.parseInt(id.substring(id.lastIndexOf(".") + 1)));
		}
        return relatore;
    }
}