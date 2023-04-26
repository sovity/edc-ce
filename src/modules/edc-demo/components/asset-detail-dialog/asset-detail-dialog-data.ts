import {Policy} from '../../../edc-dmgmt-client';
import {Asset} from '../../models/asset';
import {ContractOffer} from '../../models/contract-offer';
import {ContractAgreementCardMapped} from '../contract-agreement-cards/contract-agreement-card-mapped';

export class AssetDetailDialogData {
  constructor(
    public mode: 'asset-details' | 'contract-offer' | 'contract-agreement',
    public asset: Asset,
    public contractOffer: ContractOffer | null,
    public contractAgreement: ContractAgreementCardMapped | null,
    public policy: Policy | null,
    public allowDelete: boolean,
  ) {}

  static forAssetDetails(
    asset: Asset,
    allowDelete: boolean,
  ): AssetDetailDialogData {
    return new AssetDetailDialogData(
      'asset-details',
      asset,
      null,
      null,
      null,
      allowDelete,
    );
  }

  static forContractOffer(contractOffer: ContractOffer): AssetDetailDialogData {
    return new AssetDetailDialogData(
      'contract-offer',
      contractOffer.asset,
      contractOffer,
      null,
      contractOffer.policy,
      false,
    );
  }

  static forContractAgreement(
    contractAgreement: ContractAgreementCardMapped,
  ): AssetDetailDialogData {
    return new AssetDetailDialogData(
      'contract-agreement',
      contractAgreement.asset,
      null,
      contractAgreement,
      contractAgreement.contractPolicy.legacyPolicy,
      false,
    );
  }
}
