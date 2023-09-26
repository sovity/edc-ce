import {Asset} from 'src/app/core/services/models/asset';
import {DataOffer} from '../../../core/services/models/data-offer';
import {BrokerDataOffer} from '../../../routes/broker-ui/catalog-page/catalog-page/mapping/broker-data-offer';
import {ContractAgreementCardMapped} from '../../../routes/connector-ui/contract-agreement-page/contract-agreement-cards/contract-agreement-card-mapped';
import {PropertyGridGroup} from '../../property-grid/property-grid-group/property-grid-group';

export interface AssetDetailDialogData {
  type:
    | 'asset-details'
    | 'data-offer'
    | 'contract-agreement'
    | 'broker-data-offer';
  propertyGridGroups: PropertyGridGroup[];
  asset: Asset;
  dataOffer?: DataOffer;
  contractAgreement?: ContractAgreementCardMapped;
  brokerDataOffer?: BrokerDataOffer;
  showDeleteButton?: boolean;
}
