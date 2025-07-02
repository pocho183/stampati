import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { StampatoModel } from 'app/models/stampato.model';
import { first, Observable } from 'rxjs';

    
@Injectable()
export class FrontespizioService {
	
	constructor(private http: HttpClient) {}

	getAttiAbbinati(leg: number, pdlSearch: string): Observable<StampatoModel[]> {
		const url = `/efel/attiabbinati/${leg}/${pdlSearch}`;
		return this.http.get<StampatoModel[]>(url).pipe(first());
	}

};