import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgModule, provideZoneChangeDetection } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';

import { providePrimeNG } from 'primeng/config';
import Lara from '@primeng/themes/lara';
import { WINDOW_PROVIDERS } from './extension/window.provider';
import { BackEndInterceptor } from './extension/back-end-interceptor';

import { CommonModule } from '@angular/common';
import { AppRoutingModule } from './app-routing.module';
import { ButtonModule } from 'primeng/button';
import { FileUploadModule } from 'primeng/fileupload';
import { SelectModule } from 'primeng/select';

// PrimeNG imports
import { ToastModule } from 'primeng/toast';
import { ConfirmationService, MessageService } from 'primeng/api';

import { AppComponent } from './app.component';

import { TrialService } from '../app/services/trial.service';

@NgModule({
  declarations: [
	AppComponent,
	
   ],
  imports: [
	BrowserModule,
	HttpClientModule,
	FormsModule,
	BrowserAnimationsModule,
	AppRoutingModule,
	CommonModule,
	ButtonModule,
	ToastModule,
	FileUploadModule,
	SelectModule,
  ],
  exports: [  ],
  providers: [
	provideZoneChangeDetection({ eventCoalescing: true }), 
	provideAnimationsAsync(),
	providePrimeNG({ theme: { preset: Lara } }),
	WINDOW_PROVIDERS,
    { provide: HTTP_INTERCEPTORS, useClass: BackEndInterceptor, multi: true },
	TrialService,
	MessageService, 
	ConfirmationService,
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}