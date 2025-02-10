import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of, tap } from 'rxjs';

    
@Injectable()
export class UtilityService {
	
	private cachedLegislature: string | null = null;
	
	constructor(private http: HttpClient) {}
	
	getLastLegislature(): Observable<string> {
		const url = '/utility/legislature/last';
		if (this.cachedLegislature) {
			console.log('Returning cached legislature:', this.cachedLegislature);
	      	return of(this.cachedLegislature);
			}
	    return this.http.get(url, { responseType: 'text' }).pipe(tap((data: string) => { 
			console.log('Fetched from API:', data);
			this.cachedLegislature = data;
	    }));
	}

};