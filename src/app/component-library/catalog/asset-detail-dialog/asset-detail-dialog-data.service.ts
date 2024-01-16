import {Injectable} from '@angular/core';
import {DataOffer} from '../../../core/services/models/data-offer';
import {UiAssetMapped} from '../../../core/services/models/ui-asset-mapped';
import {CatalogDataOfferMapped} from '../../../routes/broker-ui/catalog-page/catalog-page/mapping/catalog-page-result-mapped';
import {ContractAgreementCardMapped} from '../../../routes/connector-ui/contract-agreement-page/contract-agreement-cards/contract-agreement-card-mapped';
import {
  AssetDetailDialogData,
  OnAssetEditClickFn,
} from './asset-detail-dialog-data';
import {AssetPropertyGridGroupBuilder} from './asset-property-grid-group-builder';

@Injectable()
export class AssetDetailDialogDataService {
  constructor(
    private assetPropertyGridGroupBuilder: AssetPropertyGridGroupBuilder,
  ) {}

  assetDetailsReadonly(asset: UiAssetMapped): AssetDetailDialogData {
    const propertyGridGroups = [
      this.assetPropertyGridGroupBuilder.buildAssetPropertiesGroup(asset, null),
      this.assetPropertyGridGroupBuilder.buildAdditionalPropertiesGroup(asset),
    ].filter((it) => it.properties.length);

    return {
      type: 'asset-details',
      asset,
      propertyGridGroups,
    };
  }

  assetDetailsEditable(
    asset: UiAssetMapped,
    opts: {onAssetEditClick: OnAssetEditClickFn},
  ): AssetDetailDialogData {
    return {
      ...this.assetDetailsReadonly(asset),
      showDeleteButton: true,
      showEditButton: true,
      onAssetEditClick: opts.onAssetEditClick,
    };
  }

  dataOfferDetails(dataOffer: DataOffer): AssetDetailDialogData {
    const asset = dataOffer.asset;
    const propertyGridGroups = [
      this.assetPropertyGridGroupBuilder.buildAssetPropertiesGroup(asset, null),
      this.assetPropertyGridGroupBuilder.buildAdditionalPropertiesGroup(asset),
    ].filter((it) => it.properties.length);

    return {
      type: 'data-offer',
      asset: asset,
      dataOffer,
      propertyGridGroups,
    };
  }

  contractAgreementDetails(
    contractAgreement: ContractAgreementCardMapped,
  ): AssetDetailDialogData {
    const asset = contractAgreement.asset;

    const propertyGridGroups = [
      this.assetPropertyGridGroupBuilder.buildContractAgreementGroup(
        contractAgreement,
      ),
      this.assetPropertyGridGroupBuilder.buildContractPolicyGroup(
        contractAgreement.contractPolicy,
        asset.title,
      ),
      this.assetPropertyGridGroupBuilder.buildAssetPropertiesGroup(
        asset,
        'Asset',
      ),
      this.assetPropertyGridGroupBuilder.buildAdditionalPropertiesGroup(asset),
    ].filter((it) => it.properties.length);

    return {
      type: 'contract-agreement',
      asset: contractAgreement.asset,
      contractAgreement,
      propertyGridGroups,
    };
  }

  brokerDataOfferDetails(
    dataOffer: CatalogDataOfferMapped,
  ): AssetDetailDialogData {
    const asset = dataOffer.asset;

    const propertyGridGroups = [
      this.assetPropertyGridGroupBuilder.buildBrokerDataOfferGroup(dataOffer),
      this.assetPropertyGridGroupBuilder.buildAssetPropertiesGroup(
        asset,
        'Asset',
      ),
      this.assetPropertyGridGroupBuilder.buildAdditionalPropertiesGroup(asset),
      ...dataOffer.contractOffers.map((contractOffer, i) =>
        this.assetPropertyGridGroupBuilder.buildBrokerContractOfferGroup(
          asset,
          contractOffer,
          i,
          dataOffer.contractOffers.length,
        ),
      ),
    ].filter((it) => it.properties.length);

    return {
      type: 'broker-data-offer',
      asset: dataOffer.asset,
      brokerDataOffer: dataOffer,
      propertyGridGroups,
    };
  }
}
