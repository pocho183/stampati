import { Component, OnInit } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { ButtonModule } from "primeng/button";
import { CardModule } from 'primeng/card';
import { ToastModule } from 'primeng/toast';
import { ListboxModule } from 'primeng/listbox';
import { TooltipModule } from 'primeng/tooltip';

import { ConfirmationService, MessageService } from 'primeng/api';

interface City {
    name: string,
    code: string
}

@Component({
	standalone: true,
	selector: 'relatori',
	imports: [ButtonModule, FormsModule, ReactiveFormsModule, CardModule, ToastModule, ListboxModule, TooltipModule],
	templateUrl: './relatori.component.html',
	styleUrl: './relatori.component.css'
})
export class RelatoriComponent implements OnInit {
    cities!: City[];

    selectedCity!: City;

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