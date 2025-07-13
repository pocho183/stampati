package it.camera.stampati.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import it.camera.stampati.enumerator.StampatoFormat;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Stampato {

	@EmbeddedId
	private StampatoId id;
	private Long progressivo;
	private Boolean pubblicato;
	private Boolean errataCorrige;
	private String rigoNero;
	private Boolean pdfPresente;
	private Boolean htmlPresente;
	private String numeroAtto;
	private String nomeFrontespizio;
	private String nomeFile;
	private String numeriPDL;
	private Integer pagine;
	private String lettera;
	private String navette;
	private Boolean testoUnificato;
	private Boolean rinvioInCommissione;
	private Boolean relazioneMagg;
	private String relazioneMin;
	private String suffisso;
	private String denominazioneStampato;
	private String tipoPresentazione;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
	private Date dataPresentazione;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
	private Date presentazioneOrale;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
	private Date dataStampa;
	@Column(length = 5000)
	private String titolo;
	@Enumerated(EnumType.STRING)
	private StampatoFormat format;
	@Column(name = "data_deleted")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataDeleted;
	@Column(name = "created_at", updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	@Column(name = "updated_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedAt;
	@OneToMany(mappedBy = "stampato", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<StampatoFel> stampatiFel;
	@OneToMany(mappedBy = "stampato", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<StampatoRelatore> stampatiRelatori;

	public void setStampatiFel(List<StampatoFel> stampatiFel) {
		this.stampatiFel = stampatiFel;
		if(this.stampatiFel != null) {
			this.stampatiFel.forEach(stmp -> stmp.setStampato(this));
		}
	}

	public void setStampatiRelatori(List<StampatoRelatore> stampatiRelatori) {
		this.stampatiRelatori = stampatiRelatori;
		if(this.stampatiRelatori != null) {
			this.stampatiRelatori.forEach(rel -> rel.setStampato(this));
		}
	}

	@PrePersist
	protected void onCreate() {
		createdAt = new Date();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = new Date();
	}

	@Override
	public String toString() {
		return "Stampato{" + "id='" + id + '\'' + ", format=" + format + ", dataDeleted=" + dataDeleted + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + '}';
	}
}