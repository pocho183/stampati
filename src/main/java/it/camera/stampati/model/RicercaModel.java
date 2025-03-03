package it.camera.stampati.model;

import it.camera.stampati.domain.Stampato;
import it.esinware.mapping.annotation.FieldBinding;
import it.esinware.mapping.annotation.TypeBinding;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TypeBinding(binding = Stampato.class)
public class RicercaModel {

	@FieldBinding(binding = "id.barcode")
	private String barcode;
	@FieldBinding(binding = "id.legislatura")
    private String legislatura;
	private String numeriPDL;
    private Boolean errataCorrige;
	private Boolean rigoNero;
}
