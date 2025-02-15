import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CommissioniModel } from 'app/models/commissioni.model';
import { BehaviorSubject, Observable, of, ReplaySubject } from 'rxjs';
import { tap, shareReplay, switchMap, filter } from 'rxjs/operators';

// Global Singleton, this is shared service
@Injectable({ providedIn: 'root' })
export class UtilityService {
  
    private cachedLegislature: string | null = null;
    private legislatureSubject = new ReplaySubject<string>(1);
	private commissionsSubject = new BehaviorSubject<CommissioniModel[]>([]);

    constructor(private http: HttpClient) {}
  
    fetchLegislature(): void {
        const url = '/utility/legislature/last';
        if (!this.cachedLegislature) {
            this.http.get<string>(url).pipe(
                tap((data) => {
                    this.cachedLegislature = data;
                    this.legislatureSubject.next(data);
                }), shareReplay(1) ).subscribe();
        }
    }
  
    getWorkingLegislature(): Observable<string> {
        return this.cachedLegislature ? of(this.cachedLegislature) : this.legislatureSubject.asObservable();
    }
  
	fetchCommissions(): void {
		this.getWorkingLegislature().pipe(
	    filter(leg => !!leg),
	    switchMap(leg => {
	    	const url = `/utility/commissions/${leg}`;
	        return this.http.get<CommissioniModel[]>(url).pipe(
	        	tap(data => { this.commissionsSubject.next(data); }), shareReplay(1) ); }) ).subscribe();
	}

	getWorkingCommissions(): Observable<CommissioniModel[]> {
		return this.commissionsSubject.asObservable();
	}
  
    preview(filename, leg, extension): Observable<any> {
        const url = '/utility/preview';
        const params = new HttpParams().set('filename', filename).set('leg', leg).set('extension', extension);
        return this.http.post(url, params, { responseType: 'arraybuffer' });
    }
}
