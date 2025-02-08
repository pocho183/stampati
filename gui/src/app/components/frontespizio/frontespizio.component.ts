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