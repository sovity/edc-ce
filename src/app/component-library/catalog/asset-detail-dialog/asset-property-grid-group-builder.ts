import {Injectable} from '@angular/core';
import {UiPolicy} from '@sovity.de/edc-client';
import {ActiveFeatureSet} from '../../../core/config/active-feature-set';
import {UiAssetMapped} from '../../../core/services/models/ui-asset-mapped';
import {ParticipantIdLocalization} from '../../../core/services/participant-id-localization';
import {ContractAgreementCardMapped} from '../../../routes/connector-ui/contract-agreement-page/contract-agreement-cards/contract-agreement-card-mapped';
import {ConditionsForUseDialogService} from '../../conditions-for-use-dialog/conditions-for-use-dialog/conditions-for-use-dialog.service';
import {PropertyGridGroup} from '../../property-grid/property-grid-group/property-grid-group';
import {PropertyGridField} from '../../property-grid/property-grid/property-grid-field';
import {PropertyGridFieldService} from '../../property-grid/property-grid/property-grid-field.service';
import {UrlListDialogService} from '../../url-list-dialog/url-list-dialog/url-list-dialog.service';
import {PolicyPropertyFieldBuilder} from './policy-property-field-builder';

@Injectable()
export class AssetPropertyGridGroupBuilder {
  constructor(
    private participantIdLocalization: ParticipantIdLocalization,
    private activeFeatureSet: ActiveFeatureSet,
    private propertyGridUtils: PropertyGridFieldService,
    private urlListDialogService: UrlListDialogService,
    private conditionsForUseDialogService: ConditionsForUseDialogService,
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

  buildAdditionalPropertiesGroups(asset: UiAssetMapped): PropertyGridGroup[] {
    const additionalProperties: PropertyGridField[] = [];
    if (!this.activeFeatureSet.hasMdsFields()) {
      additionalProperties.push(...this.buildMdsProperties(asset));
    }

    const customProperties: PropertyGridField[] = [
      asset.customJsonProperties,
      asset.customJsonLdProperties,
    ]
      .flat()
      .map((prop) => {
        return {
          icon: 'category ',
          label: prop.key,
          labelTitle: prop.key,
          ...this.propertyGridUtils.guessValue(prop.value),
        };
      });

    const privateCustomProperties: PropertyGridField[] = [
      asset.privateCustomJsonProperties,
      asset.privateCustomJsonLdProperties,
    ]
      .flat()
      .map((prop) => {
        return {
          icon: 'category ',
          label: prop.key,
          labelTitle: prop.key,
          ...this.propertyGridUtils.guessValue(prop.value),
        };
      });

    return [
      {
        groupLabel: 'Additional Properties',
        properties: additionalProperties,
      },
      {
        groupLabel: 'Custom Properties',
        properties: customProperties,
      },
      {
        groupLabel: 'Private Properties',
        properties: privateCustomProperties,
      },
    ];
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
    if (asset.nutsLocations?.length) {
      fields.push(this.buildNutsLocationsField(asset.nutsLocations));
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
      fields.push(
        this.buildConditionsForUseField(asset.conditionsForUse, asset.title),
      );
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

  buildContractAgreementGroup(contractAgreement: ContractAgreementCardMapped) {
    const properties: PropertyGridField[] = [
      {
        icon: 'category',
        label: 'Signed',
        ...this.propertyGridUtils.guessValue(
          this.propertyGridUtils.formatDateWithTime(
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
        icon: contractAgreement.isTerminated ? 'sync_disabled' : 'sync',
        label: 'Status',
        additionalClasses: contractAgreement.isTerminated ? 'text-warn' : '',
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

  buildConditionsForUseField(
    conditionsForUse: string,
    title: string,
  ): PropertyGridField {
    return {
      icon: 'description',
      label: 'Conditions For Use',
      text: 'Show Conditions For Use',
      onclick: () =>
        this.conditionsForUseDialogService.showConditionsForUseDialog({
          title: 'Conditions For Use',
          subtitle: title,
          icon: 'description',
          description: conditionsForUse,
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
      return `Start: ${this.propertyGridUtils.formatDate(start)}`;
    }

    if (!start) {
      return `End: ${this.propertyGridUtils.formatDate(end)}`;
    }

    return `${this.propertyGridUtils.formatDate(
      start,
    )} - ${this.propertyGridUtils.formatDate(end)}`;
  }

  buildOnRequestContactInformation(
    asset: UiAssetMapped,
    isMailHidden = false,
  ): PropertyGridGroup[] {
    if (asset.dataSourceAvailability === 'LIVE') {
      return [];
    }
    return [
      {
        groupLabel: 'Contact Information',
        properties: [
          {
            icon: 'mail',
            label: 'Contact E-Mail Address',
            copyButton: true,
            hideFieldValue: isMailHidden,
            ...this.propertyGridUtils.guessValue(asset.onRequestContactEmail),
          },
          {
            icon: 'subject',
            label: 'Preferred E-Mail Subject',
            copyButton: true,
            ...this.propertyGridUtils.guessValue(
              asset.onRequestContactEmailSubject,
            ),
          },
        ],
      },
    ];
  }
}
