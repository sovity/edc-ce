import {Injectable} from '@angular/core';
import {CatalogContractOffer} from '@sovity.de/broker-server-client';
import {UiPolicy} from '@sovity.de/edc-client';
import {ActiveFeatureSet} from '../../../core/config/active-feature-set';
import {UiAssetMapped} from '../../../core/services/models/ui-asset-mapped';
import {ParticipantIdLocalization} from '../../../core/services/participant-id-localization';
import {CatalogDataOfferMapped} from '../../../routes/broker-ui/catalog-page/catalog-page/mapping/catalog-page-result-mapped';
import {ContractAgreementCardMapped} from '../../../routes/connector-ui/contract-agreement-page/contract-agreement-cards/contract-agreement-card-mapped';
import {JsonDialogService} from '../../json-dialog/json-dialog/json-dialog.service';
import {PropertyGridGroup} from '../../property-grid/property-grid-group/property-grid-group';
import {PropertyGridField} from '../../property-grid/property-grid/property-grid-field';
import {PropertyGridFieldService} from '../../property-grid/property-grid/property-grid-field.service';
import {formatDateAgo} from '../../ui-elements/ago/formatDateAgo';
import {UrlListDialogService} from '../../url-list-dialog/url-list-dialog/url-list-dialog.service';
import {
  getOnlineStatusColor,
  getOnlineStatusIcon,
} from '../icon-with-online-status/online-status-utils';
import {PolicyPropertyFieldBuilder} from './policy-property-field-builder';

@Injectable()
export class AssetPropertyGridGroupBuilder {
  constructor(
    private participantIdLocalization: ParticipantIdLocalization,
    private activeFeatureSet: ActiveFeatureSet,
    private propertyGridUtils: PropertyGridFieldService,
    private jsonDialogService: JsonDialogService,
    private urlListDialogService: UrlListDialogService,
    private policyPropertyFieldBuilder: PolicyPropertyFieldBuilder,
  ) {}

  buildAssetPropertiesGroup(
    asset: UiAssetMapped,
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
      {
        icon: 'category',
        label: this.participantIdLocalization.participantId,
        ...this.propertyGridUtils.guessValue(asset.participantId),
      },
      {
        icon: 'account_circle',
        label: 'Organization',
        ...this.propertyGridUtils.guessValue(asset.creatorOrganizationName),
      },
      this.buildConnectorEndpointField(asset.connectorEndpoint),
      ...this.buildHttpDatasourceFields(asset),
    ];

    if (this.activeFeatureSet.hasMdsFields()) {
      fields.push(...this.buildMdsProperties(asset));
    }

    return {
      groupLabel,
      properties: fields,
    };
  }

  private buildHttpDatasourceFields(asset: UiAssetMapped): PropertyGridField[] {
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

  buildAdditionalPropertiesGroup(asset: UiAssetMapped): PropertyGridGroup {
    const fields: PropertyGridField[] = [];

    if (!this.activeFeatureSet.hasMdsFields()) {
      fields.push(...this.buildMdsProperties(asset));
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

  buildMdsProperties(asset: UiAssetMapped): PropertyGridField[] {
    const fields: PropertyGridField[] = [];
    if (asset.transportMode) {
      fields.push({
        icon: 'commute',
        label: 'Transport Mode',
        ...this.propertyGridUtils.guessValue(asset.transportMode?.label),
      });
    }
    if (asset.dataCategory) {
      fields.push({
        icon: 'commute',
        label: 'Data Category',
        ...this.propertyGridUtils.guessValue(asset.dataCategory?.label),
      });
    }
    if (asset.dataSubcategory) {
      fields.push({
        icon: 'commute',
        label: 'Data Subcategory',
        ...this.propertyGridUtils.guessValue(asset.dataSubcategory?.label),
      });
    }
    if (asset.dataModel) {
      fields.push({
        icon: 'category',
        label: 'Data Model',
        ...this.propertyGridUtils.guessValue(asset.dataModel),
      });
    }
    if (asset.geoReferenceMethod) {
      fields.push({
        icon: 'commute',
        label: 'Geo Reference Method',
        ...this.propertyGridUtils.guessValue(asset.geoReferenceMethod),
      });
    }
    if (asset.geoLocation) {
      fields.push({
        icon: 'location_on',
        label: 'Geo Location',
        ...this.propertyGridUtils.guessValue(asset.geoLocation),
      });
    }
    if (asset.nutsLocation?.length) {
      fields.push(this.buildNutsLocationsField(asset.nutsLocation));
    }
    if (asset.sovereignLegalName) {
      fields.push({
        icon: 'account_balance',
        label: 'Sovereign',
        ...this.propertyGridUtils.guessValue(asset.sovereignLegalName),
      });
    }
    if (asset.dataSampleUrls?.length) {
      fields.push(
        this.buildDataSampleUrlsField(asset.dataSampleUrls, asset.title),
      );
    }
    if (asset.referenceFileUrls?.length) {
      fields.push(
        this.buildReferenceFileUrlsField(
          asset.referenceFileUrls,
          asset.referenceFilesDescription,
          asset.title,
        ),
      );
    }
    if (asset.conditionsForUse) {
      fields.push({
        icon: 'description',
        label: 'Conditions For Use',
        ...this.propertyGridUtils.guessValue(asset.conditionsForUse),
      });
    }
    if (asset.dataUpdateFrequency) {
      fields.push({
        icon: 'timelapse',
        label: 'Data Update Frequency',
        ...this.propertyGridUtils.guessValue(asset.dataUpdateFrequency),
      });
    }
    if (asset.temporalCoverageFrom || asset.temporalCoverageToInclusive) {
      fields.push({
        icon: 'today',
        label: 'Temporal Coverage',
        ...this.propertyGridUtils.guessValue(
          this.buildTemporalCoverageString(
            asset.temporalCoverageFrom,
            asset.temporalCoverageToInclusive,
          ),
        ),
      });
    }
    return fields;
  }

  buildBrokerContractOfferGroup(
    asset: UiAssetMapped,
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
              contractOffer.contractPolicy.policyJsonLd,
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
        icon: 'category',
        label: 'Contract Agreement ID',
        ...this.propertyGridUtils.guessValue(
          contractAgreement.contractAgreementId,
        ),
      },
      {
        icon: 'link',
        label: `Counter-Party ${this.participantIdLocalization.participantId}`,
        ...this.propertyGridUtils.guessValue(contractAgreement.counterPartyId),
      },
      {
        icon: 'link',
        label: 'Counter-Party Connector Endpoint',
        ...this.propertyGridUtils.guessValue(
          contractAgreement.counterPartyAddress,
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

  buildBrokerDataOfferGroup(
    dataOffer: CatalogDataOfferMapped,
  ): PropertyGridGroup {
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

  buildNutsLocationsField(locations: string[]): PropertyGridField {
    return {
      icon: 'location_on',
      label: 'NUTS Locations',
      text: locations.join(', '),
    };
  }

  buildDataSampleUrlsField(
    dataSampleUrls: string[],
    title: string,
  ): PropertyGridField {
    return {
      icon: 'attachment',
      label: 'Data Samples',
      text: 'Show Data Samples',
      onclick: () =>
        this.urlListDialogService.showUrlListDialog({
          title: `Data Samples`,
          subtitle: title,
          icon: 'attachment',
          urls: dataSampleUrls,
        }),
    };
  }

  buildReferenceFileUrlsField(
    referenceFileUrls: string[],
    description: string | undefined,
    title: string,
  ): PropertyGridField {
    return {
      icon: 'receipt',
      label: 'Reference Files',
      text: 'Show Reference Files',
      onclick: () =>
        this.urlListDialogService.showUrlListDialog({
          title: `Reference Files`,
          subtitle: title,
          icon: 'receipt',
          urls: referenceFileUrls,
          description: description,
        }),
    };
  }

  buildTemporalCoverageString(
    start: Date | undefined,
    end: Date | undefined,
  ): string {
    if (!end) {
      return `Start: ${start!.toLocaleDateString()}`;
    }

    if (!start) {
      return `End: ${end.toLocaleDateString()}`;
    }

    return `${start.toLocaleDateString()} - ${end.toLocaleDateString()}`;
  }
}
