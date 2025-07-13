import { Component, OnInit, Input, Output, EventEmitter } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { ButtonModule } from "primeng/button";
import { CardModule } from 'primeng/card';
import { ToastModule } from 'primeng/toast';
import { ListboxModule } from 'primeng/listbox';
import { TooltipModule } from 'primeng/tooltip';
import { NgIf } from '@angular/common';
import * as _ from 'lodash';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { DialogRelatoriComponent } from "./dialog.relatori.component";
import { StampatoModel } from "app/models/stampato.model";

@Component({
	standalone: true,
	selector: 'relatori',
	imports: [ButtonModule, FormsModule, ReactiveFormsModule, CardModule, ToastModule, NgIf, 
		ListboxModule, TooltipModule],
	providers: [DialogService, MessageService],
	templateUrl: './relatori.component.html',
	styleUrl: './relatori.component.css'
})
export class RelatoriComponent implements OnInit {
	
	private _stampato: StampatoModel;
	@Output() stampatoUpdated = new EventEmitter<StampatoModel>();
	@Input()
	set stampato(value: StampatoModel) {
		this._stampato = value;
	}
	get stampato(): StampatoModel { return this._stampato; }
	private ref: DynamicDialogRef | undefined;
	
	constructor(private dialogService: DialogService,
			private messageService: MessageService,
			private confirmationService: ConfirmationService) {}

    ngOnInit() { }
	
	showDialog() {
		this.ref = this.dialogService.open(DialogRelatoriComponent, {
			header: 'Gestione Relatori', width: '30%',
			modal: true,
	        data: _.cloneDeep(this.stampato),
			contentStyle: { overflow: 'auto' },
			baseZIndex: 10000, closable: true
		});
		this.ref.onClose.subscribe((updatedStampatoFromDialog: StampatoModel) => {
			if (updatedStampatoFromDialog) {
				this.stampatoUpdated.emit(updatedStampatoFromDialog);
				this.messageService.add({ severity: 'info', summary: 'Relatori modificati', detail: 'Ricorda di salvare le modifiche dallo stampato principale.' });
			}
		});
	}
}