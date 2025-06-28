import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { first, map, Observable } from 'rxjs';
import { plainToInstance } from 'class-transformer';
import { StampatoModel } from 'app/models/stampato.model';
import { StampatoFelModel } from 'app/models/stampato.fel.model';
    
@Injectable()
export class FelService {
	
	constructor(private http: HttpClient) {}
	
	loadFel(stampato: StampatoModel): Observable<StampatoFelModel> {
		const url = `/efel/load`;
		return this.http.post<StampatoFelModel>(url, stampato).pipe(first(), map(response => plainToInstance(StampatoFelModel, response)));
	}
}