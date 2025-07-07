import { Type } from "class-transformer";
import "reflect-metadata";
import { FelRelatoreModel } from "./stampato.fel.model";
import { StampatoRelatoreModel } from "./stampato.relatore.model";

export enum StampatoFormat {
  	PDF = 1,
	XHTML = 2,
	HTML = 3,
	EPUB = 4
}

export class StampatoIdModel {
	barcode: string;
	legislatura: number;
}

export class StampatoModel {
	@Type(() => StampatoIdModel)
	id: StampatoIdModel;
	codiceEstremiAttoPdl?: number;
	progressivo?: number;
	pubblicato?:boolean;
	errataCorrige: boolean;
	rigoNero: string;
	pdfPresente?: boolean;
	htmlPresente?: boolean;
	numeroAtto?: string;
	nomeFrontespizio?: string;
	nomeFile?: string;
	numeriPDL?: string;
	pagine?: number;
	lettera?: string;
	navette?: string;
	testoUnificato?:boolean;
	rinvioInCommissione?: boolean;
	relazioneMagg?: boolean;
	relazioneMin?: string;
	suffisso?: string;
	denominazioneStampato?: string;
	tipoPresentazione?: string;
	@Type(() => Date)
	dataPresentazione?: Date;
	@Type(() => Date)
	presentazioneOrale?: Date;
	@Type(() => Date)
	dataStampa?: Date;
	titolo?: string = '';
	titoloFel?: string = '';
	format?: StampatoFormat;
	@Type(() => Date)
	dataDeleted?: Date;
	@Type(() => Date)
	createdAt?: Date;
	@Type(() => Date)
	updatedAt?: Date;
	@Type(() => FelRelatoreModel)
	relatoriFel?: FelRelatoreModel[];
	@Type(() => StampatoRelatoreModel)
	stampatiRelatori?: StampatoRelatoreModel[];

	constructor(id: StampatoIdModel) {
		this.id = id;
	}
}