package it.camera.stampati.model;

import java.io.Serializable;
import java.util.Date;
import java.util.SortedSet;

import lombok.Getter;

@Getter
public class PdlAssegnazioneModel implements Serializable, Comparable<PdlAssegnazioneModel> {

	private Long codicepdlassegnazione;
	private Long codicesequenzaasstrasf;
	private Date dataassegnazione;
	private SortedSet<RelatoreCommissioneModel> relatori;
	
	@Override
	public int compareTo(PdlAssegnazioneModel o) {
		if(o == null || o.dataassegnazione == null)
			return -1;
		if(dataassegnazione == null)
			return 1;
		int result = dataassegnazione.compareTo(o.dataassegnazione);		
		if(result == 0 && codicepdlassegnazione == null && o.codicepdlassegnazione == null)
			return -1;
		if(result == 0 && codicepdlassegnazione == null)
			return 1;
		if(result == 0 && o.codicepdlassegnazione == null)
			return -1;
		if(result == 0 && codicepdlassegnazione != null && o.codicepdlassegnazione != null)
			return codicepdlassegnazione.compareTo(o.codicepdlassegnazione);
		return result;
	}
}
