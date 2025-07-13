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
public class StampatoFel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long idFel;
	
	// Da fare, prendersi il valore di sequenza di efel
	//private Integer sequenza;
	private String numeroAtto;
	@ManyToOne
	@JoinColumns({ @JoinColumn(name = "barcode", referencedColumnName = "barcode"), @JoinColumn(name = "legislatura", referencedColumnName = "legislatura") })
	private Stampato stampato;
}
