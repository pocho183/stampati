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
	private SortedSet<PdlAssegnazioneModel> assegnazione;
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
