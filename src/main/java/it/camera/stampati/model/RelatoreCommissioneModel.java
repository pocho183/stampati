package it.camera.stampati.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import lombok.Getter;

@Getter
public class RelatoreCommissioneModel implements Serializable, Comparable<RelatoreCommissioneModel> {

	private static final long serialVersionUID = 4318366164226806364L;

	private Long codicecommissionerelatore;
	private Long codicetiposequenza;
	private Date datainizioincarico;
	private Date datafineincarico;
	private Date dataprelievopfis;
	private String descbrnumerorelazineminor;
	private String desccognome;
	private String descnome;
	private String descmaltro;
	private String descmnote;
	private String descnominativoomonimia;
	private Long idPersona;
	private Long numeroordinerelatore;
	private Boolean switchomonimia;
	private Boolean switchrelatoremaggioranza;
	private CommissioneModelFel commissione;
	
	@Override
	public int compareTo(RelatoreCommissioneModel rel) {
		if(rel == null)
			return -1;
		if(commissione == null && rel.commissione != null)
			return 1;
		if(commissione != null && rel.commissione == null)
			return -1;
		int result = 0;
		if (commissione != null && rel.commissione != null)
			result = commissione.getNumerokanagcamera().compareTo(rel.commissione.getNumerokanagcamera());		
		if(result == 0 && codicecommissionerelatore == null && rel.codicecommissionerelatore == null)
			return -1;
		if(result == 0 && codicecommissionerelatore == null)
			return 1;
		if(result == 0 && rel.codicecommissionerelatore == null)
			return -1;
		if(result == 0 && codicecommissionerelatore != null && rel.codicecommissionerelatore != null)
			return codicecommissionerelatore.compareTo(rel.codicecommissionerelatore);
		return result;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(codicecommissionerelatore, codicetiposequenza, commissione, datafineincarico, datainizioincarico, dataprelievopfis, descbrnumerorelazineminor, desccognome, descmaltro, descmnote, descnome, descnominativoomonimia, idPersona, numeroordinerelatore, switchomonimia, switchrelatoremaggioranza);
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(!(obj instanceof RelatoreCommissioneModel))
			return false;
		RelatoreCommissioneModel other = (RelatoreCommissioneModel) obj;
		return Objects.equals(codicecommissionerelatore, other.codicecommissionerelatore) && Objects.equals(codicetiposequenza, other.codicetiposequenza) && Objects.equals(commissione, other.commissione) && Objects.equals(datafineincarico, other.datafineincarico) && Objects.equals(datainizioincarico, other.datainizioincarico) && Objects.equals(dataprelievopfis, other.dataprelievopfis) && Objects.equals(descbrnumerorelazineminor, other.descbrnumerorelazineminor) && Objects.equals(desccognome, other.desccognome) && Objects.equals(descmaltro, other.descmaltro) && Objects.equals(descmnote, other.descmnote) && Objects.equals(descnome, other.descnome) && Objects.equals(descnominativoomonimia, other.descnominativoomonimia) && Objects.equals(idPersona, other.idPersona) && Objects.equals(numeroordinerelatore, other.numeroordinerelatore) && Objects.equals(switchomonimia, other.switchomonimia) && Objects.equals(switchrelatoremaggioranza, other.switchrelatoremaggioranza);
	}

}
