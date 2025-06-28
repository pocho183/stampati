package it.camera.stampati.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;

@Getter
public class PdlTitoloModel implements Serializable, Comparable<PdlTitoloModel> {

	private Long codice;
	private Long estremoAtto;
	private Long legislatura;
	private Long numeroSequenzaCoppia;
	private FelTipoTitoloattopdlModel tipologia;
	private String descMTitoloAttoPdl;
	private String descMNote;
	private Date dataTitolo;
	private Date dataAnnuncio;
	private Date dataFineTitolo;
	private Date dataPresuntoAnnuncio;

	@Override
	public int compareTo(PdlTitoloModel o) {
		if(o == null || o.numeroSequenzaCoppia == null)
			return -1;
		if(numeroSequenzaCoppia == null)
			return 1;
		int sequence = numeroSequenzaCoppia.compareTo(o.numeroSequenzaCoppia);
		if(sequence == 0) {
			if(dataFineTitolo == null && o.dataFineTitolo == null) {
				return tipologia.getCodice().intValue() == 2 ? 1 : -1;
			} else {
				if(dataFineTitolo == null)
					return 1;
				if(o.dataFineTitolo == null)
					return -1;
				int typeSequence = o.tipologia.getCodice().compareTo(tipologia.getCodice());
				if(typeSequence == 0)
					return dataFineTitolo.compareTo(o.dataFineTitolo);
				else
					return tipologia.getCodice().intValue() == 2 ? 1 : -1;
			}
		}
		return sequence;
	}
}
