import { Component, OnInit, Input } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { ButtonModule } from "primeng/button";
import { CardModule } from 'primeng/card';
import { ToastModule } from 'primeng/toast';
import { InputTextModule } from 'primeng/inputtext';
import { DatePickerModule } from 'primeng/datepicker';
import { SelectModule } from 'primeng/select';
import { StampatoModel } from "app/models/stampato.model";
import { ConfirmationService, MessageService } from 'primeng/api';
import { UtilityService } from "app/services/utility.service";

@Component({
	standalone: true,
	selector: 'presentazione',
	imports: [ButtonModule, FormsModule, ReactiveFormsModule, CardModule, ToastModule, InputTextModule, 
		DatePickerModule, SelectModule ],
	templateUrl: './presentazione.component.html',
	styleUrl: './presentazione.component.css'
})
export class PresentazioneComponent implements OnInit {
    
	@Input() stampato: StampatoModel;
	tipiPresentazione = [{ label: 'Presentato', value: 'presentato' }, 
		{ label: 'Trasmesso', value: 'trasmesso' }, { label: 'Stralciato', value: 'stralciato' }];
	selectedTipoPresentazione: string | null = null;

	constructor(private messageService: MessageService,
		private utilityService: UtilityService
	) {}
	
    ngOnInit() {
		this.updateNomeFrontespizio();
	}
	
	onChangeSuffisso(event: any) {
		this.updateNomeFrontespizio();
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
			if(this.stampato.relazioneMin?.trim()) frontespizio += '-' + this.stampato.relazioneMin;				
			if(this.stampato.suffisso?.trim())  frontespizio += "-" + this.stampato.suffisso;
			this.stampato.nomeFrontespizio = frontespizio;
		}
	}

}