package it.camera.stampati.parser;

import it.camera.stampati.model.StampatoIdModel;
import it.camera.stampati.model.StampatoModel;
import it.camera.stampati.model.TypographyToProcessModel;

public class PDFParser {

	public StampatoModel parse(TypographyToProcessModel model) {
		StampatoIdModel modelId = new StampatoIdModel();
		modelId.setBarcode(model.getBarcode());
		modelId.setLegislatura(model.getLegislaturaId());
		StampatoModel stampato = new StampatoModel();
		stampato.setId(modelId);
		stampato.setPdfPresente(true);
		return stampato;
	}
}
