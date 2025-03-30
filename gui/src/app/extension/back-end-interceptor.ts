import { HttpErrorResponse, HttpEvent, HttpHandlerFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { ErrorMessageComponent } from 'app/extension/error-message/error-message.component';
import { environment } from 'environments/environment';
import { Observable } from 'rxjs';
import { finalize, tap } from 'rxjs/operators';
import { WINDOW } from './window.provider';
import { DialogService } from 'primeng/dynamicdialog';

export function backEndInterceptor(req: HttpRequest<any>, next: HttpHandlerFn): Observable<HttpEvent<any>> {
	let window = inject(WINDOW);
	let dialogService = inject(DialogService);
	
	if (req.url && !req.url.startsWith('http') && !req.url.includes('/asset')) {
		const port = environment.backEndPort ? ':' + environment.backEndPort : window.location.port;
		req = req.clone({
			url: window.location.protocol + '//' + window.location.hostname + port + environment.backEnd + req.url
		});
	}
	return next(req).pipe(tap({
		next: response => {
		},
		error: err => {
			handleError(err, dialogService);
		}
	}), finalize(() => {  }));
}

function handleError(error: HttpErrorResponse, dialogService: DialogService): void {
    console.log(error);
    let errorMessage: any = 'Si è verificato un errore.';
	if (error.status === 0 || error.status === 404) {
		errorMessage = 'Servizio attualmente non disponibile. Riprovare più tardi.';
	} else if (error.status === 400) {
	    errorMessage = error.error || 'Richiesta non valida.';
	} else if (error.status === 500) {
	    errorMessage = error.error || 'Si è verificato un errore applicativo.';
	}
	// Propagate the error so the service can catch it.
	throw new Error(errorMessage);
}
