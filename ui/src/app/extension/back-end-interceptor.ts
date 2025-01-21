import { Injectable, Inject } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { from, Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { WINDOW } from './window.provider';
import { Router } from '@angular/router';

@Injectable()
export class BackEndInterceptor implements HttpInterceptor {

	constructor(@Inject(WINDOW) private window: Window, private router: Router) {}

	intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
		if(req.url && !req.url.startsWith('http') && !req.url.startsWith('/assets')) {
			const port = environment.backEndPort ? ':' + environment.backEndPort : this.window.location.port;
			req = req.clone( {
				url: this.window.location.protocol + '//' + this.window.location.hostname + port + environment.backEnd + req.url
			});
		}
		return next.handle(req);
	}

}