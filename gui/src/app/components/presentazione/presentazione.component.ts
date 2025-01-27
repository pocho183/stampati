import { Component, OnInit } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { ButtonModule } from "primeng/button";
import { CardModule } from 'primeng/card';
import { ToastModule } from 'primeng/toast';
import { InputTextModule } from 'primeng/inputtext';
import { DatePickerModule } from 'primeng/datepicker';
import { SelectModule } from 'primeng/select';

import { ConfirmationService, MessageService } from 'primeng/api';

interface City {
    name: string;
    code: string;
}


@Component({
	standalone: true,
	selector: 'presentazione',
	imports: [ButtonModule, FormsModule, ReactiveFormsModule, CardModule, ToastModule, InputTextModule, DatePickerModule,
		SelectModule
	],
	templateUrl: './presentazione.component.html',
	styleUrl: './presentazione.component.css'
})
export class PresentazioneComponent implements OnInit {
    cities: City[] | undefined;

    selectedCity: City | undefined;

    ngOnInit() {
        this.cities = [
            { name: 'New York', code: 'NY' },
            { name: 'Rome', code: 'RM' },
            { name: 'London', code: 'LDN' },
            { name: 'Istanbul', code: 'IST' },
            { name: 'Paris', code: 'PRS' }
        ];
    }
}