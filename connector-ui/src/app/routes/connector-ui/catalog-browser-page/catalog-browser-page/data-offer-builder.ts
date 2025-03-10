/*
 * Copyright 2025 sovity GmbH
 * Copyright 2024 Fraunhofer Institute for Applied Information Technology FIT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 *     Fraunhofer FIT - contributed initial internationalization support
 */
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
