import { Injectable } from '@angular/core';
import { BarcodeTypography } from 'app/models/typography.model';
    
@Injectable()
export class TypographyService {
    
	getStampatiXHTML(): Promise<BarcodeTypography[]> {
	    return Promise.resolve([
	        { barcode: '19PDL3456788' },
	        { barcode: '19PDL2255672' },
	        { barcode: '19PDL2255672' },
	        { barcode: '19PDL8456788' },
	        { barcode: '19MSG1055672' },
	        { barcode: '19PDL1055672' },
			{ barcode: '19PDL3456788' },
			{ barcode: '19PDL2255672' },
			{ barcode: '19PDL2255672' },
		    { barcode: '19PDL8456788' },
	        { barcode: '19MSG1055672' },
	        { barcode: '19PDL1055672' },
	    ]);
	}
	
	getStampatiPDF(): Promise<BarcodeTypography[]> {
	    return Promise.resolve([
	        { barcode: '19PDL0000000' },
	        { barcode: '19PDL1111111' },
	        { barcode: '19PDL2255672' },
	        { barcode: '19PDL8456788' },
	        { barcode: '19MSG1055672' },
	        { barcode: '19PDL1055672' },
	    ]);
	}
};