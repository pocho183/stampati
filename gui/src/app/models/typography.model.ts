import { StampatoFormat } from "./stampato.model";

export interface TypographyToProcessModel {
  barcode: string;
  format: StampatoFormat;
  dataDeleted?: Date | null;
}