package it.camera.stampati.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import it.camera.stampati.enumerator.StampatoFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StampatoModel {
	
	@NotNull
	private StampatoIdModel id;
	private Boolean errataCorrige;
	private Boolean rigoNero;
    private Boolean pdfPresente;
    private Boolean htmlPresente;
    private String numeroAtto;
    private String nomeFrontespizio;
    private String nomeFile;
    private String numeriPDL;
    private Integer pagine;
    private String lettera;
    private Boolean testoUnificato;
    private Boolean rinvioInCommissione;
    private Boolean relazioneMagg;
    private String relazioneMin;
    private String suffisso;
    private String denominazioneStampato;
    private String tipoPresentazione;
    private Date dataPresentazione;
    private Date presentazioneOrale;
    private Date dataStampa;
    private String titolo;
    private StampatoFormat format;
    private Date dataDeleted;
    private Date createdAt;
    private Date updatedAt;
    private List<StampatoFelModel> stampatiFel;
    private List<StampatoRelatoreModel> stampatiRelatori;
    
    public String getBarcode() {
        return id != null ? id.getBarcode() : null;
    }
}