import { Component, OnInit } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { ButtonModule } from "primeng/button";
import { DialogModule } from 'primeng/dialog';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { SelectButtonModule } from 'primeng/selectbutton';
import { TableModule } from 'primeng/table';
import { TooltipModule } from 'primeng/tooltip';
import { RelatoriService } from "app/services/relatori.service";
import { ListboxModule } from 'primeng/listbox';
import { SelectModule } from 'primeng/select';
import { CheckboxModule } from 'primeng/checkbox';
import { TextareaModule } from 'primeng/textarea';
import { UtilityService } from "app/services/utility.service";
import { CommissioneModel } from "app/models/commissione.model";

@Component({
	standalone: true,
	selector: 'dialog-relatori',
	imports: [ButtonModule, FormsModule, ReactiveFormsModule, DialogModule, SelectButtonModule, TableModule, 
		TooltipModule, ListboxModule, SelectModule, CheckboxModule, TextareaModule ],
	providers: [RelatoriService],
	templateUrl: './dialog.relatori.component.html',
	styleUrl: './dialog.relatori.component.css'
})
export class DialogRelatoriComponent implements OnInit {
	
	commissions: CommissioneModel[] | null = null;
	
	
	checked: boolean = false;
	checked1: boolean = false;
	value!: string;
	
  	constructor(private dialogService: DialogService,
		private ref: DynamicDialogRef,
		private utilityService: UtilityService,
		private relatoriService: RelatoriService) {}

	ngOnInit() {
		this.utilityService.getWorkingCommissions().subscribe(data => {
			this.commissions = data;
		});

	}
	
	save() {
		this.ref.close(true);
	}

}