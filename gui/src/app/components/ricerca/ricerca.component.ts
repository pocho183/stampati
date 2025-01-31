import { Component, OnInit } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { ButtonModule } from "primeng/button";
import { IftaLabelModule } from "primeng/iftalabel";
import { InputTextModule } from "primeng/inputtext";
import { InputGroupModule } from 'primeng/inputgroup';
import { InputGroupAddonModule } from 'primeng/inputgroupaddon';
import { SelectModule } from 'primeng/select';
import { CardModule } from 'primeng/card';
import { FileUploadModule } from 'primeng/fileupload';
import { ToastModule } from 'primeng/toast';
import { DialogService, DynamicDialogRef, DynamicDialogModule } from 'primeng/dynamicdialog';
import { ConfirmationService, MessageService, SelectItemGroup } from 'primeng/api';
import { DialogRicercaComponent } from 'app/components/ricerca/dialog.ricerca.component'
import { TextTypography, TextType, TextFileType } from 'app/models/text.tipography.model';

@Component({
	standalone: true,
	selector: 'ricerca',
	imports: [IftaLabelModule, InputTextModule, ButtonModule, FormsModule, ReactiveFormsModule, 
		SelectModule, InputGroupModule, InputGroupAddonModule, CardModule, FileUploadModule, ToastModule,
		DynamicDialogModule],
	providers: [DialogService, MessageService],
	templateUrl: './ricerca.component.html',
	styleUrl: './ricerca.component.css'
})
export class RicercaComponent implements OnInit {
	
	selectedXHTML: TextTypography | undefined;
	selectedPDF: TextTypography | undefined;
	
	xhtml: SelectItemGroup[] = [];
	pdf: SelectItemGroup[] = [];
	
	ref: DynamicDialogRef | undefined;
	
	constructor(
		private dialogService: DialogService,
		private messageService: MessageService,
		private confirmationService: ConfirmationService) {}
	
	ngOnInit() {
		this.xhtml = [
			{
				label: '19PDL0003150',
				value: TextFileType.XHTML,
				items: []
			},
			{
				label: '19PDL0003151',
				value: TextFileType.XHTML,
				items: []
			},
			{
				label: '19MSG0115410',
				value: TextFileType.XHTML,
				items: []
			},
			{
				label: '19PDL0007823',
				value: TextFileType.XHTML,
				items: []
			}
		];
		
		this.pdf = [
			{
				label: '19PDL0003150',
				value: TextFileType.PDF,
				items: []
			},
			{
				label: '19PDL0003151',
				value: TextFileType.PDF,
				items: []
			},
			{
				label: '19MSG0115410',
				value: TextFileType.PDF,
				items: []
			},
			{
				label: '19PDL0007823',
				value: TextFileType.PDF,
				items: []
			}
		];
	}
	
	

	showDialog() {
	        this.ref = this.dialogService.open(DialogRicercaComponent, {
	            header: 'Carica Stampato',
	            width: '70%',
				height: '50%',
				modal: true,
	            contentStyle: { overflow: 'auto' },
	            baseZIndex: 10000,
				closable: true
	        });

	        this.ref.onClose.subscribe((res: boolean) => {
				console.log(res);
	            if (res) {
	                this.messageService.add({ severity: 'info', summary: 'Stampato caricato', detail: "sssss" });
	            }
				this.ref = undefined;
	        });
	    }
	
	onUploadXHTML(event: any) {
		console.log('Upload Event:', event);
		this.messageService.add({ severity: 'info', summary: 'Success', detail: 'File Uploaded with Auto Mode' });
	}
	
	onUploadPDF(event: any) {
		console.log('Upload Event:', event);
		this.messageService.add({ severity: 'info', summary: 'Success', detail: 'File Uploaded with Auto Mode' });
	}

}