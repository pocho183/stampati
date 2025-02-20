import { Component } from '@angular/core';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { TableModule } from 'primeng/table';

@Component({
	standalone: true,
	selector: 'error-message',
	imports: [TableModule],
	templateUrl: './error-message.component.html',
	styleUrls: ['./error-message.component.css']
})
export class ErrorMessageComponent {

	errors: any[] = [];
	alertMessage: String;

	constructor(public ref: DynamicDialogRef, public config: DynamicDialogConfig) { }

	ngOnInit() {
	    if (this.config.data?.blocked === true || this.config.data?.blocked === false) {
	        this.alertMessage = "Errore applicativo";
	        this.errors = this.config.data.errors;
	    } else {
	        this.alertMessage = "Errore di sistema";
	        this.errors = [{ message: "ATTENZIONE: contattare il supporto"}];
	    }
	}

}