package it.camera.stampati.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Getter;

@Getter
public class PdlAbbinamentoModel implements Serializable {

	private static final long serialVersionUID = 6555244392888439664L;
	
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
