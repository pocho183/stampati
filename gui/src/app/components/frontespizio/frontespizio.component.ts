import { Component, OnInit, Input } from "@angular/core";
import { TableModule } from "primeng/table";
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { FormsModule } from "@angular/forms";
import { TooltipModule } from 'primeng/tooltip';
import { InputGroupModule } from 'primeng/inputgroup';
import { InputGroupAddonModule } from 'primeng/inputgroupaddon';
import { IftaLabelModule } from 'primeng/iftalabel';
import { CheckboxModule } from 'primeng/checkbox';
import { SelectModule } from 'primeng/select';
import { InputTextModule } from 'primeng/inputtext';
import { ToastModule } from 'primeng/toast';
import { DialogService, DynamicDialogRef, DynamicDialogModule } from 'primeng/dynamicdialog';
import { MessageService } from 'primeng/api';
import { DialogFrontespizioComponent } from "./dialog.frontespizio.component";
import { StampatoModel } from "app/models/stampato.model";
import { UtilityService } from "app/services/utility.service";
import { LegislaturaModel } from "app/models/legislatura.model";

@Component({
	standalone: true,
	selector: 'frontespizio',
	imports: [TableModule, CardModule, ButtonModule, FormsModule, TooltipModule, InputGroupModule,
		InputGroupAddonModule, IftaLabelModule, CheckboxModule, SelectModule, InputTextModule, 
	ToastModule, DynamicDialogModule],
	providers: [DialogService, MessageService],
	templateUrl: './frontespizio.component.html',
	styleUrl: './frontespizio.component.css'
})
export class FrontespizioComponent implements OnInit {
	
	@Input() stampato: StampatoModel;
	private ref: DynamicDialogRef | undefined;
	letters = [ { label: 'A', value: 'A' }, { label: 'C', value: 'C' }, { label: 'E', value: 'E' },
		{ label: 'G', value: 'G' }, { label: 'I', value: 'I' }, { label: 'M', value: 'M' },
	    { label: 'O', value: 'O' }, { label: 'Q', value: 'Q' }, { label: 'S', value: 'S' },
	    { label: 'U', value: 'U' }, { label: 'Z', value: 'Z' }   ];
	navettes = [ { label: 'B', value: 'B' }, { label: 'D', value: 'D' }, { label: 'F', value: 'F' },
		{ label: 'H', value: 'H' }, { label: 'L', value: 'L' }, { label: 'N', value: 'N' },
		{ label: 'P', value: 'P' }, { label: 'R', value: 'R' }, { label: 'T', value: 'T' },
		{ label: 'V', value: 'V' } ];
	minoranza = [ { label: 'bis', value: 'bis' }, { label: 'ter', value: 'ter' }, { label: 'quater', value: 'quater' },
		{ label: 'quinquies', value: 'quinquies' }, { label: 'sexies', value: 'sexies' }, { label: 'septies', value: 'septies' },
	    { label: 'octies', value: 'octies' }, { label: 'novies', value: 'novies' }, { label: 'decies', value: 'decies' },
		{ label: 'undecies', value: 'undecies' }, { label: 'duodecies', value: 'duodecies' }, { label: 'terdecies', value: 'terdecies' },
		{ label: 'quaterdecies', value: 'quaterdecies' }, { label: 'quindecies', value: 'quindecies' }, { label: 'sedecies', value: 'sedecies' },
		{ label: 'septies decies', value: 'septiesdecies' }, { label: 'duodevicies', value: 'duodevicies' }, { label: 'undevicies', value: 'undevicies' },
		{ label: 'vicies', value: 'vicies' }, { label: 'semel et vicies', value: 'semeletvicies' }, { label: 'bis et vicies', value: 'bisetvicies' },
		{ label: 'ter et vicies', value: 'teretvicies' }, { label: 'quater et vicies', value: 'quateretvicies' }, { label: 'quinquies et vicies', value: 'quinquiesetvicies' },
		{ label: 'sexies et vicies', value: 'sexiesetvicies' }, { label: 'septies et vicies', value: 'septiesetvicies' }, { label: 'octies et vicies', value: 'octiesetvicies' },
		{ label: 'novies et vicies', value: 'noviesetvicies' }, { label: 'tricies', value: 'tricies' }, { label: 'semel et tricies', value: 'semelettricies' },
		{ label: 'bis et tricies', value: 'bisettricies' }, { label: 'ter et tricies', value: 'terettricies' }, { label: 'quater et tricies', value: 'quaterettricies' },
		{ label: 'quinquies et tricies', value: 'quinquiesettricies' }, { label: 'sexies et tricies', value: 'sexiesettricies' }, { label: 'septies et tricies', value: 'septiesettricies' },
		{ label: 'octies et tricies', value: 'octiesettricies' }, { label: 'novies et tricies', value: 'noviesettricies' }, { label: 'quadragies', value: 'quadragies' },
	    { label: 'semel et quadragies', value: 'semeletquadragies' }, { label: 'bis et quadragies', value: 'bisetquadragies' }, { label: 'teretquadragies', value: 'ter et quadragies' },
		{ label: 'quater et quadragies', value: 'quateretquadragies' }, { label: 'quinquiesetquadragies', value: 'quinquies et quadragies' }, { label: 'sexies et quadragies', value: 'sexiesetquadragies' },
		{ label: 'septies et quadragies', value: 'septiesetquadragies' }, { label: 'octies et quadragies', value: 'octiesetquadragies' }, { label: 'novies et quadragies', value: 'noviesetquadragies' },
		{ label: 'quinquagies', value: 'quinquagies' } ];

	isDisabled: boolean = true;
	legislature: LegislaturaModel = null;
		
	constructor(private dialogService: DialogService,
		private utilityService: UtilityService,
		private messageService: MessageService) {}

    ngOnInit() {
		this.utilityService.getWorkingLegislature().subscribe((leg) => {
			this.legislature = leg;
		});
		this.updateNomeFrontespizio();
		this.updateFilename();
	}
	
	showDialog() {
		this.ref = this.dialogService.open(DialogFrontespizioComponent, {header: 'Atti Associati', width: '40%', height: '60%',
				modal: true, contentStyle: { overflow: 'auto' }, baseZIndex: 10000, closable: true });
		    this.ref.onClose.subscribe((atto: boolean) => {
		    	if (atto)
		        	this.messageService.add({ severity: 'info', summary: 'Atto associato' });
		});
	}
	
	onChangeLetter(event: any) {
		this.updateNomeFrontespizio();
		this.updateFilename();
	}
	
	onChangeNavette(event: any) {
		this.updateNomeFrontespizio();
		this.updateFilename();
	}
	
	onChangeRinvio(event: any) {
		this.updateNomeFrontespizio();
		this.updateFilename();
	}
	
	onChangeRelMin(event: any) {
		this.updateNomeFrontespizio();
		this.updateFilename();
	}
	
	onChangeNumeriPDL(event: any) {
		this.updateNomeFrontespizio();
		this.updateFilename();
	}
	
	updateNomeFrontespizio(): void {
		if(this.stampato.numeriPDL) {
			let stralcio = this.utilityService.getStralcio(this.stampato.numeriPDL);   
			let numeriPDL = stralcio != null ? this.stampato.numeriPDL.split("-")[0] + "-" + stralcio : this.stampato.numeriPDL.split("-")[0]
			//let numeriPDL = this.stampato.numeriPDL ? this.stampato.numeriPDL.split('-')[0] : '';
			let frontespizio = numeriPDL;
			if (this.stampato.navette?.trim()) frontespizio += '-' + this.stampato.navette;
			let relazione = this.stampato.lettera ? "-" + this.stampato.lettera : '';
			if (this.stampato.rinvioInCommissione) relazione += '/R';
			if (relazione.trim()) frontespizio = frontespizio + relazione;
			if(this.stampato.relazioneMin?.trim()) frontespizio += '_' + this.stampato.relazioneMin;				
			if(this.stampato.suffisso?.trim())  frontespizio += "-" + this.stampato.suffisso;
			this.stampato.nomeFrontespizio = frontespizio;
		}
	}
	
	updateFilename() :void {
		if(!this.stampato || !this.stampato.id || this.stampato.id.barcode != null) {
			let stralcio = this.utilityService.getStralcio(this.stampato.numeriPDL);   
			let numeriPDL = stralcio != null ? this.stampato.numeriPDL.split("-")[0] + "-" + stralcio : this.stampato.numeriPDL.split("-")[0]
			let type = this.extractTypeStampato(this.stampato.id.barcode);
			//let numeriPDL = this.stampato.numeriPDL ? this.stampato.numeriPDL.split('-')[0] : 'unknown';
			let filename = 'leg.' + this.legislature.legArabo + "." + (type ? type : '') + '.camera.' + numeriPDL;
			if (this.stampato.navette?.trim()) filename += '-' + this.stampato.navette;
			let relazione = this.stampato.lettera ? this.stampato.lettera : '';
			if (this.stampato.rinvioInCommissione) relazione += 'R';
			if (relazione.trim()) filename = filename + '_' + relazione;
			if (this.stampato.relazioneMin?.trim()) filename += '_' + this.stampato.relazioneMin;						 
			this.stampato.nomeFile = filename + '.' + this.stampato.id.barcode;
		}
	}
	
	onClearLetter() {
		this.stampato.relazioneMin = null;
		this.stampato.relazioneMagg = false;
		this.stampato.rinvioInCommissione = false;
		this.updateNomeFrontespizio();
		this.updateFilename();
	}
	
	extractTypeStampato(input: string) {
		const match = input.match(/^\d{2}(PDL|MSG|TU)/);
		return match ? match[1].toLowerCase() : null;
	}
	
}