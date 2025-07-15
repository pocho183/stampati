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
import { diffWords } from 'diff';
import { DomSanitizer } from '@angular/platform-browser';
import { MessageService } from "primeng/api";
import { UtilityService } from "app/services/utility.service";

@Component({
	standalone: true,
	selector: 'dialog-email',
	imports: [ButtonModule, FormsModule, ReactiveFormsModule, CommonModule, DialogModule, SelectButtonModule, 
		TextareaModule, CKEditorModule, InputTextModule, ChipModule ],
	providers: [DialogService, MessageService],
	templateUrl: './dialog.email.component.html',
	styleUrl: './dialog.email.component.css'
})
export class DialogEmailComponent implements OnInit {

	stampato: StampatoModel;
	emails: string[] = [];
	public Editor = ClassicEditor;
	public configEditor = {
		licenseKey: 'GPL',
		plugins: [ Essentials, Paragraph, Bold, Italic ],
		toolbar: [ 'undo', 'redo', '|', 'bold', 'italic', '|' ],
		removePlugins: ['CKBox', 'EasyImage', 'CloudServices']
	}
	diffHtml: string = '';
	
  	constructor(private dialogService: DialogService, 
		public config: DynamicDialogConfig,
		private sanitizer: DomSanitizer,
		private messageService: MessageService,
		private utilityService: UtilityService,
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
	  	const cleanOriginalText = this.stripHtml(this.stampato.titoloFel.trim());
	  	const cleanModifiedText = this.stripHtml(this.stampato.titolo.trim());
	  	if(cleanOriginalText === cleanModifiedText) {
	    	this.diffHtml = ''; 
	    	return;
	  	}
	  	const differences = diffWords(cleanOriginalText, cleanModifiedText);
	  	this.diffHtml = differences
	    	.map(part => part.added ? `<mark>${part.value}</mark>` : part.removed ? '' : part.value).join('');
	}
	
	stripHtml(html: string): string {
	  	let tempDiv = document.createElement("div");
	  	tempDiv.innerHTML = html;
	  	return tempDiv.innerText || tempDiv.textContent || "";
	}

	addEmail(newEmailInput: HTMLInputElement) {
	    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
	    const newEmail = newEmailInput.value.trim();
	    if (newEmail && emailPattern.test(newEmail) && !this.emails.includes(newEmail)) {
	        this.emails.push(newEmail);
	        newEmailInput.value = '';
	    } else {
			this.messageService.add({ severity: 'error', summary: 'Invalid or duplicate email:' + newEmail});
	    }
	}
	
	sendEmail(): void {
		if(!this.emails || this.emails.length === 0) { 
			this.messageService.add({ severity: 'warn', summary: 'Missing Email', detail: 'Please enter an email before sending.' });
		    return;
		}
	    this.utilityService.sendEmail(this.stampato.titoloFel, this.stampato.titolo, this.stampato.numeriPDL, this.emails).subscribe({
	        next: () => {
	            this.messageService.add({ severity: 'success', summary: 'Email Sent', detail: 'The email was successfully sent.' });
	            this.ref.close(true);
	        },
	        error: (err) => {
	            console.error('Email sending failed:', err);
	            this.messageService.add({ severity: 'error', summary: 'Email Error', detail: 'Failed to send the email. Please try again.' });
	            this.ref.close(false);
	        }
	    });
	}
	
}