package it.camera.stampati.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StampatiModel implements Serializable {

	private static final long serialVersionUID = 7310614034271207697L;

	private String codiceabarre;
	private String numeroatto;
	private String numeripdl;
	private String pagine;
	private String navette;
	private String lettera;
	private Character relazionemagg;
	private String relazioneminor;
	private String suffisso;
	private String denominsuffisso;
	private Character prestrasm;
	private Date data;
	private Character relazioneorale;
	private Date datastampa;
	private Integer erco;
	private String barreerco;
	private Integer rigonero;
	private String barrerigonero;
	private Date datacancellazione;
	private Boolean pdfAssente;
	private Date timestamp;
	private Date dataTrasferimento;
	private Date dateCreated;
	private Long idlegislatura;
	private String codiceesposizione;
	private String titolopdl;
	private Date datarichiestaaut;
	private Integer codicetipoattoallegato;
	private Integer numvaltab;
	private Integer codicevalstralciotab;
	private Integer codicevalparte;
	private Integer codicevalvolume;
	private Integer codicevaltomo;
	private Integer numvalannesso;
	private Character descavalletteraallegato;
	private Integer numvalallegato;
	private Integer numvalresiduo;
	private BigInteger progressivo;
	private String nomefile;
	private Boolean rinvio;
	private String linkPDF;
}
