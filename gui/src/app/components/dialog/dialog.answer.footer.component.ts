import { Component } from '@angular/core';
import { DynamicDialogRef } from 'primeng/dynamicdialog';
import { ButtonModule } from 'primeng/button';

@Component({
    selector: 'answerfooter',
    standalone: true,
    imports: [ButtonModule],
    template:  `
        <div class="flex w-full justify-end mt-4">
		    <div  class="mr-3"><p-button (click)="no()" label="No" severity="danger" [outlined]="true" severity="secondary"></p-button></div>
			<div><p-button (click)="yes()" label="Si" [outlined]="true" [style]="{'background-color': '#285492', 'color': 'white'}"></p-button></div>
		</div> `
})
export class AnswerFooterComponent {
    constructor(public ref: DynamicDialogRef) {}

	yes() {
		this.ref.close(true);
	}
		
	no() {
		this.ref.close(false);
	}
}