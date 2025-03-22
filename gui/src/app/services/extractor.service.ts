import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { StampatoFormat, StampatoModel } from 'app/models/stampato.model';
import { TypographyToProcessModel } from 'app/models/typography.model';
import { first, map, Observable } from 'rxjs';
import { ClassTransformer, plainToInstance } from 'class-transformer';

    
@Injectable()
export class ExtractorService {
	
	constructor(private http: HttpClient) {}
    
	getStampato(stampato: TypographyToProcessModel): Promise<StampatoModel> {
	    const url = `/extractor/load`;
		return this.http.post<StampatoModel>(url, stampato).pipe(first(), map(response => plainToInstance(StampatoModel, response))).toPromise().catch(error => { throw error });
	}

	getStampatiXHTML(leg: number): Promise<TypographyToProcessModel[]> {
		const url = `/extractor/newtoprocess/${leg}/xhtml`;
		return this.http.get<TypographyToProcessModel[]>(url).toPromise();
	}
	
	getStampatiPDF(leg: number): Promise<TypographyToProcessModel[]> {
	    const url = `/extractor/newtoprocess/${leg}/pdf`;
	    return this.http.get<TypographyToProcessModel[]>(url).toPromise();
	}

};