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
import { RelatoriService } from "app/services/relatori.service";
import { ListboxModule } from 'primeng/listbox';
import { SelectModule } from 'primeng/select';
import { CheckboxModule } from 'primeng/checkbox';
import { TextareaModule } from 'primeng/textarea';

interface City {
    name: string,
    code: string
}

@Component({
	standalone: true,
	selector: 'dialog-relatori',
	imports: [ButtonModule, FormsModule, ReactiveFormsModule, DialogModule, SelectButtonModule, TableModule, 
		BadgeModule, InputGroupModule, InputGroupAddonModule, TooltipModule, ListboxModule, SelectModule,
		CheckboxModule, TextareaModule ],
	providers: [RelatoriService],
	templateUrl: './dialog.relatori.component.html',
	styleUrl: './dialog.relatori.component.css'
})
export class DialogRelatoriComponent implements OnInit {
	
	cities!: City[];

	selectedCity!: City;
	
	checked: boolean = false;
	checked1: boolean = false;
	value!: string;
	
  	constructor(private dialogService: DialogService,
		private ref: DynamicDialogRef, 	
		private relatoriService: RelatoriService,
		private cdr: ChangeDetectorRef) {}

	ngOnInit() {
		this.cities = [
			{ name: 'Cirielli Edmondo, per la maggioranza', code: 'NY' },
	        { name: 'Ziello Edorardo', code: 'RM' }
		];
	}
	
	save() {
		this.ref.close(true);
	}

}