package it.camera.stampati.model;

import java.util.Date;

import it.camera.stampati.enumerator.StampatoFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TypographyToProcessModel {

	private String barcode;
    private String legislaturaId;
    private StampatoFormat format;
    private Date dataDeleted;
    
	public TypographyToProcessModel(String fileName, String leg, StampatoFormat format, Date dataDeleted) {
		this.barcode = fileName;
		this.legislaturaId = leg;
		this.format = format;		
		this.dataDeleted = dataDeleted;
	}

}
