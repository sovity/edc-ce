import {Asset} from '../../models/asset';
import {ContractOffer} from '../../models/contract-offer';

export class AssetDetailDialogData {
  constructor(
    public mode: 'asset-details' | 'contract-offer',
    public asset: Asset,
    public contractOffer: ContractOffer | null,
    public allowDelete: boolean,
  ) {}

  static forAssetDetails(
    asset: Asset,
    allowDelete: boolean,
  ): AssetDetailDialogData {
    return new AssetDetailDialogData('asset-details', asset, null, allowDelete);
  }

  static forContractOffer(contractOffer: ContractOffer): AssetDetailDialogData {
    return new AssetDetailDialogData(
      'contract-offer',
      contractOffer.asset!,
      contractOffer,
      false,
    );
  }
}
