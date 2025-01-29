import { Component, OnInit } from "@angular/core";
import { SplitterModule } from 'primeng/splitter';
import { RicercaComponent } from './ricerca/ricerca.component';
import { BarcodeComponent } from './barcode/barcode.component';
import { FrontespizioComponent } from "./frontespizio/frontespizio.component";
import { RelatoriComponent } from './relatori/relatori.component';
import { PresentazioneComponent } from "./presentazione/presentazione.component";

@Component({
	standalone: true,
	selector: 'stampato',
	imports: [SplitterModule, RicercaComponent, BarcodeComponent, FrontespizioComponent, RelatoriComponent, 
		PresentazioneComponent],
	templateUrl: './stampato.component.html',
	styleUrl: './stampato.component.css'
})
export class StampatoComponent implements OnInit {

    ngOnInit() { }
	
}