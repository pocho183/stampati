package it.camera.stampati.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StampatoRelatoreModel {

	private Long id;
	private Long idPersona;
	private String desccognome;
	private Boolean maggioranza;
	private Integer idCommissione;
}
