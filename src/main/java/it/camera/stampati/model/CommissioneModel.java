package it.camera.stampati.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommissioneModel implements Comparable<CommissioneModel> {

	private String codiceTipografia;
	private Long aulId;
	private String denominazione;
	private Long legislatura;
	private String ramo;

	@JsonProperty(value = "isPermanent")
	public boolean isPermanent() {
		if(codiceTipografia == null) {
			return false;
		}
		int code = Integer.parseInt(codiceTipografia);
		return code < 15 && code > 0;
	}

	@Override
	public int compareTo(CommissioneModel o) {
		if(getAulId() == null || o.getAulId() == null) {
			return 0;
		}
		return getAulId().compareTo(o.getAulId());
	}
}

