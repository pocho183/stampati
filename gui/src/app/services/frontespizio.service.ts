import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Product } from 'app/models/product';
import { first, Observable } from 'rxjs';

    
@Injectable()
export class FrontespizioService {

	constructor(private http: HttpClient) {}

	getProductsData(): Promise<Product[]> {
	    return Promise.resolve([
	        { id: '1000', code: 'f230fh0g3', name: '1245', description: 'Product Description', image: 'bamboo-watch.jpg', price: 65, category: 'Accessories', quantity: 24, inventoryStatus: 'INSTOCK', rating: 5 },
	        { id: '1001', code: 'nvklal433', name: '9944', description: 'Product Description', image: 'black-watch.jpg', price: 72, category: 'Accessories', quantity: 61, inventoryStatus: 'OUTOFSTOCK', rating: 4 },
	        { id: '1002', code: 'zz21cz3c1', name: '12345', description: 'Product Description', image: 'blue-band.jpg', price: 79, category: 'Fitness', quantity: 2, inventoryStatus: 'LOWSTOCK', rating: 3 },
	        { id: '1003', code: '1003', name: '1003', description: 'Product Description', image: 'blue-t-shirt.jpg', price: 29, category: 'Clothing', quantity: 25, inventoryStatus: 'INSTOCK', rating: 5 },
	        { id: '1004', code: 'h456wer53', name: '366', description: 'Product Description', image: 'bracelet.jpg', price: 15, category: 'Accessories', quantity: 73, inventoryStatus: 'INSTOCK', rating: 4 },
	        { id: '1005', code: 'av2231fwg', name: '62-bis', description: 'Product Description', image: 'brown-purse.jpg', price: 120, category: 'Accessories', quantity: 0, inventoryStatus: 'OUTOFSTOCK', rating: 4 },
	        { id: '1006', code: 'bib36pfvm', name: '98', description: 'Product Description', image: 'chakra-bracelet.jpg', price: 32, category: 'Accessories', quantity: 5, inventoryStatus: 'LOWSTOCK', rating: 3 },
	        { id: '1007', code: 'mbvjkgip5', name: '101', description: 'Product Description', image: 'galaxy-earrings.jpg', price: 34, category: 'Accessories', quantity: 23, inventoryStatus: 'INSTOCK', rating: 5 }
	    ]);
	}
	
	getAttiAbbinati(atto: string): Observable<string[]> {
		const url = `/efel/attiabbinati/${atto}`;
		return this.http.get<string[]>(url).pipe(first());
	}

};