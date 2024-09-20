import {UiAssetMapped} from '../../../../core/services/models/ui-asset-mapped';

export interface AssetCreateDialogResult {
  /**
   * Updated asset list for the asset page
   */
  refreshedList: UiAssetMapped[];

  /**
   * The updated / created asset
   */
  asset: UiAssetMapped;
}
