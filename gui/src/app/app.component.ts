import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { environment } from 'environments/environment.develop';
import { PrimeNG } from 'primeng/config';
import { NgIf } from '@angular/common';
import { ToolbarModule } from 'primeng/toolbar';
import { ProgressSpinner } from 'primeng/progressspinner';
import { UtilityService } from "app/services/utility.service";

@Component({
	standalone: true,
	selector: 'app-root',
	imports: [ToolbarModule, ProgressSpinner, RouterModule, NgIf],
	providers: [UtilityService],
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

	version: string = environment.version;
	showSpinner: boolean;
	legislature: string = '';

	constructor(private config: PrimeNG, 
		private utilityService: UtilityService) { }

	ngOnInit(): void {
		this.config.setTranslation({
			accept: 'Accetta',
			reject: 'Annulla',
			dayNamesMin: ['Do', 'Lu', 'Ma', 'Me', 'Gi', 'Ve', 'Sa'],
			dayNamesShort: ['Dom', 'Lun', 'Mar', 'Mer', 'Gio', 'Ven', 'Sab'],
			dayNames: ['Domenica', 'Lunedì', 'Martedì', 'Mercoledì', 'Giovedì', 'Venerdì', 'Sabato'],
			monthNames: ['Gennaio', 'Febbraio', 'Marzo', 'Aprile', 'Maggio', 'Giugno', 'Luglio', 'Agosto', 'Settembre', 'Ottobre', 'Novembre', 'Dicembre'],
			monthNamesShort: ['Gen', 'Feb', 'Mar', 'Apr', 'Mag', 'Giu', 'Lug', 'Ago', 'Set', 'Ott', 'Nov', 'Dic'],
			dateFormat: 'dd-MM-yyyy',
			today: 'Oggi',
			firstDayOfWeek: 1,
			noFilter: 'Nessun filtro',
			startsWith: 'Inizia con',
			contains: 'Contiene',
		    notContains: 'Non contiene',
		    endsWith: 'Finisce con',
		    equals: 'Uguale a',
			notEquals: 'Diverso da'
		});
		
		this.utilityService.fetchLastLegislature();
		this.utilityService.fetchCommissions();
	}
}
