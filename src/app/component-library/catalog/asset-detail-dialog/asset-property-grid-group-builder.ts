import {Injectable} from '@angular/core';
import {CatalogContractOffer} from '@sovity.de/broker-server-client';
import {UiPolicy} from '@sovity.de/edc-client';
import {ActiveFeatureSet} from '../../../core/config/active-feature-set';
import {Asset} from '../../../core/services/models/asset';
import {BrokerDataOffer} from '../../../routes/broker-ui/catalog-page/catalog-page/mapping/broker-data-offer';
import {ContractAgreementCardMapped} from '../../../routes/connector-ui/contract-agreement-page/contract-agreement-cards/contract-agreement-card-mapped';
import {JsonDialogService} from '../../json-dialog/json-dialog/json-dialog.service';
import {PropertyGridGroup} from '../../property-grid/property-grid-group/property-grid-group';
import {PropertyGridField} from '../../property-grid/property-grid/property-grid-field';
import {PropertyGridFieldService} from '../../property-grid/property-grid/property-grid-field.service';
import {formatDateAgo} from '../../ui-elements/ago/formatDateAgo';
import {
  getOnlineStatusColor,
  getOnlineStatusIcon,
} from '../icon-with-online-status/online-status-utils';
import {PolicyPropertyFieldBuilder} from './policy-property-field-builder';

@Injectable()
export class AssetPropertyGridGroupBuilder {
  constructor(
    private activeFeatureSet: ActiveFeatureSet,
    private propertyGridUtils: PropertyGridFieldService,
    private jsonDialogService: JsonDialogService,
    private policyPropertyFieldBuilder: PolicyPropertyFieldBuilder,
  ) {}

  buildAssetPropertiesGroup(
    asset: Asset,
    groupLabel: string | null,
  ): PropertyGridGroup {
    const fields: PropertyGridField[] = [
      {
        icon: 'category',
        label: 'ID',
        ...this.propertyGridUtils.guessValue(asset.assetId),
      },
      {
        icon: 'file_copy',
        label: 'Version',
        ...this.propertyGridUtils.guessValue(asset.version),
      },
      {
        icon: 'language',
        label: 'Language',
        ...this.propertyGridUtils.guessValue(asset.language?.label),
      },
      {
        icon: 'apartment',
        label: 'Publisher',
        ...this.propertyGridUtils.guessValue(asset.publisherHomepage),
      },
      {
        icon: 'bookmarks',
        label: 'Endpoint Documentation',
        ...this.propertyGridUtils.guessValue(asset.landingPageUrl),
      },
      {
        icon: 'gavel',
        label: 'Standard License',
        ...this.propertyGridUtils.guessValue(asset.licenseUrl),
      },
      this.buildConnectorEndpointField(asset.connectorEndpoint),
      {
        icon: 'account_circle',
        label: 'Organization',
        ...this.propertyGridUtils.guessValue(asset.creatorOrganizationName),
      },
      {
        icon: 'category',
        label: 'Participant ID',
        ...this.propertyGridUtils.guessValue(asset.participantId),
      },
      ...this.buildHttpDatasourceFields(asset),
    ];

    if (this.activeFeatureSet.hasMdsFields()) {
      fields.push(...this.buildMdsProperties(asset, true));
    }

    return {
      groupLabel,
      properties: fields,
    };
  }

  private buildHttpDatasourceFields(asset: Asset): PropertyGridField[] {
    const fields: PropertyGridField[] = [];

    const hints: {label: string; value: boolean | undefined}[] = [
      {label: 'Method', value: asset.httpDatasourceHintsProxyMethod},
      {label: 'Path', value: asset.httpDatasourceHintsProxyPath},
      {label: 'Query Params', value: asset.httpDatasourceHintsProxyQueryParams},
      {label: 'Body', value: asset.httpDatasourceHintsProxyBody},
    ];

    if (hints.some((hint) => hint.value != null)) {
      const text = hints.some((hint) => hint.value)
        ? hints
            .filter((hint) => hint.value)
            .map((hint) => hint.label)
            .join(', ')
        : 'Disabled';

      fields.push({
        icon: 'api',
        label: 'HTTP Data Source Parameterization',
        text,
      });
    }

    if (asset.mediaType) {
      fields.push({
        icon: 'category',
        label: 'Content Type',
        ...this.propertyGridUtils.guessValue(asset.mediaType),
      });
    }

    return fields;
  }

  buildAdditionalPropertiesGroup(asset: Asset): PropertyGridGroup {
    const fields: PropertyGridField[] = [];

    if (!this.activeFeatureSet.hasMdsFields()) {
      fields.push(...this.buildMdsProperties(asset, false));
    }

    fields.push(
      ...asset.mergedAdditionalProperties.map((prop) => {
        return {
          icon: 'category ',
          label: prop.key,
          labelTitle: prop.key,
          ...this.propertyGridUtils.guessValue(prop.value),
        };
      }),
    );

    return {
      groupLabel: 'Additional Properties',
      properties: fields,
    };
  }

  buildMdsProperties(asset: Asset, includeEmpty: boolean): PropertyGridField[] {
    const fields: PropertyGridField[] = [];
    if (includeEmpty || asset.transportMode) {
      fields.push({
        icon: 'commute',
        label: 'Transport Mode',
        ...this.propertyGridUtils.guessValue(asset.transportMode?.label),
      });
    }
    if (includeEmpty || asset.dataCategory) {
      fields.push({
        icon: 'commute',
        label: 'Data Category',
        ...this.propertyGridUtils.guessValue(asset.dataCategory?.label),
      });
    }
    if (includeEmpty || asset.dataSubcategory) {
      fields.push({
        icon: 'commute',
        label: 'Data Subcategory',
        ...this.propertyGridUtils.guessValue(asset.dataSubcategory?.label),
      });
    }
    if (includeEmpty || asset.dataModel) {
      fields.push({
        icon: 'category',
        label: 'Data Model',
        ...this.propertyGridUtils.guessValue(asset.dataModel),
      });
    }
    if (includeEmpty || asset.geoReferenceMethod) {
      fields.push({
        icon: 'commute',
        label: 'Geo Reference Method',
        ...this.propertyGridUtils.guessValue(asset.geoReferenceMethod),
      });
    }
    return fields;
  }

  buildBrokerContractOfferGroup(
    asset: Asset,
    contractOffer: CatalogContractOffer,
    i: number,
    total: number,
  ) {
    const groupLabel = `Contract Offer ${total > 1 ? i + 1 : ''}`;
    const properties: PropertyGridField[] = [
      {
        icon: 'policy',
        label: 'Contract Policy',
        text: 'Show Policy Details',
        onclick: () =>
          this.jsonDialogService.showJsonDetailDialog({
            title: `${groupLabel} Contract Policy)`,
            subtitle: asset.title,
            icon: 'policy',
            objectForJson: JSON.parse(
              contractOffer.contractPolicy.legacyPolicy ?? 'null',
            ),
          }),
      },
      {
        icon: 'category',
        label: 'Contract Offer ID',
        ...this.propertyGridUtils.guessValue(contractOffer.contractOfferId),
      },
      {
        icon: 'category',
        label: 'Created At',
        ...this.propertyGridUtils.guessValue(
          this.propertyGridUtils.formatDate(contractOffer.createdAt),
        ),
      },
    ];
    return {groupLabel, properties};
  }

  buildContractAgreementGroup(contractAgreement: ContractAgreementCardMapped) {
    const properties: PropertyGridField[] = [
      {
        icon: 'category',
        label: 'Signed',
        ...this.propertyGridUtils.guessValue(
          this.propertyGridUtils.formatDate(
            contractAgreement.contractSigningDate,
          ),
        ),
      },
      {
        icon: 'policy',
        label: 'Direction',
        ...this.propertyGridUtils.guessValue(contractAgreement.direction),
      },
      {
        icon: 'link',
        label: 'Other Connector Endpoint',
        ...this.propertyGridUtils.guessValue(
          contractAgreement.counterPartyAddress,
        ),
      },
      {
        icon: 'link',
        label: 'Other Connector Participant ID',
        ...this.propertyGridUtils.guessValue(contractAgreement.counterPartyId),
      },
      {
        icon: 'category',
        label: 'Contract Agreement ID',
        ...this.propertyGridUtils.guessValue(
          contractAgreement.contractAgreementId,
        ),
      },
    ];

    if (contractAgreement.isConsumingLimitsEnforced) {
      properties.push({
        icon: contractAgreement.canTransfer ? 'sync' : 'sync_disabled',
        label: 'Status',
        tooltip: contractAgreement.statusTooltipText,
        textIconAfter: contractAgreement.statusTooltipText ? 'help' : null,
        text: contractAgreement.statusText,
        additionalClasses: contractAgreement.canTransfer ? '' : 'text-warn',
      });
    }

    return {
      groupLabel: 'Contract Agreement',
      properties,
    };
  }

  buildContractPolicyGroup(
    contractPolicy: UiPolicy,
    subtitle: string,
  ): PropertyGridGroup {
    return {
      groupLabel: 'Contract Policy',
      properties: this.policyPropertyFieldBuilder.buildPolicyPropertyFields(
        contractPolicy,
        'Contract Policy JSON-LD',
        subtitle,
      ),
    };
  }

  buildBrokerDataOfferGroup(dataOffer: BrokerDataOffer): PropertyGridGroup {
    const lastUpdate = formatDateAgo(
      dataOffer.connectorOfflineSinceOrLastUpdatedAt,
    );
    return {
      groupLabel: null,
      properties: [
        {
          icon: 'today',
          label: 'Updated At',
          ...this.propertyGridUtils.guessValue(
            this.propertyGridUtils.formatDate(dataOffer.updatedAt),
          ),
        },
        {
          ...this.buildConnectorEndpointField(dataOffer.connectorEndpoint),
          copyButton: true,
        },
        {
          icon: getOnlineStatusIcon(dataOffer.connectorOnlineStatus),
          label: 'Status',
          labelTitle: `Last updated ${lastUpdate}`,
          text:
            dataOffer.connectorOnlineStatus == 'ONLINE'
              ? `Online`
              : `Offline since ${lastUpdate}`,
          additionalClasses: getOnlineStatusColor(
            dataOffer.connectorOnlineStatus,
          ),
          additionalIconClasses: getOnlineStatusColor(
            dataOffer.connectorOnlineStatus,
          ),
        },
      ],
    };
  }

  buildConnectorEndpointField(endpoint: string): PropertyGridField {
    return {
      icon: 'link',
      label: 'Connector Endpoint',
      ...this.propertyGridUtils.guessValue(endpoint),
    };
  }
}
