import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map, first } from 'rxjs/operators';
import { plainToClass } from "class-transformer";

@Injectable()
export class TrialService {

  	constructor(private http: HttpClient) {}
    
	trialCall(): Observable<MyResponse> {
		const url = '/trial/hello';
	  	return this.http.get<MyResponse>(url).pipe(first(), map(res => plainToClass(MyResponse, res)) );
	}
  
}

class MyResponse {
	value!: string;
}