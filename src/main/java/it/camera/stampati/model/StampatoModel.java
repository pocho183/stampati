package it.camera.stampati.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import it.camera.stampati.enumerator.StampatoFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StampatoModel {

	private StampatoIdModel id;
	private String barcode;
    private String legislatura;
    private Boolean pdfPresente;
    private Boolean htmlPresente;
    private String numeroAtto;
    private String nomeFrontespizio;
    private String nomeFile;
    private String numeriPDL;
    private Integer pagine;
    private String lettera;
    private Boolean rinvioInCommissione;
    private Boolean relazioneMagg;
    private String relazioneMin;
    private String suffisso;
    private String denominazioneStampato;
    private String tipoPresentazione;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dataPresentazione;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date presentazioneOrale;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dataStampa;
    private String titolo;
    private StampatoFormat format;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dataDeleted;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;
    private List<StampatoFelModel> stampatiFel;
    private List<StampatoRelatoreModel> stampatiRelatori;
    
}