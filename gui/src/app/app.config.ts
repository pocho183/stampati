import { ApplicationConfig } from "@angular/core";
import { provideRouter, Routes } from "@angular/router";
import { provideHttpClient, withInterceptors } from "@angular/common/http";
import { backEndInterceptor } from "./extension/back-end-interceptor";
import { TextComponent } from "./components/text.component";

const routes: Routes = [
	{ path: '', component: TextComponent },
	// otherwise redirect to home
	{ path: '**', redirectTo: '' }
];

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([backEndInterceptor]))
  ]
};
