import { ApplicationConfig } from "@angular/core";
import { provideRouter, Routes } from "@angular/router";
import { provideHttpClient, withInterceptors } from "@angular/common/http";
import { provideAnimations } from "@angular/platform-browser/animations";
import { backEndInterceptor } from "./extension/back-end-interceptor";
import { providePrimeNG } from "primeng/config";
import Nora from "@primeng/themes/nora";
import { definePreset } from "@primeng/themes";
import { ConfirmationService, MessageService } from 'primeng/api';
import { DialogService } from 'primeng/dynamicdialog';
import { WINDOW_PROVIDERS } from './extension/window.provider';
import { MenuComponent } from "./components/menu/menu.component";
import { CanDeactivateGuard } from './extension/can-deactivate.guard';

const routes: Routes = [
  { path: 'testi', component: MenuComponent, canDeactivate: [CanDeactivateGuard] },
  { path: 'testi/:barcode', component: MenuComponent, canDeactivate: [CanDeactivateGuard] },
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
	provideAnimations(),
    provideHttpClient(withInterceptors([backEndInterceptor])),	
	providePrimeNG({
		theme: {
			preset: customStyle,
			options: {
				darkModeSelector: '.my-app-dark'
			}
		}
	}),
	WINDOW_PROVIDERS,
	ConfirmationService,
	MessageService,
	DialogService
  ]
};
