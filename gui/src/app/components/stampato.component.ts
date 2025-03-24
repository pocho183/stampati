import { Component, OnInit, HostListener } from "@angular/core";
import { SplitterModule } from 'primeng/splitter';
import { RicercaComponent } from './ricerca/ricerca.component';
import { BarcodeComponent } from './barcode/barcode.component';
import { FrontespizioComponent } from "./frontespizio/frontespizio.component";
import { RelatoriComponent } from './relatori/relatori.component';
import { PresentazioneComponent } from "./presentazione/presentazione.component";
import { StampatoIdModel, StampatoModel } from "app/models/stampato.model";
import { ButtonModule } from 'primeng/button';
import { NgIf } from '@angular/common';
import { StampatoService } from "app/services/stampato.service";
import { DialogService, DynamicDialogRef } from "primeng/dynamicdialog";
import { MessageService } from "primeng/api";
import { AnswerFooterComponent } from "./dialog/dialog.answer.footer.component";
import { DialogAnswerComponent } from "./dialog/dialog.answer.component";
import { ToastModule } from 'primeng/toast';
import * as _ from 'lodash';

@Component({
	standalone: true,
	selector: 'stampato',
	imports: [SplitterModule, RicercaComponent, BarcodeComponent, FrontespizioComponent, RelatoriComponent, 
		PresentazioneComponent, ButtonModule, NgIf, ToastModule],
	providers: [StampatoService, MessageService, DialogService],
	templateUrl: './stampato.component.html',
	styleUrl: './stampato.component.css'
})
export class StampatoComponent implements OnInit {

	stampatoId: StampatoIdModel = new StampatoIdModel();
	stampato: StampatoModel = new StampatoModel(this.stampatoId);
	originalStampato: StampatoModel;
	private ref: DynamicDialogRef | undefined;
	
	constructor(private stampatoService: StampatoService,
			private dialogService: DialogService,
			private messageService: MessageService) { }
	
    ngOnInit() { 
		this.originalStampato = _.cloneDeep(this.stampato);
	}
	
	hasUnsavedChanges(): boolean {
	    return !_.isEqual(this.originalStampato, this.stampato);
	}
	
	confirmUnsavedChanges(): boolean {
		if (this.hasUnsavedChanges() && !confirm('Le modifiche apportate potrebbero non essere salvate. Vuoi continuare ?'))
			return false;
		return true;
	}
	
	// TODO: Togliere ed usare CanDeactive
	// Check: close page and refresh page
	@HostListener('window:beforeunload', ['$event'])
	onBeforeUnload(event: any) {
	    if (this.hasUnsavedChanges())
	    	event.returnValue = 'Le modifiche apportate potrebbero non essere salvate.';
	}
	
	updateStampato(newStampato: StampatoModel) {
		if (!this.confirmUnsavedChanges()) return;
	    this.stampato = { ...newStampato };
		this.originalStampato = _.cloneDeep(this.stampato);
	}
	
	rigonero() {
		if (!this.confirmUnsavedChanges()) return;
		this.ref = this.dialogService.open(DialogAnswerComponent, {
			header: 'Rigo nero', width: '20%', height: '20%', modal: true, contentStyle: { overflow: 'auto', paddingBottom: '1px' }, 
			data: { text: 'Confermi di creare il rigo nero ?' },
			templates: { footer: AnswerFooterComponent },
			baseZIndex: 10000, closable: true });
		this.ref.onClose.subscribe((answer: boolean) => {
			if (answer) {
				this.stampatoService.rigonero(this.stampato).subscribe({
					next: (rigonero) => {
				   		this.stampato = rigonero;
						this.originalStampato = _.cloneDeep(this.stampato);
						this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Stampato con rigo nero salvato correttamente !' });
					},
					error: (err) => { this.messageService.add({ severity: 'error', summary: 'Error', detail: 'La creazione del rigo nero è fallita!' }); }
				});
			}
		});	
	}
	
	errata(stampato: StampatoModel) {
		if (!this.confirmUnsavedChanges()) return;
		this.ref = this.dialogService.open(DialogAnswerComponent, {
			header: 'Errata Corrige', width: '30%', height: '30%', modal: true, contentStyle: { overflow: 'auto', paddingBottom: '1px' }, 
			data: { text: 'Confermi di creare una errata corrige ?'},
			templates: { footer: AnswerFooterComponent },
			baseZIndex: 10000, closable: true });
		this.ref.onClose.subscribe((answer: boolean) => {
			if (answer) {
				this.stampatoService.errata(stampato).subscribe({
				    next: (res) => {
					    this.stampato = res;
				     	this.messageService.add({ severity: 'success', summary: 'Errata corrige creata correttamente' });
				    },
				    error: (err) => { this.messageService.add({ severity: 'error', summary: 'Errore durante la creazione dell\'errata corrige' }); }
				});
			}
		});
	}
	
	new() {
		if (!this.confirmUnsavedChanges()) return;
		this.ref = this.dialogService.open(DialogAnswerComponent, {
			header: 'Nuovo Stampato', width: '20%', height: '20%', modal: true, contentStyle: { overflow: 'auto', paddingBottom: '1px' }, 
			data: { text: 'Confermi di creare un nuovo stampato ?' },
			templates: { footer: AnswerFooterComponent },
			baseZIndex: 10000, closable: true });
		this.ref.onClose.subscribe((answer: boolean) => {
			if (answer) {
				let stampatoId = new StampatoIdModel();
				this.stampato = new StampatoModel(stampatoId);
			}
		});		
	}
	
	save(stampato: StampatoModel) {
		if(this.stampato.id.barcode != null) {
			this.stampatoService.save(stampato).subscribe({
		       	next: (savedStampato) => {
			     	this.stampato = savedStampato;
					this.originalStampato = _.cloneDeep(this.stampato);
					this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Stampato salvato!' });
			    },
			    error: (err) => { this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Salvataggio fallito!' }); }
			});
		} else {
			console.error("Errore: Codice a barre mancante !");
			this.messageService.add({ severity: 'error', sticky: true, summary: 'Errore', detail: 'Codice a barre mancante !' });			    		    
		}
	}
		
	publish(stampato: StampatoModel) {
		if(!this.confirmUnsavedChanges()) return;
		this.stampatoService.publish(stampato).subscribe({
			next: (publishedStampato) => {
				this.stampato = publishedStampato;
				this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Stampato pubblicato!' });
			},
		    error: (err) => { this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Pubblicazione fallita!' }); }
		});
	}
	
	unpublish(stampato: StampatoModel) {
		if(!this.confirmUnsavedChanges()) return;
		this.stampatoService.unpublish(stampato).subscribe({
			next: (unpublishedStampato) => {
				this.stampato = unpublishedStampato;
				this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Stampato non pubblicato!' });
			},
			error: (err) => { this.messageService.add({ severity: 'error', summary: 'Error', detail: 'L\'operazione di rendere lo stampato non pubblico fallita!' }); }
		});
	}
		
	delete(stampato: StampatoModel) {
		if (!this.confirmUnsavedChanges()) return;
		this.ref = this.dialogService.open(DialogAnswerComponent, {
			header: 'Cancella Stampato', width: '30%', height: '30%', modal: true, contentStyle: { overflow: 'auto', paddingBottom: '1px' }, 
			data: { text: 'Confermi di cancellare lo stampato con BARCODE: ' + stampato.id.barcode + ' ?'},
			templates: { footer: AnswerFooterComponent },
			baseZIndex: 10000, closable: true });
		this.ref.onClose.subscribe((answer: boolean) => {
			if (answer) {
				this.stampatoService.delete(stampato).subscribe({
				    next: (res) => {
					    this.stampato = { ...res };
				     	this.messageService.add({ severity: 'success', summary: 'Stampato cancellato correttamente' });
				    },
				    error: (err) => { this.messageService.add({ severity: 'error', summary: 'Errore durante la cancellazione dello stampato' }); }
				});
			}
		});
	}
		
	restore(stampato: StampatoModel) {
		if (!this.confirmUnsavedChanges()) return;
		this.ref = this.dialogService.open(DialogAnswerComponent, {
			header: 'Ripristina Stampato', width: '30%', height: '30%', modal: true, contentStyle: { overflow: 'auto', paddingBottom: '1px' }, 
			data: { text: 'Confermi di ripristinare lo stampato con BARCODE: ' + stampato.id.barcode + ' ?'},
			templates: { footer: AnswerFooterComponent },
			baseZIndex: 10000, closable: true });
		this.ref.onClose.subscribe((answer: boolean) => {
			if (answer) {
				this.stampatoService.restore(stampato).subscribe({
					next: (res) => {
						this.stampato = { ...res };
						this.messageService.add({ severity: 'success', summary: 'Stampato ripristinato correttamente' });
					},
					error: (err) => { this.messageService.add({ severity: 'error', summary: 'Errore durante il ripristino dello stampato' }); }
				});
			}
		});
	}

}