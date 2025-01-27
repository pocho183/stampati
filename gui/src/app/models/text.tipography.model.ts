export enum TextFileType {
	XHTML = 1,
  	PDF = 2
}

export enum TextType {
	PDL = 1,
  	MSG = 2
}

export class TextTypography {
	id: number;
	leg:number;
  	name: string;
  	label: string;
	type: TextType;
  	typeFile: TextFileType;
}