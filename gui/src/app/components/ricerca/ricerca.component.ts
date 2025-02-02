import { Component, OnInit } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { ButtonModule } from "primeng/button";
import { IftaLabelModule } from "primeng/iftalabel";
import { InputTextModule } from "primeng/inputtext";
import { InputGroupModule } from 'primeng/inputgroup';
import { InputGroupAddonModule } from 'primeng/inputgroupaddon';
import { SelectModule } from 'primeng/select';
import { CardModule } from 'primeng/card';
import { FileUploadModule } from 'primeng/fileupload';
import { ToastModule } from 'primeng/toast';
import { DialogService, DynamicDialogRef, DynamicDialogModule } from 'primeng/dynamicdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { DialogRicercaComponent } from 'app/components/ricerca/dialog.ricerca.component'
import { TypographyToProcessModel } from "app/models/typography.model";

@Component({
	standalone: true,
	selector: 'ricerca',
	imports: [IftaLabelModule, InputTextModule, ButtonModule, FormsModule, ReactiveFormsModule, 
		SelectModule, InputGroupModule, InputGroupAddonModule, CardModule, FileUploadModule, ToastModule,
		DynamicDialogModule],
	providers: [DialogService, MessageService],
	templateUrl: './ricerca.component.html',
	styleUrl: './ricerca.component.css'
})
export class RicercaComponent implements OnInit {

	private ref: DynamicDialogRef | undefined;
	
	constructor(
		private dialogService: DialogService,
		private messageService: MessageService,
		private confirmationService: ConfirmationService) {}
	
	ngOnInit() {
			
	}

	showDialog() {
		this.ref = this.dialogService.open(DialogRicercaComponent, {header: 'Carica Stampato da Elaborare', width: '40%', height: '60%',
			modal: true, contentStyle: { overflow: 'auto' }, baseZIndex: 10000, closable: true });
	    this.ref.onClose.subscribe((stampato: TypographyToProcessModel) => {
	    	if (stampato) {
	        	this.messageService.add({ severity: 'info', summary: 'Stampato caricato', detail: stampato.barcode });
	        }
	    });
	}
		

}