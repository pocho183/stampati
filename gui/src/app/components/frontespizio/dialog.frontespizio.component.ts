import { Component, OnInit, ChangeDetectorRef } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { ButtonModule } from "primeng/button";
import { DialogModule } from 'primeng/dialog';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { SelectButtonModule } from 'primeng/selectbutton';
import { TableModule } from 'primeng/table';
import { BadgeModule } from 'primeng/badge';
import { InputGroupModule } from 'primeng/inputgroup';
import { InputGroupAddonModule } from 'primeng/inputgroupaddon';
import { TooltipModule } from 'primeng/tooltip';
import { PickListModule } from 'primeng/picklist';
import { FrontespizioService } from 'app/services/frontespizio.service';
import { StampatoIdModel, StampatoModel } from "app/models/stampato.model";
import { LegislaturaModel } from "app/models/legislatura.model";
import { UtilityService } from "app/services/utility.service";

@Component({
	standalone: true,
	selector: 'dialog-frontespizio',
	imports: [ButtonModule, FormsModule, ReactiveFormsModule, DialogModule, SelectButtonModule, TableModule, 
		BadgeModule, InputGroupModule, InputGroupAddonModule, PickListModule, TooltipModule ],
	providers: [FrontespizioService],
	templateUrl: './dialog.frontespizio.component.html',
	styleUrl: './dialog.frontespizio.component.css'
})
export class DialogFrontespizioComponent implements OnInit {

	stampato: StampatoModel;
	sourceActs!: StampatoModel[];
	targetActs!: StampatoModel[];
	attiAbbinati: string[];
	legislature: LegislaturaModel = null;
	
  	constructor(private ref: DynamicDialogRef,
		public config: DynamicDialogConfig,
		private utilityService: UtilityService,
		private frontespizioService: FrontespizioService,
		private cdr: ChangeDetectorRef) {}
	
	
	ngOnInit() {
		this.stampato = this.config.data || null;
		this.utilityService.getWorkingLegislature().subscribe((leg) => {
			this.legislature = leg;
		});
		if (this.stampato?.numeriPDL) {
			const numeri = this.stampato.numeriPDL.split('-');
			this.targetActs = numeri.map((numero: string) => {
				const id: StampatoIdModel = {
					barcode: '',
					legislatura: this.legislature.legArabo
				};
				const nuovo = new StampatoModel(id);
				nuovo.numeroAtto = numero;
				return nuovo;
			});
		} else {
			this.targetActs = [];
		}
		this.cdr.markForCheck();
	}
	
	save() {
		this.ref.close(this.targetActs);
	}
	
	search(searchInput: string) {
		this.frontespizioService.getAttiAbbinati(this.legislature.legArabo, searchInput).subscribe((abbinati) => {
			this.sourceActs = abbinati;
		});
	}
	
	duplicateToTarget(event: any) {
	    const itemsToAdd = event.items;
	    itemsToAdd.forEach((item: any) => {
	        if (!this.targetActs.some(p => p.codiceEstremiAttoPdl === item.codiceEstremiAttoPdl)) 
	            this.targetActs.push(item);
	    });
		this.sourceActs = this.sourceActs.filter(item =>
	    	!itemsToAdd.some(moved => moved.codiceEstremiAttoPdl === item.codiceEstremiAttoPdl)
		);		
	    this.targetActs.sort((a, b) => a.numeroAtto.localeCompare(b.numeroAtto));
	    this.sourceActs.sort((a, b) => a.numeroAtto.localeCompare(b.numeroAtto));
	}
}