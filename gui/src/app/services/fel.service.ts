import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { first, map, Observable } from 'rxjs';
import { plainToInstance } from 'class-transformer';
import { StampatoModel } from 'app/models/stampato.model';
import { TypographyToProcessModel } from 'app/models/typography.model';
    
@Injectable()
export class FelService {
	
	constructor(private http: HttpClient) {}
	
	loadFel(stampato: StampatoModel): Observable<StampatoModel> {
		const url = `/efel/load`;
		return this.http.post<StampatoModel>(url, stampato).pipe(first(), map(response => plainToInstance(StampatoModel, response)));
	}
}