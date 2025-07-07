package it.camera.stampati.model;

import java.util.SortedSet;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StampatoFelModel {

	private Long id;
	private Long codiceEstremiAttoPdl;
	private String titolo;
	private String numeroAtto;
	private SortedSet<RelatoreCommissioneModel> relatori;
	
}
