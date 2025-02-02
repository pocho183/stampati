import { Injectable } from '@angular/core';
import { StampatoFormat } from 'app/models/stampato.model';
import { TypographyToProcessModel } from 'app/models/typography.model';
    
@Injectable()
export class TypographyService {
    
	getStampatiXHTML(): Promise<TypographyToProcessModel[]> {
	    return Promise.resolve([
	        { barcode: '19PDL3456788', format: StampatoFormat.XHTML, deleted: false  },
			{ barcode: '19PDL3456788', format: StampatoFormat.XHTML, deleted: true  },
	    ]);
	}
	
	getStampatiPDF(): Promise<TypographyToProcessModel[]> {
	    return Promise.resolve([
	        { barcode: '19PDL0000000', format: StampatoFormat.PDF, deleted: false },
			{ barcode: '19PDL1110000', format: StampatoFormat.PDF, deleted: true }
	    ]);
	}
};