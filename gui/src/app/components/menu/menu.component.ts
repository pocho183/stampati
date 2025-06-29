import { Component, OnInit, EventEmitter, Output, ViewChild } from "@angular/core";
import { SplitterModule } from 'primeng/splitter';
import { BarcodeComponent } from '../barcode/barcode.component';
import { FrontespizioComponent } from "../frontespizio/frontespizio.component";
import { RelatoriComponent } from '../relatori/relatori.component';
import { PresentazioneComponent } from "../presentazione/presentazione.component";
import { StampatoIdModel, StampatoModel } from "app/models/stampato.model";
import { ButtonModule } from 'primeng/button';
import { StampatoService } from "app/services/stampato.service";
import { FelService } from "app/services/fel.service";
import { DialogService, DynamicDialogRef } from "primeng/dynamicdialog";
import { MessageService } from "primeng/api";
import { ToastModule } from 'primeng/toast';
import * as _ from 'lodash';
import { AnswerFooterComponent } from "../dialog/dialog.answer.footer.component";
import { DialogAnswerComponent } from "../dialog/dialog.answer.component";
import { ActivatedRoute } from '@angular/router';
import { RicercaService } from "app/services/ricerca.service";
import { NgIf } from '@angular/common';
import { Subscription } from "rxjs";
import { StampatoFelModel } from "app/models/stampato.fel.model";

@Component({
	standalone: true,
	selector: 'app-menu',
	imports: [SplitterModule, BarcodeComponent, FrontespizioComponent, RelatoriComponent, 
		PresentazioneComponent, ButtonModule, ToastModule, NgIf],
	providers: [StampatoService, FelService, MessageService, DialogService, RicercaService],
	templateUrl: './menu.component.html',
	styleUrl: './menu.component.css'
})
export class MenuComponent implements OnInit {

	@Output() stampatoChange = new EventEmitter<StampatoModel>();

	@ViewChild(BarcodeComponent)
	barcodeComponent: BarcodeComponent;
	barcode: string | null = null;
	private routeSub: Subscription;
	stampatoId: StampatoIdModel = new StampatoIdModel();
	stampato: StampatoModel = new StampatoModel(this.stampatoId);
	originalStampato: StampatoModel;
	efel: StampatoFelModel;
	private ref: DynamicDialogRef | undefined;
	
	constructor(private stampatoService: StampatoService,
		private route: ActivatedRoute,
		private felService: FelService,
		private ricercaService: RicercaService,
		private dialogService: DialogService,
		private messageService: MessageService) { }
	
    ngOnInit() {
		this.routeSub = this.route.paramMap.subscribe(paramMap => {
			this.barcode = paramMap.get('barcode');
			this.handleBarcodeChange(this.barcode);		
		});
	}
	
	ngOnDestroy(): void {
		if (this.routeSub)
	    	this.routeSub.unsubscribe();
	}

	handleBarcodeChange(barcode: string | null): void {
		if (barcode) {
			this.ricercaService.load(Number(barcode.substring(0, 2)), barcode).subscribe( res => {
				this.stampato = res;
				this.stampatoChange.emit(this.stampato);
				this.originalStampato = _.cloneDeep(this.stampato);
				// Call eFel
				if(this.stampato.numeroAtto)
					this.loadFel();
			});
	    }
	}
	
	hasUnsavedChanges(): boolean {
		const isEqual = _.isEqual(this.originalStampato, this.stampato);
		if (!isEqual) {
			console.log('Detected changes:', {
				original: this.originalStampato,
				current: this.stampato
			});
		}
		return !isEqual;
	}
		
	confirmUnsavedChanges(): Promise<boolean> {
		if (!this.hasUnsavedChanges()) return Promise.resolve(true);
		this.ref = this.dialogService.open(DialogAnswerComponent, { header: 'Attenzione', width: '30%', modal: true,
			data: { text: 'Hai delle modifiche non salvate. Vuoi continuare senza salvare?'},
			templates: { footer: AnswerFooterComponent }, baseZIndex: 10000, closable: false
		});
		return new Promise<boolean>((resolve) => { 
			this.ref.onClose.subscribe((answer: boolean) => { resolve(answer); });
		});
	}
		
	rigonero() {
		this.confirmUnsavedChanges().then((confirmed: boolean) => {
			if (!confirmed) return;
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
						error: (err) => { 
							let errorMessage = err?.message || 'La creazione del rigo nero è fallita!';
							this.messageService.add({ severity: 'error', summary: 'Errore', detail: errorMessage });
						}
					});
				}
			});	
		});
	}
		
	errata(stampato: StampatoModel) {
		this.confirmUnsavedChanges().then((confirmed: boolean) => {
			if (!confirmed) return;
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
							this.originalStampato = _.cloneDeep(this.stampato);
						   	this.messageService.add({ severity: 'success', summary: 'Errata corrige creata correttamente' });
						},
					    error: (err) => { 
							let errorMessage = err?.message || 'Errore durante la creazione dell\'errata corrige';
							this.messageService.add({ severity: 'error', summary: 'Errore', detail: errorMessage });
						}
					});
				}
			});
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
				error: (err) => { 
					let errorMessage = err?.message || 'Salvataggio fallito!';
					this.messageService.add({ severity: 'error', summary: 'Errore', detail: errorMessage });
				}
			});
		} else {
			console.error("Errore: Codice a barre mancante !");
			this.messageService.add({ severity: 'error', sticky: true, summary: 'Errore', detail: 'Codice a barre mancante !' });			    		    
		}
	}
			
	publish(stampato: StampatoModel) {
		this.confirmUnsavedChanges().then((confirmed: boolean) => {
			if (!confirmed) return;
			if(this.stampato.pdfPresente || this.stampato.htmlPresente) {
				this.stampatoService.publish(stampato).subscribe({
					next: (publishedStampato) => {
						this.stampato = publishedStampato;
						this.originalStampato = _.cloneDeep(this.stampato);
						this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Stampato pubblicato!' });
					},
					error: (err) => {
						let errorMessage = err?.message || 'Pubblicazione fallita!';
						this.messageService.add({ severity: 'error', summary: 'Errore', detail: errorMessage });
					}
				});
			} else {
				this.messageService.add({ severity: 'error', summary: 'Errore', detail: "Abilitare almeno una tipologia di pubblicazione: PDF o XHTML" });
			}
		});
	}
		
	unpublish(stampato: StampatoModel) {
		this.confirmUnsavedChanges().then((confirmed: boolean) => {
			if (!confirmed) return;
			this.stampatoService.unpublish(stampato).subscribe({
				next: (unpublishedStampato) => {
					this.stampato = unpublishedStampato;
					this.originalStampato = _.cloneDeep(this.stampato);
					this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Stampato non pubblicato!' });
				},
				error: (err) => { 
					let errorMessage = err?.message || 'L\'operazione di rendere lo stampato non pubblico fallita!';
					this.messageService.add({ severity: 'error', summary: 'Errore', detail: errorMessage });
				}
			});
		});
	}
			
	delete(stampato: StampatoModel) {
		this.confirmUnsavedChanges().then((confirmed: boolean) => {
			if (!confirmed) return;
			this.ref = this.dialogService.open(DialogAnswerComponent, {
				header: 'Cancella Stampato', width: '30%', height: '30%', modal: true, contentStyle: { overflow: 'auto', paddingBottom: '1px' }, 
				data: { text: 'Confermi di cancellare lo stampato con BARCODE: ' + stampato.id.barcode + ' ?'},
				templates: { footer: AnswerFooterComponent },
				baseZIndex: 10000, closable: true });
			this.ref.onClose.subscribe((answer: boolean) => {
				if (answer) {
					this.stampatoService.delete(stampato).subscribe({
					    next: (res) => {
						    this.stampato = res;
							this.originalStampato = _.cloneDeep(this.stampato);
						   	this.messageService.add({ severity: 'success', summary: 'Stampato cancellato correttamente' });
						},
					    error: (err) => {
							let errorMessage = err?.message || 'Errore durante la cancellazione dello stampato';
							this.messageService.add({ severity: 'error', summary: 'Errore', detail: errorMessage });
						}
					});
				}
			});
		});
	}
			
	restore(stampato: StampatoModel) {
		this.confirmUnsavedChanges().then((confirmed: boolean) => {
			if (!confirmed) return;
			this.ref = this.dialogService.open(DialogAnswerComponent, {
				header: 'Ripristina Stampato', width: '30%', height: '30%', modal: true, contentStyle: { overflow: 'auto', paddingBottom: '1px' }, 
				data: { text: 'Confermi di ripristinare lo stampato con BARCODE: ' + stampato.id.barcode + ' ?'},
				templates: { footer: AnswerFooterComponent },
				baseZIndex: 10000, closable: true });
			this.ref.onClose.subscribe((answer: boolean) => {
				if (answer) {
					this.stampatoService.restore(stampato).subscribe({
						next: (res) => {
							this.stampato = res;
							this.originalStampato = _.cloneDeep(this.stampato);
							this.messageService.add({ severity: 'success', summary: 'Stampato ripristinato correttamente' });
						},
						error: (err) => { 
							let errorMessage = err?.message || 'Errore durante il ripristino dello stampato';
							this.messageService.add({ severity: 'error', summary: 'Errore', detail: errorMessage });
						}
					});
				}
			});
		});
	}
		
	loadFel() {
		this.felService.loadFel(this.stampato).subscribe({
			next: (eFelStampato) => {	
				this.efel = eFelStampato;
				this.stampato.titoloFel = this.efel.titolo;				
				this.originalStampato = _.cloneDeep(this.stampato);
				setTimeout(() => {
				  this.barcodeComponent?.tryCompareTexts();
				});
			},
			error: (err) => {
				let errorMessage = err?.message || 'Caricamento dati eFel fallito!';
				this.messageService.add({ severity: 'error', summary: 'Errore', detail: errorMessage });
			}
		});
	}
	
}