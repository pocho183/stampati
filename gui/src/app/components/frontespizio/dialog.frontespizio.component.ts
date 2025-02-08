import { Component, OnInit, ChangeDetectorRef } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { ButtonModule } from "primeng/button";
import { DialogModule } from 'primeng/dialog';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { SelectButtonModule } from 'primeng/selectbutton';
import { TableModule } from 'primeng/table';
import { BadgeModule } from 'primeng/badge';
import { InputGroupModule } from 'primeng/inputgroup';
import { InputGroupAddonModule } from 'primeng/inputgroupaddon';
import { TooltipModule } from 'primeng/tooltip';
import { PickListModule } from 'primeng/picklist';

import { FrontespizioService } from 'app/services/frontespizio.service';
import { Product } from "app/models/product";

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
	
	sourceProducts!: Product[];
	targetProducts!: Product[];
	
  	constructor(private dialogService: DialogService,
		private ref: DynamicDialogRef, 	
		private frontespizioService: FrontespizioService,
		private cdr: ChangeDetectorRef) {}

	ngOnInit() {
		this.frontespizioService.getProductsData().then(products => {
			this.sourceProducts = products;
		    this.cdr.markForCheck();
		});
		this.targetProducts = [];
	}
	
	save() {
		this.ref.close(true);
	}
	
	duplicateToTarget(event: any) {
	    const itemsToAdd = event.items;
	    itemsToAdd.forEach((item: any) => {
	        if (!this.targetProducts.some(p => p.id === item.id)) 
	            this.targetProducts.push(item);
	    });
	    this.sourceProducts = [...this.sourceProducts, ...itemsToAdd.filter(item =>
	        !this.sourceProducts.some(p => p.id === item.id)
	    )];
	    this.targetProducts.sort((a, b) => a.name.localeCompare(b.name));
	    this.sourceProducts.sort((a, b) => a.name.localeCompare(b.name));
	}
}