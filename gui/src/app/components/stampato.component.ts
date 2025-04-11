import { Component, OnInit, HostListener } from "@angular/core";
import { SplitterModule } from 'primeng/splitter';
import { RicercaComponent } from './ricerca/ricerca.component';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import * as _ from 'lodash';
import { RouterModule } from '@angular/router';

@Component({
	standalone: true,
	selector: 'stampato',
	imports: [RouterModule, SplitterModule, RicercaComponent, ButtonModule, ToastModule],
	providers: [],
	templateUrl: './stampato.component.html',
	styleUrl: './stampato.component.css'
})
export class StampatoComponent implements OnInit {
	
	constructor() { }
	
    ngOnInit() { }
	

}