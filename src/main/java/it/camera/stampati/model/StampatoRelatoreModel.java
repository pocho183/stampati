package it.camera.stampati.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StampatoRelatoreModel {

	private Long id;
	private Long idPersona;
	private String cognome;
	private Boolean maggioranza;
	private Integer idCommissione;
	private String altro;
}
