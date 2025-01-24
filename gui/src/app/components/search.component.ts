import { Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { ButtonModule } from "primeng/button";
import { IftaLabelModule } from "primeng/iftalabel";
import { InputTextModule } from "primeng/inputtext";

@Component({
	standalone: true,
	selector: 'app-text-search',
	imports: [IftaLabelModule, InputTextModule, ButtonModule, FormsModule],
	templateUrl: './search.component.html',
	styleUrl: './search.component.css'
})
export class SearchComponent {
	txt: string;

	find() {
		console.log('Find pdl');
	}
}