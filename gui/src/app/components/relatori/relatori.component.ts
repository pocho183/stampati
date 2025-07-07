import { Component, OnInit, Input } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { ButtonModule } from "primeng/button";
import { CardModule } from 'primeng/card';
import { ToastModule } from 'primeng/toast';
import { ListboxModule } from 'primeng/listbox';
import { TooltipModule } from 'primeng/tooltip';
import { NgIf } from '@angular/common';
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
	@Input()
	set stampato(value: StampatoModel) {
		this._stampato = value;
	}
	get stampato(): StampatoModel { return this._stampato; }
	private ref: DynamicDialogRef | undefined;
	
	constructor(private dialogService: DialogService,
			private messageService: MessageService,
			private confirmationService: ConfirmationService) {}

    ngOnInit() {

    }
	
	showDialog() {
		this.ref = this.dialogService.open(DialogRelatoriComponent, {header: 'Gestione Relatori', width: '30%', height: '60%',
			modal: true,
			data: this.stampato,
			contentStyle: { overflow: 'auto' }, 
			baseZIndex: 10000, closable: true 
		});
		this.ref.onClose.subscribe((atto: boolean) => {
			if (atto)
		       	this.messageService.add({ severity: 'info', summary: 'Relatori associati' });
		});
	}
	


	/*
	
			this.ref.onClose.subscribe((targetActs: any[]) => {
				if(targetActs && targetActs.length > 0) {
					const PDLChain = targetActs.map(act => act.numeroAtto).join('-');
			
					this.updateNomeFrontespizioForDialog(PDLChain);
					
			    	this.messageService.add({ severity: 'info', summary: 'Atto associato' });
					console.log('Selected PDLChain:', PDLChain);
					console.log('Selected targetProducts:', targetActs);
			    }
			});
		}*/

}