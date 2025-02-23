import { Component, OnInit } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { DialogModule } from 'primeng/dialog';
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';

@Component({
	standalone: true,
	selector: 'dialog-answer',
	imports: [FormsModule, ReactiveFormsModule, DialogModule, ButtonModule, ToastModule ],
	providers: [ ],
	templateUrl: './dialog.answer.component.html',
	styleUrl: './dialog.answer.component.css'
})
export class DialogAnswerComponent implements OnInit {
	
	data: any;
	
	constructor(private dialogService: DialogService,
		public config: DynamicDialogConfig,
		private ref: DynamicDialogRef) {}

	ngOnInit() { 
		this.data = this.config.data;
	}

}