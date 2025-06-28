package it.camera.stampati.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LegislaturaModel implements Comparable<LegislaturaModel> {

	private String id;
	private String legRomano;
	private Integer legArabo;
	private String dataInizio;
	private String dataFine;

	@Override
	public int compareTo(LegislaturaModel l) {
		if(getLegArabo() == null || l.getLegArabo() == null) {
			return 0;
		}
		return getLegArabo().compareTo(l.getLegArabo());
	}
}
