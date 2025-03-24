package it.camera.stampati.domain;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class StampatoId implements Serializable {

	private static final long serialVersionUID = 8142557787638258187L;

	private String barcode;
	private String legislatura;

	public StampatoId() {
	}

	public StampatoId(String barcode, String legislatura) {
		this.barcode = barcode;
		this.legislatura = legislatura;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;
		StampatoId that = (StampatoId)o;
		return barcode.equals(that.barcode) && legislatura.equals(that.legislatura);
	}

	@Override
	public int hashCode() {
		return barcode.hashCode() + legislatura.hashCode();
	}
}
