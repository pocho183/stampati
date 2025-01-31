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
	
	stateOptions: any[] = [{ label: 'XHTML', initialValue: 'xhtml' },{ label: 'PDF', initialValue: 'pdf' }];
	initialValue: string = 'xhtml';

  	constructor(private dialogService: DialogService, private ref: DynamicDialogRef) {}

	ngOnInit() {
  	}

	closeDialog() {
	    this.ref.close(true);
	  }
}