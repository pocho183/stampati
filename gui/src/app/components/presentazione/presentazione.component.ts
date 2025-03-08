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

	constructor(private messageService: MessageService) {}
	
    ngOnInit() {
		this.updateNomeFrontespizio();
	}
	
	onChangeSuffisso(event: any) {
		this.updateNomeFrontespizio();
	}	
	
	updateNomeFrontespizio(): void {
		const trimValue = (value?: string): string => value?.trim() || "";
		const lettera = trimValue(this.stampato.lettera);
		const minoranza = trimValue(this.stampato.relazioneMin);
		const suffisso = trimValue(this.stampato.suffisso);
		const navette = trimValue(this.stampato.navette);
	    const numeriPDL = trimValue(this.stampato.numeriPDL);
	    const rinvio = this.stampato.rinvioInCommissione ? "/R" : "";
		const parts = [lettera, navette].filter(part => part.length > 0).join("-");
		const rest = [minoranza, suffisso].filter(part => part.length > 0).join("-");
		const finalPart = 
        	(parts.length > 0 ? parts : "") +
	        (rinvio.length > 0 ? rinvio : "") +
	        (rest.length > 0 ? (parts.length > 0 || rinvio.length > 0 ? `-${rest}` : rest) : "");
		this.stampato.nomeFrontespizio = numeriPDL + (finalPart.length > 0 ? `_${finalPart}` : "");
	}

}