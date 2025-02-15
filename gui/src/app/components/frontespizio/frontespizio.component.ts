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
import { ToastModule } from 'primeng/toast';
import { DialogService, DynamicDialogRef, DynamicDialogModule } from 'primeng/dynamicdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { DialogFrontespizioComponent } from "./dialog.frontespizio.component";

@Component({
	standalone: true,
	selector: 'frontespizio',
	imports: [TableModule, CardModule, ButtonModule, FormsModule, TooltipModule, InputGroupModule,
		InputGroupAddonModule, IftaLabelModule, CheckboxModule, SelectModule, InputTextModule, 
	ToastModule, DynamicDialogModule],
	providers: [DialogService, MessageService, ConfirmationService],
	templateUrl: './frontespizio.component.html',
	styleUrl: './frontespizio.component.css'
})
export class FrontespizioComponent implements OnInit {
	
	private ref: DynamicDialogRef | undefined;
	letters: { name: string; value: string }[] = [];
	selectedLetter: string | null = null;
	minoranza: { name: string; value: string }[] = [];
    selectedMinoranza: string | null = null;
	
	constructor(private dialogService: DialogService,
		private messageService: MessageService,
		private confirmationService: ConfirmationService) {}

    ngOnInit() {
		this.letters = [
			{ name: 'A', value: 'A' }, { name: 'C', value: 'C' }, { name: 'E', value: 'E' },
		    { name: 'G', value: 'G' }, { name: 'I', value: 'I' }, { name: 'M', value: 'M' },
		    { name: 'O', value: 'O' }, { name: 'Q', value: 'Q' }, { name: 'S', value: 'S' },
		    { name: 'U', value: 'U' }, { name: 'Z', value: 'Z' }   
		];
		this.minoranza = [
			{ name: 'bis', value: 'bis' }, { name: 'ter', value: 'ter' }, { name: 'quater', value: 'quater' },
			{ name: 'quinquies', value: 'quinquies' }, { name: 'sexies', value: 'sexies' }, { name: 'septies', value: 'septies' },
		    { name: 'octies', value: 'octies' }, { name: 'novies', value: 'novies' }, { name: 'decies', value: 'decies' },
			{ name: 'undecies', value: 'undecies' }, { name: 'duodecies', value: 'duodecies' }, { name: 'terdecies', value: 'terdecies' },
			{ name: 'quaterdecies', value: 'quaterdecies' }, { name: 'quindecies', value: 'quindecies' }, { name: 'sedecies', value: 'sedecies' },
		    { name: 'septies decies', value: 'septiesdecies' }, { name: 'duodevicies', value: 'duodevicies' }, { name: 'undevicies', value: 'undevicies' },
			{ name: 'vicies', value: 'vicies' }, { name: 'semel et vicies', value: 'semeletvicies' }, { name: 'bis et vicies', value: 'bisetvicies' },
			{ name: 'ter et vicies', value: 'teretvicies' }, { name: 'quater et vicies', value: 'quateretvicies' }, { name: 'quinquies et vicies', value: 'quinquiesetvicies' },
			{ name: 'sexies et vicies', value: 'sexiesetvicies' }, { name: 'septies et vicies', value: 'septiesetvicies' }, { name: 'octies et vicies', value: 'octiesetvicies' },
			{ name: 'novies et vicies', value: 'noviesetvicies' }, { name: 'tricies', value: 'tricies' }, { name: 'semel et tricies', value: 'semelettricies' },
			{ name: 'bis et tricies', value: 'bisettricies' }, { name: 'ter et tricies', value: 'terettricies' }, { name: 'quater et tricies', value: 'quaterettricies' },
			{ name: 'quinquies et tricies', value: 'quinquiesettricies' }, { name: 'sexies et tricies', value: 'sexiesettricies' }, { name: 'septies et tricies', value: 'septiesettricies' },
			{ name: 'octies et tricies', value: 'octiesettricies' }, { name: 'novies et tricies', value: 'noviesettricies' }, { name: 'quadragies', value: 'quadragies' },
			{ name: 'semel et quadragies', value: 'semeletquadragies' }, { name: 'bis et quadragies', value: 'bisetquadragies' }, { name: 'teretquadragies', value: 'ter et quadragies' },
			{ name: 'quater et quadragies', value: 'quateretquadragies' }, { name: 'quinquiesetquadragies', value: 'quinquies et quadragies' }, { name: 'sexies et quadragies', value: 'sexiesetquadragies' },
			{ name: 'septies et quadragies', value: 'septiesetquadragies' }, { name: 'octies et quadragies', value: 'octiesetquadragies' }, { name: 'novies et quadragies', value: 'noviesetquadragies' },
			{ name: 'quinquagies', value: 'quinquagies' }
		];
    }
	
	showDialog() {
		this.ref = this.dialogService.open(DialogFrontespizioComponent, {header: 'Atti Associati', width: '40%', height: '60%',
				modal: true, contentStyle: { overflow: 'auto' }, baseZIndex: 10000, closable: true });
		    this.ref.onClose.subscribe((atto: boolean) => {
		    	if (atto)
		        	this.messageService.add({ severity: 'info', summary: 'Atto associato' });
		});
	}
	
	onLetterSelect(letter: string) {
	    console.log("Selected Letter:", letter);
	}
	
}