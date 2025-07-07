export class StampatoFelModel {
	
	codiceEstremiAttoPdl: number;
	titolo: string;
	numeroAtto: string;
	relatori: FelRelatoreModel[];
}

export class FelRelatoreModel {
	
	commissione: CommissioneModelFel;
	desccognome: string;
	descnome: string;
	idPersona: number;
	switchomonimia: boolean;
}

export class CommissioneModelFel {
	
	descgcommissionebreve: string;
	numerokanagcamera: number;
	desccecommissione: string;
	codicecommissione: number;
	codicelegislatura: number;
}