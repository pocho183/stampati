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
			} else {
		        this.messageService.add({ severity: 'error', summary: 'Errore nell\'invio email' });
			}
		});
	}
	
	copyTitle() {
		this.stampato.titolo = this.text;
	}
	
	preview(format: string) {
		if(this.stampato && this.stampato.id && this.stampato.id.barcode) {
			let filename = (this.stampato.id.barcode + "." + format).replace("x", "");;
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
		let type = this.extractTypeStampato(this.stampato.id.barcode);
		let numeriPDL = this.stampato.numeriPDL ? this.stampato.numeriPDL.split('-')[0] : 'unknown';
		let filename = 'leg.' + this.legislature.legArabo + "." + (type ? type : '') + '.camera.' + numeriPDL;
		if (this.stampato.relazioneMin?.trim()) filename += '-' + this.stampato.relazioneMin;
		if (this.stampato.navette?.trim()) filename += '-' + this.stampato.navette;
		let relazione = this.stampato.lettera ? this.stampato.lettera : '';
		if (this.stampato.rinvioInCommissione) relazione += 'R';
		//if (this.stampato.relazioneMin?.trim())
			//relazione = relazione.trim() ? relazione.concat('-').concat(this.stampato.relazioneMin) : relazione.concat(this.stampato.relazioneMin);
		if (relazione.trim()) filename = filename + '_' + relazione;    
			this.stampato.nomeFile = filename + '.' + this.stampato.id.barcode + '.html';
		}
	}
	
	extractTypeStampato(input?: string): string | null {
	    const match = input.match(/^\d{2}(PDL|MSG|TU)/);
	    return match ? match[1].toLowerCase() : null;
	}
}