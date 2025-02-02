package it.camera.stampati.model;

import java.util.Date;

import it.camera.stampati.enumerator.StampatoFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StampatoModel {

	private String barcode;
    private String legislaturaId;
    private StampatoFormat format;
    private Date dataDeleted;
    private Date createdAt;
    private Date updatedAt;
    
}