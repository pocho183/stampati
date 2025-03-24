import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { StampatoFormat, StampatoModel } from 'app/models/stampato.model';
import { TypographyToProcessModel } from 'app/models/typography.model';
import { first, map, Observable } from 'rxjs';
import { ClassTransformer, plainToInstance } from 'class-transformer';

    
@Injectable()
export class ExtractorService {
	
	constructor(private http: HttpClient) {}
    
	getStampato(stampato: TypographyToProcessModel): Observable<StampatoModel> {
	    const url = `/extractor/load`;
		//return this.http.post<StampatoModel>(url, stampato).toPromise().catch(error => { throw error });
		return this.http.post<StampatoModel>('/extractor/load', stampato).pipe(first(), map(response => plainToInstance(StampatoModel, response)));			
	}

	getStampatiXHTML(leg: number): Observable<TypographyToProcessModel[]> {
		const url = `/extractor/newtoprocess/${leg}/xhtml`;
		return this.http.get<TypographyToProcessModel[]>(url).pipe(first());
	}
	
	getStampatiPDF(leg: number): Observable<TypographyToProcessModel[]> {
	    const url = `/extractor/newtoprocess/${leg}/pdf`;
	    return this.http.get<TypographyToProcessModel[]>(url).pipe(first());
	}

};