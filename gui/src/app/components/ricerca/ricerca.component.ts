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
import { TableModule } from 'primeng/table';
import { FloatLabel } from "primeng/floatlabel"
import { RicercaService } from "app/services/ricerca.service";

import { Product } from "app/models/product";

@Component({
	standalone: true,
	selector: 'ricerca',
	imports: [IftaLabelModule, InputTextModule, ButtonModule, FormsModule, ReactiveFormsModule, 
		SelectModule, InputGroupModule, InputGroupAddonModule, CardModule, FileUploadModule, ToastModule,
		DynamicDialogModule, TableModule, FloatLabel],
	providers: [DialogService, MessageService, RicercaService],
	templateUrl: './ricerca.component.html',
	styleUrl: './ricerca.component.css'
})
export class RicercaComponent implements OnInit {

	private ref: DynamicDialogRef | undefined;
	products!: Product[];

	
	constructor(
		private dialogService: DialogService,
		private ricercaService: RicercaService,
		private messageService: MessageService,
		private confirmationService: ConfirmationService) {}
	
	ngOnInit() {
		this.ricercaService.getProducts().then((data) => { this.products = data; });
	}

	showDialog() {
		this.ref = this.dialogService.open(DialogRicercaComponent, {header: 'Carica Stampato da Elaborare', width: '40%', height: '60%',
			modal: true, contentStyle: { overflow: 'auto' }, baseZIndex: 10000, closable: true, dismissableMask: true });
	    this.ref.onClose.subscribe((stampato: TypographyToProcessModel) => {
	    	if (stampato) {
	        	this.messageService.add({ severity: 'info', summary: 'Stampato caricato', detail: stampato.barcode });
	        }
	    });
	}
		
}