import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { TrialService } from './services/trial.service'
import { ConfirmationService, MessageService } from 'primeng/api';

import { HttpEvent } from '@angular/common/http';

interface UploadEvent {
    originalEvent: HttpEvent<any>;
    files: File[];
}

import { Select } from 'primeng/select';
import { SelectItemGroup } from 'primeng/api';

interface City {
    name: string;
    code: string;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
	
    groupedCities: SelectItemGroup[] = [];

    selectedCity: City | undefined;

	constructor(
		private service: TrialService,
		private messageService: MessageService,
		private confirmationService: ConfirmationService) {}
		
	ngOnInit() {
		this.groupedCities = [
		            {
		                label: 'PDF',
		                value: 'pdf',
		                items: [
		                    { label: 'Berlin', value: 'Berlin' },
		                    { label: 'Frankfurt', value: 'Frankfurt' },
		                    { label: 'Hamburg', value: 'Hamburg' },
		                    { label: 'Munich', value: 'Munich' }
		                ]
		            }
		        ];
	    }
	
	callBE() {
		console.log("CIAOOO");
		this.service.trialCall().subscribe(response => {
			console.log("APP Component");
			console.log(response);
		}, error => { this.messageService.add({severity:'error', summary: 'Errore', detail:'Il server non risponde', closable: false, life: 2000}); });
	}
	
	onBasicUploadAuto(event: any) {
	    console.log('Upload Event:', event);
	    this.messageService.add({ severity: 'info', summary: 'Success', detail: 'File Uploaded with Auto Mode' });
	}
}
