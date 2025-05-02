import { Component, ViewEncapsulation, OnInit, Input, Output } from "@angular/core";
import { TableModule } from "primeng/table";
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { FormsModule } from "@angular/forms";
import { TooltipModule } from 'primeng/tooltip';
import { InputGroupModule } from 'primeng/inputgroup';
import { InputGroupAddonModule } from 'primeng/inputgroupaddon';
import { IftaLabelModule } from 'primeng/iftalabel';
import { CheckboxModule } from 'primeng/checkbox';
import { SelectModule } from 'primeng/select';
import { InputTextModule } from 'primeng/inputtext';
import { DatePickerModule } from 'primeng/datepicker';
import { CommonModule } from "@angular/common";
import { CKEditorModule } from '@ckeditor/ckeditor5-angular';
import { ToastModule } from 'primeng/toast';
import { ClassicEditor, Bold, Essentials, Italic, Paragraph } from 'ckeditor5';
import { StampatoModel } from "app/models/stampato.model";
import { StampatoService } from "app/services/stampato.service";
import { UtilityService } from "app/services/utility.service";
import { MessageService } from 'primeng/api';
import { LegislaturaModel } from "app/models/legislatura.model";
import { DialogService, DynamicDialogRef } from "primeng/dynamicdialog";
import { DialogEmailComponent } from "./dialog.email.component";
import { PdfViewerComponent } from "../pdfviewer/pdfviewer.component";
import { diffWords } from 'diff';
import { environment } from "environments/environment.develop";

@Component({
	standalone: true,
	selector: 'barcode',
	encapsulation: ViewEncapsulation.None,
	imports: [CommonModule, CKEditorModule, TableModule, CardModule, ButtonModule, FormsModule, TooltipModule, InputGroupModule,
		InputGroupAddonModule, IftaLabelModule, CheckboxModule, SelectModule, InputTextModule, DatePickerModule, 
		ToastModule],
	providers: [StampatoService, MessageService, DialogService],
	templateUrl: './barcode.component.html',
	styleUrl: './barcode.component.css'
})
export class BarcodeComponent implements OnInit {

	@Input() stampato: StampatoModel;
	legislature: LegislaturaModel = null;
	private ref: DynamicDialogRef | undefined;
	public Editor = ClassicEditor;
	public config = {
		licenseKey: 'GPL',
	    plugins: [ Essentials, Paragraph, Bold, Italic ],
	    toolbar: [ 'undo', 'redo', '|', 'bold', 'italic', '|' ],
		removePlugins: ['CKBox', 'EasyImage', 'CloudServices']
	}
	compareTitle: boolean = false;
	text: string = 'Titolo eFel';
	
	constructor(private stampatoService: StampatoService,
		private dialogService: DialogService,
		private messageService: MessageService,
		private utilityService: UtilityService) {}
	
    ngOnInit() {
		this.utilityService.getWorkingLegislature().subscribe((leg) => { this.legislature = leg; });
		this.compareTexts();
	 }
	 
	 onChangeBarcode(event: any) {
		this.updateFilename();
	 }
	
	openEmail() {
		this.ref = this.dialogService.open(DialogEmailComponent, 
			{ header: 'Invia modifiche titolo', width: '50%', height: '70%', modal: true, contentStyle: { overflow: 'auto' },
			data: this.stampato,
			baseZIndex: 10000, closable: true });
		this.ref.onClose.subscribe((emailSuccess: boolean) => {
			if (emailSuccess) {
				this.compareTexts();
				this.messageService.add({ severity: 'success', summary: 'Email inviata con successo' });
			}
		});
	}
	
	copyTitle() {
		this.stampato.titolo = this.text;
		this.compareTexts();
	}
	
	linkPDL() {
		if(this.stampato && this.stampato.id && this.stampato.id.barcode && this.stampato.numeroAtto) {
			let url = environment.linkPDL.replace("{legislatura}", ''+this.stampato.id.legislatura).replace("{legislatura}", ''+this.stampato.id.legislatura).replace("{numeroAtto}", ''+this.stampato.numeroAtto);
			// Append a unique cache-busting anchor to avoid cache
			const now = new Date();
			const timeString = `${now.getHours()}${now.getMinutes()}${now.getSeconds()}`;
			url += `#${timeString}`;
			window.open(url, "_blank");
		}
	}
	
	preview(format: string) {
		if(this.stampato && this.stampato.id && this.stampato.id.barcode) {
			let filename = (this.stampato.id.barcode + "." + format).replace("x", "");
			this.utilityService.preview(filename, this.stampato.id.legislatura, format).subscribe(response => {
				if (response) {
					if (format === 'pdf') {
				    	this.dialogService.open(PdfViewerComponent, { 
					    	width: '50vw', height: '80vh', modal: true, baseZIndex: 10000, dismissableMask: true,
					        data: { file: response, filename: filename }
					    });
					} else if (format === 'xhtml') {
						const newWindow = window.open('', '_blank');
						if (newWindow) {
							newWindow.document.open();
							newWindow.document.write(new TextDecoder().decode(response));
							newWindow.document.close();
						}
					}
				} else {
					console.error("Empty response, file not loaded.");
				}
			});
		}
	}
	
	onTitoloChange(value: string): void {
	    this.compareTexts();
	}
	
	compareTexts() {
	  	const cleanOriginalText = this.stripHtml(this.text.trim());
		const cleanModifiedText = this.stripHtml(this.stampato.titolo.trim());
		if(cleanOriginalText === cleanModifiedText)
		    this.compareTitle = true;
		else
			this.compareTitle = false;
	}
	
	stripHtml(html: string): string {
		let tempDiv = document.createElement("div");
	  	tempDiv.innerHTML = html;
		return tempDiv.innerText || tempDiv.textContent || "";
	}
	
	updateFilename() :void {
		if(!this.stampato || !this.stampato.id || this.stampato.id.barcode != null) {
			let stralcio = this.utilityService.getStralcio(this.stampato.numeriPDL);   
			let numeriPDL = stralcio != null ? this.stampato.numeriPDL.split("-")[0] + "-" + stralcio : this.stampato.numeriPDL.split("-")[0]
			let type = this.extractTypeStampato(this.stampato.id.barcode);
			//let numeriPDL = this.stampato.numeriPDL ? this.stampato.numeriPDL.split('-')[0] : 'unknown';
			let filename = 'leg.' + this.legislature.legArabo + "." + (type ? type : '') + '.camera.' + numeriPDL;
			if (this.stampato.navette?.trim()) filename += '-' + this.stampato.navette;
			let relazione = this.stampato.lettera ? this.stampato.lettera : '';
			if (this.stampato.rinvioInCommissione) relazione += 'R';
			if (relazione.trim()) filename = filename + '_' + relazione;
			if (this.stampato.relazioneMin?.trim()) filename += '-' + this.stampato.relazioneMin;						 
			this.stampato.nomeFile = filename + '.' + this.stampato.id.barcode;
		}
	}
	
	extractTypeStampato(input?: string): string | null {
	    const match = input.match(/^\d{2}(PDL|MSG|TU)/);
	    return match ? match[1].toLowerCase() : null;
	}
	
}