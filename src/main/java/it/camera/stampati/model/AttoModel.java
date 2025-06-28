package it.camera.stampati.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import lombok.Getter;

@Getter
public class AttoModel implements Serializable {

	private Long codiceestremiattopdl;
	private LegModel legislatura;
	private Long numeroattobase;
	private String descattoportante;
	private String numeroEsteso;
	private Date dataPresuntoAnnuncio;
	private Date dataannunciopdl;
	private PdlTitoloModel currentAnnounceTitle;
	private PdlTitoloModel currentPresentationTitle;
	private Date dataconclusione;
	private SortedSet<PdlTitoloModel> titoli;
	private List<PdlAbbinamentoModel> abbinamenti;

	public List<PdlAbbinamentoModel> getAbbinamenti() {
		if(abbinamenti != null) {
			abbinamenti.sort((o1, o2) -> {
				if(o1 == o2)
					return 0;
				if(o1 == null)
					return 1;
				if(o2 == null)
					return -1;
				if(o1.getCodicepdlabbinamento() == null || o2.getCodicepdlabbinamento() == null)
					return 1;
				return o1.getCodicepdlabbinamento().compareTo(o2.getCodicepdlabbinamento());
			});
		}
		return abbinamenti;
	}
	
}

@Getter
class LegModel implements Serializable {

	private Long codiceLegislatura;
	private String descrEstesa;
	private String descRomano;
	private Date dataInizio;
	private Date dataFine;

}

@Getter
class FelTipoTitoloattopdlModel implements Serializable {
	
	private Long codice;
	private String descrizione;
	
}

@Getter
class PdlAbbinamentoModel implements Serializable {

	private Long codicepdlabbinamento;
	private Long codiceestremiattopdl;
	private String attoPortante;
	private Long legislatura;
	private Date dataabbinamento;
	private String descmattiabbinati;
	private Boolean switchtestounificato;
	private Boolean tobedeleted = Boolean.FALSE;
	private List<PdlAbbinatiModel> pdlabbinati;

	public List<PdlAbbinatiModel> getPdlabbinati() {
		if(pdlabbinati != null) {
			pdlabbinati.sort((o1, o2) -> {
				if(o1 == o2)
					return 0;
				if(o1 == null)
					return 1;
				if(o2 == null)
					return -1;
				if(o1.getCodicetiposequenza() == null && o2.getCodicetiposequenza() == null)
					return 0;
				return o1.getCodicetiposequenza().compareTo(o2.getCodicetiposequenza());
			});
		}
		return pdlabbinati;
	}
}

@Getter
class PdlAbbinatiModel {
	
	private Long codiceattopdlabbinato;
	private Long codicelegislatura;
	private Long codiceattopdlportante;
	private String numeroattoportante;
	private Long numeroattobase;
	private String lasttitle;
	private Boolean switchtestounificato;
	private Long codicetiposequenza;
	private String descmnote;

}
