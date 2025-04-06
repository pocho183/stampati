import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CommissioneModel } from 'app/models/commissione.model';
import { LegislaturaModel } from 'app/models/legislatura.model';
import { BehaviorSubject, Observable, of, ReplaySubject } from 'rxjs';
import { tap, shareReplay, switchMap, filter } from 'rxjs/operators';

// Global Singleton, this is shared service
@Injectable({ providedIn: 'root' })
export class UtilityService {
  
    private cachedLegislature: LegislaturaModel | null = null;
    private legislatureSubject = new ReplaySubject<LegislaturaModel>(1);
	private legislatures: LegislaturaModel[] = [];
	private commissionsSubject = new BehaviorSubject<CommissioneModel[]>([]);
	readonly STRALCIO: string[] = [
		"bis", "ter", "quater", "quinquies", "sexies", "septies", "octies", "novies", "decies",
	  	"undecies", "duodecies", "terdecies", "quaterdecies", "quindecies", "sedecies", 
	  	"septiesdecies", "duodevicies", "undevicies", "vicies", "semeletvicies", "bisetvicies",
	  	"teretvicies", "quateretvicies", "quinquiesetvicies", "sexiesetvicies", "septiesetvicies",
	  	"octiesetvicies", "noviesetvicies", "tricies", "semelettricies", "bisettricies", "terettricies",
	 	"quaterettricies", "quinquiesettricies", "sexiesettricies", "septiesettricies", "octiesettricies",
	  	"noviesettricies", "quadragies", "semeletquadragies", "bisetquadragies", "teretquadragies",
		"quateretquadragies", "quinquiesetquadragies", "sexiesetquadragies", "septiesetquadragies",
		"octiesetquadragies", "noviesetquadragies", "quinquagies"
	];

    constructor(private http: HttpClient) {}
  
	fetchLegislature(): Observable<LegislaturaModel[]> {
	    const url = '/utility/legislature';
	    return this.http.get<LegislaturaModel[]>(url);
	}
	
    fetchLastLegislature(): void {
        const url = '/utility/legislature/last';
        if (!this.cachedLegislature) {
            this.http.get<LegislaturaModel>(url).pipe(
                tap((data) => {
                    this.cachedLegislature = data;
                    this.legislatureSubject.next(data);
                }), shareReplay(1) ).subscribe();
        }
    }
  
    getWorkingLegislature(): Observable<LegislaturaModel> {
        return this.cachedLegislature ? of(this.cachedLegislature) : this.legislatureSubject.asObservable();
    }
  
	fetchCommissions(): void {
		this.getWorkingLegislature().pipe(
	    filter(leg => !!leg),
	    switchMap(leg => {
	    	const url = `/utility/commissions/${leg.legArabo}`;
	        return this.http.get<CommissioneModel[]>(url).pipe(
	        	tap(data => { this.commissionsSubject.next(data); }), shareReplay(1) ); }) ).subscribe();
	}

	getWorkingCommissions(): Observable<CommissioneModel[]> {
		return this.commissionsSubject.asObservable();
	}
  
    preview(filename, leg, extension): Observable<any> {
        const url = '/utility/preview';
        const params = new HttpParams().set('filename', filename).set('leg', leg).set('extension', extension);
        return this.http.post(url, params, { responseType: 'arraybuffer' });
    }
	
	sendEmail(titleFel: string, title: string, numeriPDL: string, emails: string[]): Observable<boolean> {
	    const url = '/utility/sendEmail';
	    const payload = { titleFel, title, numeriPDL, emails };
	    return this.http.post<boolean>(url, payload);
	}
	
	getStralcio(atto: string): string | null {
		const pattern = new RegExp(`-(?i:(${this.STRALCIO.join("|")}))`, "i");
	  	const match = atto?.match(pattern);
	  	return match ? match[1] : null;
	}
}
