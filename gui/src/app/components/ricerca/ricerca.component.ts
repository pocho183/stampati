import { Component, ElementRef, Input, OnInit, ViewChild, EventEmitter, Output } from "@angular/core";
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
import { UtilityService } from "app/services/utility.service";
import { LegislaturaModel } from "app/models/legislatura.model";
import { RicercaModel } from "app/models/ricerca.model";
import { DialogModule } from 'primeng/dialog';
import { NgIf } from '@angular/common';
import { StampatoModel } from "app/models/stampato.model";

@Component({
	standalone: true,
	selector: 'ricerca',
	imports: [IftaLabelModule, InputTextModule, ButtonModule, FormsModule, ReactiveFormsModule, 
		SelectModule, InputGroupModule, InputGroupAddonModule, CardModule, FileUploadModule, ToastModule,
		DynamicDialogModule, TableModule, FloatLabel, NgIf, DialogModule],
	providers: [DialogService, MessageService, RicercaService],
	templateUrl: './ricerca.component.html',
	styleUrl: './ricerca.component.css'
})
export class RicercaComponent implements OnInit {

	@Input() stampato: StampatoModel;
	@Output() stampatoChange = new EventEmitter<StampatoModel>();
	legislatures: LegislaturaModel[] = [];
	selectedLegislature: LegislaturaModel = null;
	@ViewChild('searchInput') searchInput: ElementRef;
	displayPopup: boolean = false;
	selectedResult: RicercaModel;
	isChanged: boolean = false;
	private ref: DynamicDialogRef | undefined;	
	results: RicercaModel[];

	constructor(
		private dialogService: DialogService,
		private ricercaService: RicercaService,
		private messageService: MessageService,
		private utilityService: UtilityService,
		private confirmationService: ConfirmationService) {}
	
	ngOnInit() {
		this.utilityService.fetchLegislature().subscribe(legs => {
			this.legislatures = legs;
			if (this.legislatures.length > 0)
				this.selectedLegislature = this.legislatures[0];
		});
	}

	showDialog() {
		this.ref = this.dialogService.open(DialogRicercaComponent, {header: 'Carica Stampato da Elaborare della LEGISLATURA: ' + this.legislatures[0].legArabo, width: '40%', height: '60%',
			modal: true, contentStyle: { overflow: 'auto' }, baseZIndex: 10000, closable: true, dismissableMask: true });
	    this.ref.onClose.subscribe((stampato: TypographyToProcessModel) => {
	    	if (stampato) {
	        	this.messageService.add({ severity: 'info', summary: 'Stampato caricato', detail: stampato.barcode });
	        }
	    });
	}
	
	search(textSearch: string) {
		if(textSearch != null && textSearch != '') {
			this.ricercaService.search(this.selectedLegislature.legArabo, textSearch).subscribe( res => {
				this.results = res;
			});
		}
	}
	
	clearInput() {
		if(this.searchInput && this.searchInput.nativeElement)
	    	this.searchInput.nativeElement.value = '';
		this.results = null;
	}
	
	openPopup(result: any) {
		this.selectedResult = result;
	    this.displayPopup = true;
	}
	
	loadStampato(selectedResult: RicercaModel) {
		this.displayPopup = false;
		this.ricercaService.load(selectedResult.legislatura, selectedResult.barcode).subscribe( res => {
			// New object reference
			this.stampato = { ...res };  
			this.stampatoChange.emit(this.stampato); 
		});
	}
	
	setStampato(newStampato: StampatoModel) {
	    this.stampato = newStampato;
	}
		
}