import { ApplicationConfig } from "@angular/core";
import { provideRouter, Routes } from "@angular/router";
import { provideHttpClient, withInterceptors } from "@angular/common/http";
import { backEndInterceptor } from "./extension/back-end-interceptor";
import { StampatoComponent } from "./components/stampato.component";
import { providePrimeNG } from "primeng/config";
import Nora from "@primeng/themes/nora";
import { definePreset } from "@primeng/themes";

import { ConfirmationService, MessageService } from 'primeng/api';

const routes: Routes = [
	{ path: '', component: StampatoComponent },
	// otherwise redirect to home
	{ path: '**', redirectTo: '' }
];

const customStyle = definePreset(Nora, {
    semantic: {
        primary: {
            50: '{indigo.50}',
            100: '{indigo.100}',
            200: '{indigo.200}',
            300: '{indigo.300}',
            400: '{indigo.400}',
            500: '{indigo.500}',
            600: '{indigo.800}',
            700: '{indigo.700}',
            800: '{indigo.800}',
            900: '{indigo.900}',
            950: '{indigo.950}'
        }
    }
});

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([backEndInterceptor])),	
	providePrimeNG({
		theme: {
			preset: customStyle,
			options: {
				darkModeSelector: '.my-app-dark'
			}
		}
	}),
	ConfirmationService,
	MessageService,
  ]
};
