import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of, ReplaySubject } from 'rxjs';
import { tap, shareReplay } from 'rxjs/operators';

    
@Injectable()
export class UtilityService {
	
	// Global variable
	private cachedLegislature: string | null = null;
	private legislatureSubject = new ReplaySubject<string>(1);
	
	constructor(private http: HttpClient) {}
	
	fetchLegislature(): void {
		const url = '/utility/legislature/last';
	    if (!this.cachedLegislature) {
	    	this.http.get<string>(url).pipe( tap((data) => {
	      		this.cachedLegislature = data;
	        	this.legislatureSubject.next(data);
	      	}), shareReplay(1)).subscribe();
	    }
	}

	getLastLegislature(): Observable<string> {
		return this.cachedLegislature ? of(this.cachedLegislature) : this.legislatureSubject.asObservable();
	}

};