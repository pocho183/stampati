package it.camera.stampati.model;

import java.io.Serializable;

import lombok.Getter;

@Getter
public class CommissioneModelFel implements Serializable {

	private static final long serialVersionUID = 1251893040473748155L;

	private String descgcommissionebreve;
	private Long numerokanagcamera;
	private String desccecommissione;
	private Long codicecommissione;
	private Long codicelegislatura;

}
