import { Component, OnInit } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { CommonModule } from '@angular/common';
import { StampatoModel } from "app/models/stampato.model";
import { ButtonModule } from "primeng/button";
import { DialogModule } from 'primeng/dialog';
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { SelectButtonModule } from 'primeng/selectbutton';
import { TextareaModule } from 'primeng/textarea';
import { CKEditorModule } from '@ckeditor/ckeditor5-angular';
import { ChipModule } from 'primeng/chip'; 
import { ClassicEditor, Bold, Essentials, Italic, Paragraph } from 'ckeditor5';
import { InputTextModule } from 'primeng/inputtext';
import { Diff, diffWords } from 'diff';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Component({
	standalone: true,
	selector: 'dialog-email',
	imports: [ButtonModule, FormsModule, ReactiveFormsModule, CommonModule, DialogModule, SelectButtonModule, 
		TextareaModule, CKEditorModule, InputTextModule, ChipModule ],
	providers: [],
	templateUrl: './dialog.email.component.html',
	styleUrl: './dialog.email.component.css'
})
export class DialogEmailComponent implements OnInit {

	stampato: StampatoModel;
	emails: string[] = ['example1@email.com', 'example2@email.com'];
	public Editor = ClassicEditor;
	public configEditor = {
		licenseKey: 'GPL',
		plugins: [ Essentials, Paragraph, Bold, Italic ],
		toolbar: [ 'undo', 'redo', '|', 'bold', 'italic', '|' ],
		removePlugins: ['CKBox', 'EasyImage', 'CloudServices']
	}
	diffHtml: string = '';
	text: string = 'titolo efel';
	
  	constructor(private dialogService: DialogService, 
		public config: DynamicDialogConfig,
		private sanitizer: DomSanitizer,
		private ref: DynamicDialogRef) {
		
	}

	ngOnInit() { 
		this.stampato = this.config.data || { titolo: '' }
		this.compareTexts();
	}
	
	compareTexts() {
		if(!this.stampato.titolo) {
	    	this.diffHtml = '';
	    	return;
	  	}
	  	const cleanOriginalText = this.stripHtml(this.text.trim());
	  	const cleanModifiedText = this.stripHtml(this.stampato.titolo.trim());
	  	if(cleanOriginalText === cleanModifiedText) {
	    	this.diffHtml = ''; 
	    	return;
	  	}
	  	const differences = diffWords(cleanOriginalText, cleanModifiedText);
	  	this.diffHtml = differences
	    	.map(part => 
	      	part.added ? `<mark>${part.value}</mark>` :
	      	part.removed ? '' :
	      	part.value).join('');
		}
	
	stripHtml(html: string): string {
	  	let tempDiv = document.createElement("div");
	  	tempDiv.innerHTML = html;
	  	return tempDiv.innerText || tempDiv.textContent || "";
	}

	addEmail(newEmail: string) {
		if (newEmail && !this.emails.includes(newEmail))
	    	this.emails.push(newEmail);
	}
	
	save() {
		
	}
	
}