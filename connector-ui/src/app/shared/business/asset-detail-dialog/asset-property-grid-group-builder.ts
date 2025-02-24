import {Injectable} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {UiPolicy} from '@sovity.de/edc-client';
import {ActiveFeatureSet} from '../../../core/config/active-feature-set';
import {UiAssetMapped} from '../../../core/services/models/ui-asset-mapped';
import {ParticipantIdLocalization} from '../../../core/services/participant-id-localization';
import {ContractAgreementCardMapped} from '../../../routes/connector-ui/contract-agreement-page/contract-agreement-cards/contract-agreement-card-mapped';
import {PropertyGridGroup} from '../../common/property-grid-group/property-grid-group';
import {PropertyGridField} from '../../common/property-grid/property-grid-field';
import {PropertyGridFieldService} from '../../common/property-grid/property-grid-field.service';
import {UrlListDialogService} from '../../common/url-list-dialog/url-list-dialog.service';
import {ConditionsForUseDialogService} from '../conditions-for-use-dialog/conditions-for-use-dialog.service';
import {PolicyPropertyFieldBuilder} from './policy-property-field-builder';

@Injectable()
export class AssetPropertyGridGroupBuilder {
  constructor(
    private activeFeatureSet: ActiveFeatureSet,
    private propertyGridUtils: PropertyGridFieldService,
    private urlListDialogService: UrlListDialogService,
    private conditionsForUseDialogService: ConditionsForUseDialogService,
    private policyPropertyFieldBuilder: PolicyPropertyFieldBuilder,
    private translateService: TranslateService,
    private participantIdLocalization: ParticipantIdLocalization,
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
        label: this.translateService.instant('general.language'),
        ...this.propertyGridUtils.guessValue(asset.language?.label),
      },
      {
        icon: 'apartment',
        label: this.translateService.instant('general.publisher'),
        ...this.propertyGridUtils.guessValue(asset.publisherHomepage),
      },
      {
        icon: 'bookmarks',
        label: this.translateService.instant('general.endpoint_doc'),
        ...this.propertyGridUtils.guessValue(asset.landingPageUrl),
      },
      {
        icon: 'gavel',
        label: this.translateService.instant('general.standard_license'),
        ...this.propertyGridUtils.guessValue(asset.licenseUrl),
      },
      {
        icon: 'category',
        label: this.translateService.instant(
          'component_library.participant_id',
        ),
        ...this.propertyGridUtils.guessValue(asset.participantId),
      },
      {
        icon: 'account_circle',
        label: this.translateService.instant('component_library.organization'),
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
      {
        label: this.translateService.instant('general.method'),
        value: asset.httpDatasourceHintsProxyMethod,
      },
      {
        label: this.translateService.instant('general.path'),
        value: asset.httpDatasourceHintsProxyPath,
      },
      {
        label: this.translateService.instant('general.params'),
        value: asset.httpDatasourceHintsProxyQueryParams,
      },
      {
        label: this.translateService.instant('general.body'),
        value: asset.httpDatasourceHintsProxyBody,
      },
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
        label: this.translateService.instant('component_library.http_param'),
        text,
      });
    }

    if (asset.mediaType) {
      fields.push({
        icon: 'category',
        label: this.translateService.instant('general.content_type'),
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
        groupLabel: this.translateService.instant(
          'general.additional_properties',
        ),
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
        label: this.translateService.instant('general.transport_mode'),
        ...this.propertyGridUtils.guessValue(asset.transportMode?.label),
      });
    }
    if (asset.dataCategory) {
      fields.push({
        icon: 'commute',
        label: this.translateService.instant('general.data_category'),
        ...this.propertyGridUtils.guessValue(asset.dataCategory?.label),
      });
    }
    if (asset.dataSubcategory) {
      fields.push({
        icon: 'commute',
        label: this.translateService.instant('general.data_subcategory'),
        ...this.propertyGridUtils.guessValue(asset.dataSubcategory?.label),
      });
    }
    if (asset.dataModel) {
      fields.push({
        icon: 'category',
        label: this.translateService.instant('general.data_model'),
        ...this.propertyGridUtils.guessValue(asset.dataModel),
      });
    }
    if (asset.geoReferenceMethod) {
      fields.push({
        icon: 'commute',
        label: this.translateService.instant('general.geo_reference_method'),
        ...this.propertyGridUtils.guessValue(asset.geoReferenceMethod),
      });
    }
    if (asset.geoLocation) {
      fields.push({
        icon: 'location_on',
        label: this.translateService.instant('general.geo_location'),
        ...this.propertyGridUtils.guessValue(asset.geoLocation),
      });
    }
    if (asset.nutsLocations?.length) {
      fields.push(this.buildNutsLocationsField(asset.nutsLocations));
    }
    if (asset.sovereignLegalName) {
      fields.push({
        icon: 'account_balance',
        label: this.translateService.instant('general.sovereign'),
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
        label: this.translateService.instant('general.frequency'),
        ...this.propertyGridUtils.guessValue(asset.dataUpdateFrequency),
      });
    }
    if (asset.temporalCoverageFrom || asset.temporalCoverageToInclusive) {
      fields.push({
        icon: 'today',
        label: this.translateService.instant('general.coverage'),
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
        label: this.translateService.instant('general.signed'),
        ...this.propertyGridUtils.guessValue(
          this.propertyGridUtils.formatDateWithTime(
            contractAgreement.contractSigningDate,
          ),
        ),
      },
      {
        icon: 'policy',
        label: this.translateService.instant('general.direction'),
        ...this.propertyGridUtils.guessValue(
          contractAgreement.direction === 'CONSUMING'
            ? this.translateService.instant('general.consuming')
            : this.translateService.instant('general.providing'),
        ),
      },
      {
        icon: 'category',
        label: this.translateService.instant('general.contract') + ' ID',
        ...this.propertyGridUtils.guessValue(
          contractAgreement.contractAgreementId,
        ),
      },
      {
        icon: 'link',
        label: `${this.translateService.instant('general.oth_connector')} ${
          this.participantIdLocalization.participantId
        }`,
        ...this.propertyGridUtils.guessValue(contractAgreement.counterPartyId),
      },
      {
        icon: 'link',
        label: this.translateService.instant(
          'transfer_history_page.counter_endpoint',
        ),
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
      groupLabel: this.translateService.instant('general.contract'),
      properties,
    };
  }

  buildContractPolicyGroup(
    contractPolicy: UiPolicy,
    subtitle: string,
  ): PropertyGridGroup {
    return {
      groupLabel: this.translateService.instant('general.contract_policy'),
      properties: this.policyPropertyFieldBuilder.buildPolicyPropertyFields(
        contractPolicy,
        this.translateService.instant('general.contract_policy') + ' JSON-LD',
        subtitle,
      ),
    };
  }

  buildConnectorEndpointField(endpoint: string): PropertyGridField {
    return {
      icon: 'link',
      label: this.translateService.instant('general.endpoint'),
      ...this.propertyGridUtils.guessValue(endpoint),
    };
  }

  buildNutsLocationsField(locations: string[]): PropertyGridField {
    return {
      icon: 'location_on',
      label: this.translateService.instant('general.nuts'),
      text: locations.join(', '),
    };
  }

  buildDataSampleUrlsField(
    dataSampleUrls: string[],
    title: string,
  ): PropertyGridField {
    return {
      icon: 'attachment',
      label: this.translateService.instant('general.data'),
      text: this.translateService.instant('general.show_data'),
      onclick: () =>
        this.urlListDialogService.showUrlListDialog({
          title: this.translateService.instant('general.data'),
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
      label: this.translateService.instant('general.conditions'),
      text: 'Show Conditions For Use', // TODO
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
      label: this.translateService.instant('general.files'),
      text: this.translateService.instant('general.show_files'),
      onclick: () =>
        this.urlListDialogService.showUrlListDialog({
          title: this.translateService.instant('general.show_files'),
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
