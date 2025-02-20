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
	tipiPresentazione: { name: string; value: string }[] = [];
	selectedTipoPresentazione: string | null = null;

    ngOnInit() {
        this.tipiPresentazione = [{ name: 'Presentato', value: 'presentato' }, { name: 'Trasmesso', value: 'trasmesso' }, { name: 'Stralciato', value: 'stralciato' }];
    }
}