import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ClassTransformer, plainToInstance } from 'class-transformer';
import { RicercaModel } from 'app/models/ricerca.model';
import { StampatoModel } from 'app/models/stampato.model';
import { first, map, Observable } from 'rxjs';

@Injectable()
export class RicercaService {
	
	constructor(private http: HttpClient) {}
	
	search(leg:number, textSearch: string): Observable<RicercaModel[]> {
		console.log('Searching.....');
		const url = '/search/stampato';
		const params = new HttpParams().set('leg', leg).set('text', textSearch);
	    return this.http.get<RicercaModel[]>(url, { params: params });
	}
	
	load(leg:number, barcode: string): Observable<StampatoModel> {
		const url = '/search/stampato/' + leg + '/' + barcode;
		return this.http.get<StampatoModel>(url).pipe(first(), map(response => plainToInstance(StampatoModel, response)));
	}
	
	saveOrder(results: RicercaModel[]): Observable<RicercaModel[]> {
		const url = `/search/saveorder`;
		return this.http.post<RicercaModel[]>(url, results);
	}
	
};