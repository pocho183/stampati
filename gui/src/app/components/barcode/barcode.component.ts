import { Component, OnInit } from "@angular/core";
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
import { CKEditorModule } from "@ckeditor/ckeditor5-angular";
import { CommonModule } from "@angular/common";
import { Autosave, Bold, ClassicEditor, EditorConfig, Essentials, Indent, Italic, Paragraph, Subscript, Superscript, Undo } from "ckeditor5";

interface City {
    name: string;
    code: string;
}

@Component({
	standalone: true,
	selector: 'barcode',
	imports: [CommonModule, CKEditorModule, TableModule, CardModule, ButtonModule, FormsModule, TooltipModule, InputGroupModule,
		InputGroupAddonModule, IftaLabelModule, CheckboxModule, SelectModule, InputTextModule, DatePickerModule],
	templateUrl: './barcode.component.html',
	styleUrl: './barcode.component.css'
})
export class BarcodeComponent implements OnInit {

	public Editor = ClassicEditor;
	public config : EditorConfig;
	cities: City[] | undefined;

	text: string = '<div>Hello World!</div><div>PrimeNG <b>Editor</b> Rocks</div><div><br></div>';

    selectedCity: City | undefined;

    ngOnInit() {
        this.cities = [
            { name: 'New York', code: 'NY' },
            { name: 'Rome', code: 'RM' },
            { name: 'London', code: 'LDN' },
            { name: 'Istanbul', code: 'IST' },
            { name: 'Paris', code: 'PRS' }
        ];
			this.config = {
				toolbar: {
					items: ['bold', 'italic', 'subscript', 'superscript', '|', 'undo', 'redo'],
					shouldNotGroupWhenFull: false
				},
				initialData: '',
				plugins: [ Autosave, Bold, Essentials, Italic, Paragraph, Subscript, Superscript, Undo],
				language: 'it',
			};
    }
	
}