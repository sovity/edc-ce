import {Injectable} from '@angular/core';
import {UiContractOffer, UiDataOffer} from '@sovity.de/edc-client';
import {PolicyPropertyFieldBuilder} from '../../../../component-library/catalog/asset-detail-dialog/policy-property-field-builder';
import {PropertyGridFieldService} from '../../../../component-library/property-grid/property-grid/property-grid-field.service';
import {AssetBuilder} from '../../../../core/services/asset-builder';
import {Asset} from '../../../../core/services/models/asset';
import {ContractOffer} from '../../../../core/services/models/contract-offer';
import {DataOffer} from '../../../../core/services/models/data-offer';

@Injectable()
export class DataOfferBuilder {
  constructor(
    private assetBuilder: AssetBuilder,
    private policyPropertyFieldBuilder: PolicyPropertyFieldBuilder,
    private propertyGridFieldService: PropertyGridFieldService,
  ) {}
  buildDataOffer(dataOffer: UiDataOffer): DataOffer {
    let asset = this.assetBuilder.buildAsset(
      dataOffer.asset,
      dataOffer.endpoint,
    );
    return {
      ...dataOffer,
      asset,
      contractOffers: dataOffer.contractOffers.map(
        (contractOffer, iContractOffer): ContractOffer => {
          return this.buildContractOffer(
            dataOffer,
            asset,
            contractOffer,
            iContractOffer,
          );
        },
      ),
    };
  }

  private buildContractOffer(
    dataOffer: UiDataOffer,
    asset: Asset,
    contractOffer: UiContractOffer,
    iContractOffer: number,
  ): ContractOffer {
    const groupLabel = `Contract Offer ${
      dataOffer.contractOffers.length > 1 ? iContractOffer + 1 : ''
    }`;
    return {
      ...contractOffer,
      properties: [
        {
          icon: 'category',
          label: 'Contract Offer ID',
          ...this.propertyGridFieldService.guessValue(
            contractOffer.contractOfferId,
          ),
        },
        ...this.policyPropertyFieldBuilder.buildPolicyPropertyFields(
          contractOffer.policy,
          `${groupLabel} Contract Policy JSON-LD`,
          asset.name,
        ),
      ],
    };
  }
}
