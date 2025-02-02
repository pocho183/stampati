import { Component, OnInit } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { ButtonModule } from "primeng/button";
import { DialogModule } from 'primeng/dialog';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { SelectButtonModule } from 'primeng/selectbutton';
import { TableModule } from 'primeng/table';

import { TypographyService } from 'app/services/typography.service';
import { TypographyToProcessModel } from "app/models/typography.model";

@Component({
	standalone: true,
	selector: 'dialog-ricerca',
	imports: [ButtonModule, FormsModule, ReactiveFormsModule, DialogModule, SelectButtonModule, TableModule],
	providers: [TypographyService],
	templateUrl: './dialog.ricerca.component.html',
	styleUrl: './dialog.ricerca.component.css'
})
export class DialogRicercaComponent implements OnInit {
	
	stateOptions: any[] = [
	  { label: 'XHTML', initialValue: 'xhtml', icon: 'assets/img/xhtml.png' },
	  { label: 'PDF', initialValue: 'pdf', icon: 'assets/img/sheet.png' }
	];
	initialValue: string = 'xhtml';
	stampati!: TypographyToProcessModel[];

  	constructor(private dialogService: DialogService,
		private ref: DynamicDialogRef,
		private typographyService: TypographyService) {}

	ngOnInit() {
		this.typographyService.getStampatiXHTML().then((data) => { this.stampati = data; });
  	}

	onSelectionChange(selectedValue: string) {
	    if (selectedValue === 'xhtml') {
	        this.typographyService.getStampatiXHTML().then((data) => { this.stampati = data; });
	    } else if (selectedValue === 'pdf') {
	        this.typographyService.getStampatiPDF().then((data) => { this.stampati = data; });
	    }
	}
	
	selectBarcode(stampato: TypographyToProcessModel) {
		this.ref.close(stampato);
	}
	
	previewStampato(stampato: TypographyToProcessModel) {
		
	}
}