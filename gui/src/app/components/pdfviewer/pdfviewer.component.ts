import { Component, Inject } from '@angular/core';
import { DynamicDialogRef, DynamicDialogConfig } from 'primeng/dynamicdialog';
import { DialogModule } from 'primeng/dialog';
import { NgxExtendedPdfViewerModule } from 'ngx-extended-pdf-viewer';

@Component({
	standalone: true,
	selector: 'pdfviewer',
  	templateUrl: './pdfviewer.component.html',
	imports: [DialogModule, NgxExtendedPdfViewerModule],
  	styleUrls: ['./pdfviewer.component.css']
})
export class PdfViewerComponent {
  	fileURL: string;
  	filename: string;
	isVisible: boolean = true;

  	constructor(public ref: DynamicDialogRef, public config: DynamicDialogConfig) {
    	this.filename = config.data.filename;
    	this.loadPdf(config.data.file);
  	}

	loadPdf(file: ArrayBuffer) {
	    if (file) {
	        const blob = new Blob([file], { type: 'application/pdf' });
	        this.fileURL = URL.createObjectURL(blob);
	    } else {
	        console.error("No PDF file received.");
	    }
	}

}
