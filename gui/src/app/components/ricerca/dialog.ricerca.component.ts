import { Component, OnInit } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { ButtonModule } from "primeng/button";
import { DialogModule } from 'primeng/dialog';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { SelectButtonModule } from 'primeng/selectbutton';

@Component({
	standalone: true,
	selector: 'dialog-ricerca',
	imports: [ButtonModule, FormsModule, ReactiveFormsModule, DialogModule, SelectButtonModule],
	templateUrl: './dialog.ricerca.component.html',
	styleUrl: './dialog.ricerca.component.css'
})
export class DialogRicercaComponent implements OnInit {
	
	stateOptions: any[] = [
	  { label: 'XHTML', initialValue: 'xhtml', icon: 'assets/img/xhtml.png' },
	  { label: 'PDF', initialValue: 'pdf', icon: 'assets/img/sheet.png' }
	];
	initialValue: string = 'xhtml';

  	constructor(private dialogService: DialogService, private ref: DynamicDialogRef) {}

	ngOnInit() {
  	}

	onSelectionChange(selectedValue: string) {
	    console.log("Selected format:", selectedValue);

	    if (selectedValue === 'xhtml') {
	        alert("XHTML selected!");
	        // Add any logic specific to XHTML
	    } else if (selectedValue === 'pdf') {
	        alert("PDF selected!");
	        // Add any logic specific to PDF
	    }
	}
}