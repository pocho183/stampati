import { Component, ViewEncapsulation, OnInit } from "@angular/core";
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
import { ClassicEditor, Bold, Essentials, Italic, Paragraph } from 'ckeditor5';

@Component({
	standalone: true,
	selector: 'barcode',
	encapsulation: ViewEncapsulation.None,
	imports: [CommonModule, CKEditorModule, TableModule, CardModule, ButtonModule, FormsModule, TooltipModule, InputGroupModule,
		InputGroupAddonModule, IftaLabelModule, CheckboxModule, SelectModule, InputTextModule, DatePickerModule],
	templateUrl: './barcode.component.html',
	styleUrl: './barcode.component.css'
})
export class BarcodeComponent implements OnInit {

	public Editor = ClassicEditor;
	public config = {
		licenseKey: 'GPL',
	    plugins: [ Essentials, Paragraph, Bold, Italic ],
	    toolbar: [ 'undo', 'redo', '|', 'bold', 'italic', '|' ]
	}
	text: string = '<p>Conversione in legge del decreto-legge 2 gennaio 2023, n. 1, recante disposizioni urgenti per la gestione dei flussi migrator</p>';
	text1: string = '<p>Conversione in legge del decreto-legge 2 gennaio 2023, n. 1, recante disposizioni urgenti per la gestione dei flussi migrator</p>';

    ngOnInit() {
	
	
	
    }
	
}