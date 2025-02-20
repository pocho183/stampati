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
	
}