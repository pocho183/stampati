import { Component, OnInit } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { ButtonModule } from "primeng/button";
import { DialogModule } from 'primeng/dialog';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';

@Component({
	standalone: true,
	selector: 'dialog-ricerca',
	imports: [ButtonModule, FormsModule, ReactiveFormsModule, DialogModule],
	templateUrl: './dialog.ricerca.component.html',
	styleUrl: './dialog.ricerca.component.css'
})
export class DialogRicercaComponent implements OnInit {

  constructor(private dialogService: DialogService, private ref: DynamicDialogRef) {}

	ngOnInit() {
  	}

	closeDialog() {
	    this.ref.close(true);
	  }
}