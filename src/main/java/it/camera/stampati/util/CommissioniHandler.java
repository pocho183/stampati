package it.camera.stampati.util;

import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import it.camera.stampati.model.CommissioneModel;

public class CommissioniHandler extends DefaultHandler {

	private Set<CommissioneModel> commissioni;
	boolean bBreve = false;
	boolean bEstesa = false;
	private String ramo;
	private String elementValue;
	private Long leg;
	CommissioneModel commissione = null;
	String tipologia = "";

	public CommissioniHandler(Set<CommissioneModel> commissioni, Long leg) {
		this.commissioni = commissioni;
		this.leg = leg;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if(qName.equals("commissione")) {
			tipologia = attributes.getValue("tipologia");
			String id = attributes.getValue("id");
			if(tipologia.equals("commissione") || tipologia.equals("comitato") || tipologia.equals("giunta")) {
				commissione = new CommissioneModel();
				commissione.setCodiceTipografia(id);
				commissione.setLegislatura(leg);
			}
		} else if(commissione != null && qName.equals("organo")) {
			if(!attributes.getValue("id").equals("x"))
				commissione.setAulId(Long.parseLong(attributes.getValue("id")));
			ramo = attributes.getValue("ramo");
			if(!ramo.equals("S")) {
				commissione.setRamo(ramo);
				if(!commissione.getCodiceTipografia().equals("20"))
					commissioni.add(commissione);
			}
		} else if(commissione != null && qName.equals("breve")) {
			bBreve = true;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(qName.equals("commissione")) {
			commissione = null;
		} else if(commissione != null && qName.equals("xml") && bBreve) {
			commissione.setDenominazione(elementValue);
		} else if(commissione != null && qName.equals("breve")) {
			bBreve = false;
		} else if(commissione != null && qName.equals("estesa")) {
			bEstesa = false;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		elementValue = new String(ch, start, length);
	}
}

