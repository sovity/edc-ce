/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Injectable} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {DataOffer} from '../../../core/services/models/data-offer';
import {UiAssetMapped} from '../../../core/services/models/ui-asset-mapped';
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
    private translateService: TranslateService,
  ) {}

  assetDetailsReadonly(asset: UiAssetMapped): AssetDetailDialogData {
    const propertyGridGroups = [
      this.assetPropertyGridGroupBuilder.buildAssetPropertiesGroup(asset, null),
      ...this.assetPropertyGridGroupBuilder.buildAdditionalPropertiesGroups(
        asset,
      ),
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
    const assetDetailsReadonly = this.assetDetailsReadonly(asset);
    return {
      ...assetDetailsReadonly,
      propertyGridGroups: [
        ...assetDetailsReadonly.propertyGridGroups,
        ...this.assetPropertyGridGroupBuilder.buildOnRequestContactInformation(
          asset,
        ),
      ],
      showDeleteButton: true,
      showEditButton: true,
      onAssetEditClick: opts.onAssetEditClick,
    };
  }

  dataOfferDetails(
    dataOffer: DataOffer,
    consumingLimitsExceeded: boolean,
  ): AssetDetailDialogData {
    const asset = dataOffer.asset;
    const propertyGridGroups = [
      this.assetPropertyGridGroupBuilder.buildAssetPropertiesGroup(asset, null),
      ...this.assetPropertyGridGroupBuilder.buildAdditionalPropertiesGroups(
        asset,
      ),
      ...this.assetPropertyGridGroupBuilder.buildOnRequestContactInformation(
        asset,
        true,
      ),
    ].filter((it) => it.properties.length);

    return {
      type: 'data-offer',
      asset: asset,
      dataOffer,
      propertyGridGroups,
      consumingLimitsExceeded,
    };
  }

  contractAgreementDetails(
    contractAgreement: ContractAgreementCardMapped,
    refreshCallback: () => void,
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
        this.translateService.instant('general.asset'),
      ),
      ...this.assetPropertyGridGroupBuilder.buildAdditionalPropertiesGroups(
        asset,
      ),
    ].filter((it) => it.properties.length);

    return {
      type: 'contract-agreement',
      asset: contractAgreement.asset,
      contractAgreement,
      propertyGridGroups,
      refreshCallback,
    };
  }
}
