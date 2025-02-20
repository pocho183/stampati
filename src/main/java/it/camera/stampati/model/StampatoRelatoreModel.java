package it.camera.stampati.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StampatoRelatoreModel {

	private Long idPersona;
	private Boolean maggioranza;
	private Integer idCommissione;
	private String altro;
}
