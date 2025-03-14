import { Component, OnInit } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { ButtonModule } from "primeng/button";
import { DialogModule } from 'primeng/dialog';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { SelectButtonModule } from 'primeng/selectbutton';
import { TableModule } from 'primeng/table';

import { ExtractorService } from 'app/services/extractor.service';
import { TypographyToProcessModel } from "app/models/typography.model";
import { UtilityService } from "app/services/utility.service";
import { saveAs } from 'file-saver';
import { PdfViewerComponent } from "../pdfviewer/pdfviewer.component";
import { LegislaturaModel } from "app/models/legislatura.model";
import { StampatoModel } from "app/models/stampato.model";
import { MessageService } from "primeng/api";

@Component({
	standalone: true,
	selector: 'dialog-ricerca',
	imports: [ButtonModule, FormsModule, ReactiveFormsModule, DialogModule, SelectButtonModule, TableModule ],
	providers: [ExtractorService],
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
	stampato: StampatoModel;
	legislature: LegislaturaModel = null;

  	constructor(private dialogService: DialogService,
		private ref: DynamicDialogRef,
		private utilityService: UtilityService,
		private messageService: MessageService,
		private extractorService: ExtractorService) {}

	ngOnInit() {
		this.utilityService.getWorkingLegislature().subscribe((leg) => {
		    this.legislature = leg;
		    this.extractorService.getStampatiXHTML(this.legislature.legArabo).then((data) => { this.stampati = data; });
		});
	}

	onSelectionChange(selectedValue: string) {
	    if (selectedValue === 'xhtml') {
	        this.extractorService.getStampatiXHTML(this.legislature.legArabo).then((data) => { this.stampati = data; });
	    } else if (selectedValue === 'pdf') {
	        this.extractorService.getStampatiPDF(this.legislature.legArabo).then((data) => { this.stampati = data; });
	    }
	}
	
	getSeverity(dataDeleted: string | null): 'success' | 'danger' {
	    return dataDeleted ? 'danger' : 'success';
	}
	
	async selectBarcode(stampato: TypographyToProcessModel) {
		try {
			this.stampato = await this.extractorService.getStampato(stampato);
			if (this.stampato?.id?.barcode)
				this.ref.close(this.stampato);
			else
				this.messageService.add({ severity: 'error', summary: 'Errore', detail: 'Stampato non valido' });
		} catch (error) {
			this.messageService.add({ severity: 'error', summary: 'Errore', detail: 'Errore nel caricamento dello stampato' });
		}
	}

	
	previewStampato(stampato: TypographyToProcessModel) {
		let extension = stampato.format.toString().toLocaleLowerCase();
		
		console.log(extension);
		let filename = stampato.barcode + "." + extension;
		this.utilityService.preview(filename, stampato.legislaturaId, extension).subscribe(response => {
		    if (response) {
		        if (extension === 'pdf') {
		            this.dialogService.open(PdfViewerComponent, { 
		                width: '50vw', height: '80vh', modal: true, baseZIndex: 10000, dismissableMask: true,
		                data: { file: response, filename: filename }
		            });
				} else if (extension === 'xhtml') {
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