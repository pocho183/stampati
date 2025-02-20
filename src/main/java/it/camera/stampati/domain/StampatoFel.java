package it.camera.stampati.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class StampatoFel {
	
	@Id
	private Long idFel;
	private String numeroAtto;
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "barcode", referencedColumnName = "barcode"),
        @JoinColumn(name = "legislatura", referencedColumnName = "legislatura")
    })
    private Stampato stampato;
}
