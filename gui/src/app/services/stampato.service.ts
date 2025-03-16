import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { StampatoModel } from 'app/models/stampato.model';
import { Observable } from 'rxjs';
    
@Injectable()
export class StampatoService {
	
	constructor(private http: HttpClient) {}
	
	save(stampato: StampatoModel): Observable<StampatoModel> {
	  const url = '/stampato/save';
	  return this.http.post<StampatoModel>(url, stampato);
	}
	
	delete(stampato: StampatoModel): Observable<StampatoModel> {
		const url = '/stampato/delete';
		return this.http.post<StampatoModel>(url, stampato);
	}
	
	restore(stampato: StampatoModel): Observable<StampatoModel> {
		const url = '/stampato/restore';
		return this.http.post<StampatoModel>(url, stampato);
	}
	
	publish(stampato: StampatoModel): Observable<StampatoModel> {
		const url = '/stampato/publish';
		return this.http.post<StampatoModel>(url, stampato);
	}
	
	unpublish(stampato: StampatoModel): Observable<StampatoModel> {
		const url = '/stampato/unpublish';
		return this.http.post<StampatoModel>(url, stampato);
	}
	
}