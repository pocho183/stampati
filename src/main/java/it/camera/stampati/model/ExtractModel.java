package it.camera.stampati.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;

public class ExtractModel implements Serializable {

	private static final long serialVersionUID = 5797348617033734957L;
	
	private String numero;
	private String link;
	@JsonFormat(pattern="dd-MM-yyyy")
	private Date pubDate;
	@JsonFormat(pattern="EEE, d MMM yyyy HH:mm:ss Z", timezone="Europe/Rome")
	private Date pubDateRSS;
	@JacksonXmlCData
	private String titolo;
	@JsonIgnore
	private String codiceabarre;
	@JsonIgnore
	private String numeroatto;
	@JsonIgnore
	private String navette;
	@JsonIgnore
	private String lettera;
	@JsonIgnore
	private String relazioneminor;
	@JsonIgnore
	private String suffisso;
	@JsonIgnore
	private Integer erco;
	@JsonIgnore
	private Integer rigonero;
	@JsonIgnore
	private Date dateCreated;
	@JsonIgnore
	private BigInteger idlegislatura;

	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public Date getPubDate() {
		return pubDate;
	}
	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}
	public Date getPubDateRSS() {
		return pubDateRSS;
	}
	public void setPubDateRSS(Date pubDateRSS) {
		this.pubDateRSS = pubDateRSS;
	}
	public String getTitolo() {
		return titolo;
	}
	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}
	public String getCodiceabarre() {
		return codiceabarre;
	}
	public void setCodiceabarre(String codiceabarre) {
		this.codiceabarre = codiceabarre;
	}
	public String getNumeroatto() {
		return numeroatto;
	}
	public void setNumeroatto(String numeroatto) {
		this.numeroatto = numeroatto;
	}
	public String getNavette() {
		return navette;
	}
	public void setNavette(String navette) {
		this.navette = navette;
	}
	public String getLettera() {
		return lettera;
	}
	public void setLettera(String lettera) {
		this.lettera = lettera;
	}
	public String getRelazioneminor() {
		return relazioneminor;
	}
	public void setRelazioneminor(String relazioneminor) {
		this.relazioneminor = relazioneminor;
	}
	public String getSuffisso() {
		return suffisso;
	}
	public void setSuffisso(String suffisso) {
		this.suffisso = suffisso;
	}
	public Integer getErco() {
		return erco;
	}
	public void setErco(Integer erco) {
		this.erco = erco;
	}
	public Integer getRigonero() {
		return rigonero;
	}
	public void setRigonero(Integer rigonero) {
		this.rigonero = rigonero;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public BigInteger getIdlegislatura() {
		return idlegislatura;
	}
	public void setIdlegislatura(BigInteger idlegislatura) {
		this.idlegislatura = idlegislatura;
	}
}
