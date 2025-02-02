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

@Component({
	standalone: true,
	selector: 'frontespizio',
	imports: [TableModule, CardModule, ButtonModule, FormsModule, TooltipModule, InputGroupModule,
		InputGroupAddonModule, IftaLabelModule, CheckboxModule, SelectModule, InputTextModule],
	templateUrl: './frontespizio.component.html',
	styleUrl: './frontespizio.component.css'
})
export class FrontespizioComponent implements OnInit {
	
	letters: { name: string; value: string }[] = [];
    selectedLetter: string | null = null;

    ngOnInit() {
		this.letters = [
			{ name: 'A', value: 'A' }, { name: 'C', value: 'C' }, { name: 'E', value: 'E' },
		    { name: 'G', value: 'G' }, { name: 'I', value: 'I' }, { name: 'M', value: 'M' },
		    { name: 'O', value: 'O' }, { name: 'Q', value: 'Q' }, { name: 'S', value: 'S' },
		    { name: 'U', value: 'U' }, { name: 'Z', value: 'Z' }   
		];
    }
	
	onLetterSelect(letter: string) {
	    console.log("Selected Letter:", letter);
	}
	
}