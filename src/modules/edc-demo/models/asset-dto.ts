/**
 * Asset (API Model)
 */
export interface AssetDto {
  id: string;
  createdAt: number;
  properties: Record<string, string | null>;
}
