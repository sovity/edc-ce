import {Injectable} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {UiContractOffer, UiDataOffer} from '@sovity.de/edc-client';
import {AssetBuilder} from '../../../../core/services/asset-builder';
import {ContractOffer} from '../../../../core/services/models/contract-offer';
import {DataOffer} from '../../../../core/services/models/data-offer';
import {UiAssetMapped} from '../../../../core/services/models/ui-asset-mapped';
import {PolicyPropertyFieldBuilder} from '../../../../shared/business/asset-detail-dialog/policy-property-field-builder';

@Injectable()
export class DataOfferBuilder {
  constructor(
    private assetBuilder: AssetBuilder,
    private policyPropertyFieldBuilder: PolicyPropertyFieldBuilder,
    private translateService: TranslateService,
  ) {}
  buildDataOffer(dataOffer: UiDataOffer): DataOffer {
    const asset = this.assetBuilder.buildAsset(dataOffer.asset);
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
    asset: UiAssetMapped,
    contractOffer: UiContractOffer,
    iContractOffer: number,
  ): ContractOffer {
    const groupLabel = this.getGroupLabel(
      iContractOffer,
      dataOffer.contractOffers.length,
    );
    return {
      ...contractOffer,
      properties: this.policyPropertyFieldBuilder.buildPolicyPropertyFields(
        contractOffer.policy,
        `${groupLabel} Contract Policy JSON-LD`,
        asset.title,
      ),
    };
  }

  private getGroupLabel(i: number, total: number) {
    const contract = this.translateService.instant(
      'catalog_browser_page.contract',
    );
    return `${contract}  ${total > 1 ? i : ''}`;
  }
}
