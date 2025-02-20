package it.camera.stampati.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StampatoIdModel implements Serializable {

    private static final long serialVersionUID = -3337522152624921093L;
	private String barcode;
    private String legislatura;

    public StampatoIdModel() {}

    public StampatoIdModel(String barcode, String legislatura) {
        this.barcode = barcode;
        this.legislatura = legislatura;
    }
}
