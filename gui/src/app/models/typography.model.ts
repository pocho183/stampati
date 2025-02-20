import { StampatoFormat } from "./stampato.model";

export interface TypographyToProcessModel {
  barcode: string;
  format: StampatoFormat;
  legislatura: string;
  dataDeleted?: Date | string | null;
}