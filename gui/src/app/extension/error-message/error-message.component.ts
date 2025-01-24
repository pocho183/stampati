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
	defaultMessage = "Errore di sistema";
	alertMessage: String;

	constructor(public ref: DynamicDialogRef, public config: DynamicDialogConfig) { }

	ngOnInit() {
		if (this.config.data.blocked === true) {
			this.alertMessage = "Errore di sistema";
			this.errors = this.config.data.errors;
		} else if (this.config.data.blocked === false) {
			this.alertMessage = "Errore di sistema";
			this.errors = this.config.data.errors;
		} else {
			this.alertMessage = this.config.data[0].context;
			this.config.data.forEach(element => {
				this.errors.push(element);
			});
		}
	}
}