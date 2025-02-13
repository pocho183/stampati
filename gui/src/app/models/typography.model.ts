import { StampatoFormat } from "./stampato.model";

export interface TypographyToProcessModel {
  barcode: string;
  format: StampatoFormat;
  legislaturaId: string;
  dataDeleted?: Date | string | null;
}