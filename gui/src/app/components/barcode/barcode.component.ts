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
import { DialogService } from "primeng/dynamicdialog";

@Component({
	standalone: true,
	selector: 'barcode',
	encapsulation: ViewEncapsulation.None,
	imports: [CommonModule, CKEditorModule, TableModule, CardModule, ButtonModule, FormsModule, TooltipModule, InputGroupModule,
		InputGroupAddonModule, IftaLabelModule, CheckboxModule, SelectModule, InputTextModule, DatePickerModule, 
		ToastModule],
	providers: [StampatoService, UtilityService, MessageService, DialogService],
	templateUrl: './barcode.component.html',
	styleUrl: './barcode.component.css'
})
export class BarcodeComponent implements OnInit {

	@Input() stampato: StampatoModel;
	legislature: LegislaturaModel = null;
	public Editor = ClassicEditor;
	public config = {
		licenseKey: 'GPL',
	    plugins: [ Essentials, Paragraph, Bold, Italic ],
	    toolbar: [ 'undo', 'redo', '|', 'bold', 'italic', '|' ],
		removePlugins: ['CKBox', 'EasyImage', 'CloudServices']
	}
	text: string = '';
	
	constructor(private stampatoService: StampatoService,
		private dialogService: DialogService,
		private messageService: MessageService,
		private utilityService: UtilityService) {}
	
    ngOnInit() { }
	
}