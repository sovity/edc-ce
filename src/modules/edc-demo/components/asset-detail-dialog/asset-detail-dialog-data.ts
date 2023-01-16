import {Asset} from "../../models/asset";
import {ContractOffer} from "../../models/contract-offer";

export class AssetDetailDialogData {
  constructor(
    public mode: 'asset-details' | 'contract-offer',
    public asset: Asset,
    public contractOffer: ContractOffer | null
  ) {
  }

  static forAssetDetails(asset: Asset): AssetDetailDialogData {
    return new AssetDetailDialogData('asset-details', asset, null)
  }

  static forContractOffer(contractOffer: ContractOffer): AssetDetailDialogData {
    return new AssetDetailDialogData('contract-offer', contractOffer.asset!, contractOffer)
  }
}
