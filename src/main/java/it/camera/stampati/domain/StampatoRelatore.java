package it.camera.stampati.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class StampatoRelatore {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long idPersona;
	private Boolean maggioranza;
	private Integer idCommissione;
	private String altro;
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "barcode", referencedColumnName = "barcode"),
        @JoinColumn(name = "legislatura", referencedColumnName = "legislatura")
    })
    private Stampato stampato;
}
