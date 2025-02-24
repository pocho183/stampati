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

    ngOnInit() { }
}