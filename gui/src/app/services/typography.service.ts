import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { StampatoFormat } from 'app/models/stampato.model';
import { TypographyToProcessModel } from 'app/models/typography.model';

    
@Injectable()
export class TypographyService {
	
	constructor(private http: HttpClient) {}
    
	getStampatiXHTML(leg: string): Promise<TypographyToProcessModel[]> {
		const url = `/stampato/newtoprocess/${leg}/html`;
		return this.http.get<TypographyToProcessModel[]>(url).toPromise();
	}
	
	getStampatiPDF(leg: string): Promise<TypographyToProcessModel[]> {
	    const url = `/stampato/newtoprocess/${leg}/pdf`;
	    return this.http.get<TypographyToProcessModel[]>(url).toPromise();
	}

};