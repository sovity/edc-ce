import {Policy} from '../../../core/services/api/legacy-managent-api-client';
import {Asset} from '../../../core/services/models/asset';
import {ContractOffer} from '../../../core/services/models/contract-offer';
import {BrokerDataOffer} from '../../../routes/broker-ui/catalog-browser-page/catalog-page/mapping/broker-data-offer';
import {ContractAgreementCardMapped} from '../../../routes/connector-ui/contract-agreement-page/contract-agreement-cards/contract-agreement-card-mapped';

export class AssetDetailDialogData {
  constructor(
    public mode:
      | 'asset-details'
      | 'contract-offer'
      | 'contract-agreement'
      | 'broker-data-offer',
    public asset: Asset,
    public contractOffer: ContractOffer | null,
    public contractAgreement: ContractAgreementCardMapped | null,
    public brokerDataOffer: BrokerDataOffer | null,
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
      null,
      contractAgreement.contractPolicy.legacyPolicy,
      false,
    );
  }

  static forBrokerDataOffer(dataOffer: BrokerDataOffer): AssetDetailDialogData {
    return new AssetDetailDialogData(
      'broker-data-offer',
      dataOffer.asset,
      null,
      null,
      dataOffer,
      dataOffer.policy[0].legacyPolicy as Policy,
      false,
    );
  }
}
