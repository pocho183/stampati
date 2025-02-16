import { Component, OnInit } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { ButtonModule } from "primeng/button";
import { DialogModule } from 'primeng/dialog';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { SelectButtonModule } from 'primeng/selectbutton';
import { TableModule } from 'primeng/table';

import { TypographyService } from 'app/services/typography.service';
import { TypographyToProcessModel } from "app/models/typography.model";
import { UtilityService } from "app/services/utility.service";
import { saveAs } from 'file-saver';
import { PdfViewerComponent } from "../pdfviewer/pdfviewer.component";
import { LegislaturaModel } from "app/models/legislatura.model";

@Component({
	standalone: true,
	selector: 'dialog-ricerca',
	imports: [ButtonModule, FormsModule, ReactiveFormsModule, DialogModule, SelectButtonModule, TableModule ],
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
	legislature: LegislaturaModel = null;

  	constructor(private dialogService: DialogService,
		private ref: DynamicDialogRef,
		private utilityService: UtilityService,
		private typographyService: TypographyService) {}

	ngOnInit() {
		this.utilityService.getWorkingLegislature().subscribe((leg) => {
		    this.legislature = leg;
		    this.typographyService.getStampatiXHTML(this.legislature.legArabo).then((data) => { this.stampati = data; });
		});
	}

	onSelectionChange(selectedValue: string) {
	    if (selectedValue === 'xhtml') {
	        this.typographyService.getStampatiXHTML(this.legislature.legArabo).then((data) => { this.stampati = data; });
	    } else if (selectedValue === 'pdf') {
	        this.typographyService.getStampatiPDF(this.legislature.legArabo).then((data) => { this.stampati = data; });
	    }
	}
	
	getSeverity(dataDeleted: string | null): 'success' | 'danger' {
	    return dataDeleted ? 'danger' : 'success';
	}
	
	selectBarcode(stampato: TypographyToProcessModel) {
		this.ref.close(stampato);
	}
	
	previewStampato(stampato: TypographyToProcessModel) {
		let extension = stampato.format.toString().toLocaleLowerCase();
		let filename = stampato.barcode + "." + extension;
		this.utilityService.preview(filename, stampato.legislaturaId, extension).subscribe(response => {
		    if (response) {
		        if (extension === 'pdf') {
		            this.dialogService.open(PdfViewerComponent, { 
		                width: '50vw', height: '80vh', modal: true, baseZIndex: 10000, dismissableMask: true,
		                data: { file: response, filename: filename }
		            });
				} else if (extension === 'html') {
					const newWindow = window.open('', '_blank');
					if (newWindow) {
						newWindow.document.open();
						newWindow.document.write(new TextDecoder().decode(response));
						newWindow.document.close();
					}
		        } else {
		            let blob = new Blob([response], { type: 'blob' });
		            if (blob) saveAs(blob, filename);
		        }
		    } else {
		        console.error("Empty response, file not loaded.");
		    }
		});
	}
}