package it.camera.stampati.domain;

import java.util.Date;

import org.hibernate.annotations.Where;

import it.camera.stampati.enumerator.StampatoFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Stampato {

    @Id
    private String barcode;
    private String legislaturaId;
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

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }

    @Override
    public String toString() {
        return "Stampato{" + "barcode='" + barcode + '\'' + ", legislaturaId=" + legislaturaId + ", format=" + format + ", dataDeleted=" + dataDeleted + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + '}';
    }
}