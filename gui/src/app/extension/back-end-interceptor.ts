import { HttpErrorResponse, HttpEvent, HttpHandlerFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { ErrorMessageComponent } from 'app/extension/error-message/error-message.component';
import { environment } from 'environments/environment';
import { Observable } from 'rxjs';
import { finalize, tap } from 'rxjs/operators';
import { WINDOW } from './window.provider';

export function backEndInterceptor(req: HttpRequest<any>, next: HttpHandlerFn): Observable<HttpEvent<any>> {
	let window = inject(WINDOW);
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
			handleError(err);
		}
	}), finalize(() => {  }));
}

function handleError(error: HttpErrorResponse): void {
	console.log(error);
	if (error.status === 0 || error.status === 404) {
		this.dialogService.dialogComponentRefMap.forEach(dialog => {
			dialog.destroy();
		});
		this.ref = this.dialogService.open(ErrorMessageComponent, {
			header: 'Attenzione',
			width: '70%',
			contentStyle: { overflow: 'auto' },
			baseZIndex: 10000,
			data: 'Servizio attualmente non disponibile. Riprovare più tardi.',
			closable: true
		});
	} else if (error.status === 400) {
		this.ref = this.dialogService.open(ErrorMessageComponent, {
			header: error.error.blocked === false ? "Attenzione" : "Errore",
			width: '70%',
			contentStyle: { overflow: 'auto' },
			baseZIndex: 10000,
			data: error.error,
		});
	} else if (error.status === 500) {
		this.ref = this.dialogService.open(ErrorMessageComponent, {
			header: "Errore di sistema",
			width: '70%',
			contentStyle: { overflow: 'auto' },
			baseZIndex: 10000,
			data: 'Si è verificato un errore applicativo.',
		});
	}
}
