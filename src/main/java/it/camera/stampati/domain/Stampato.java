package it.camera.stampati.domain;

import java.util.Date;
import java.util.List;

import it.camera.stampati.enumerator.StampatoFormat;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
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
    private Date dataPresentazione;
    private Date presentazioneOrale;
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
    private Date createdAt = new Date();
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @OneToMany(mappedBy = "stampato", cascade = CascadeType.ALL)
    private List<StampatoFel> stampatiFel;
    @OneToMany(mappedBy = "stampato", cascade = CascadeType.ALL)
    private List<StampatoRelatore> stampatiRelatori;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }

    @Override
    public String toString() {
        return "Stampato{" + "id='" + id + '\'' + ", format=" + format + ", dataDeleted=" + dataDeleted + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + '}';
    }
}