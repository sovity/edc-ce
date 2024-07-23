import {Injectable} from '@angular/core';
import {UiAssetMapped} from 'src/app/core/services/models/ui-asset-mapped';
import {LanguageSelectItemService} from '../../language-select/language-select-item.service';
import {AssetDatasourceFormValue} from './model/asset-datasource-form-model';
import {EditAssetFormValue} from './model/edit-asset-form-model';

/**
 * Handles AngularForms for Edit Asset Form
 */
@Injectable()
export class EditAssetFormInitializer {
  constructor(private languageSelectItemService: LanguageSelectItemService) {}

  forCreate(): EditAssetFormValue {
    return {
      mode: 'CREATE',
      general: {
        id: '',
        name: '',
        description: '',
        keywords: [],
        dataCategory: null,
        dataSubcategory: null,
        version: '',
        contentType: '',
        language: this.languageSelectItemService.english(),
        publisher: '',
        standardLicense: '',
        endpointDocumentation: '',
        showAdvancedFields: false,
      },
      advanced: {
        dataModel: '',
        transportMode: null,
        geoReferenceMethod: '',
        conditionsForUse: '',
        dataUpdateFrequency: '',
        sovereignLegalName: '',
        geoLocation: '',
        nutsLocations: [],
        dataSampleUrls: [],
        referenceFileUrls: [],
        referenceFilesDescription: '',
        temporalCoverage: {from: null, toInclusive: null},
      },
      datasource: this.emptyHttpDatasource(),
    };
  }

  forEdit(asset: UiAssetMapped): EditAssetFormValue {
    return {
      mode: 'EDIT',
      general: {
        id: asset.assetId,
        name: asset.title,
        description: asset.description,
        keywords: asset.keywords,
        dataCategory: asset.dataCategory,
        dataSubcategory: asset.dataSubcategory,
        version: asset.version,
        contentType: asset.mediaType,
        language: asset.language,
        publisher: asset.publisherHomepage,
        standardLicense: asset.licenseUrl,
        endpointDocumentation: asset.landingPageUrl,
        showAdvancedFields: true,
      },
      advanced: {
        dataModel: asset.dataModel,
        transportMode: asset.transportMode,
        geoReferenceMethod: asset.geoReferenceMethod,
        sovereignLegalName: asset.sovereignLegalName,
        geoLocation: asset.geoLocation,
        nutsLocations: asset.nutsLocations,
        dataSampleUrls: asset.dataSampleUrls,
        referenceFileUrls: asset.referenceFileUrls,
        referenceFilesDescription: asset.referenceFilesDescription,
        conditionsForUse: asset.conditionsForUse,
        dataUpdateFrequency: asset.dataUpdateFrequency,
        temporalCoverage: {
          from: asset.temporalCoverageFrom,
          toInclusive: asset.temporalCoverageToInclusive,
        },
      },
      datasource: this.emptyEditDatasource(asset),
    };
  }

  private emptyHttpDatasource(): AssetDatasourceFormValue {
    return {
      dataSourceAvailability: 'On-Request',
      contactEmail: '',
      contactPreferredEmailSubject: '',

      dataAddressType: 'Http',
      dataDestination: '',

      httpUrl: '',
      httpMethod: 'GET',
      httpAuthHeaderType: 'None',
      httpAuthHeaderName: '',
      httpAuthHeaderValue: '',
      httpAuthHeaderSecretName: '',
      httpQueryParams: [],

      httpDefaultPath: '',
      httpProxyMethod: false,
      httpProxyPath: false,
      httpProxyQueryParams: false,
      httpProxyBody: false,

      httpHeaders: [],
    };
  }

  private emptyEditDatasource(asset: UiAssetMapped): AssetDatasourceFormValue {
    return {
      ...this.emptyHttpDatasource(),
      dataSourceAvailability:
        asset.dataSourceAvailability === 'LIVE' ? 'Unchanged' : 'On-Request',
      contactEmail: asset.onRequestContactEmail ?? '',
      contactPreferredEmailSubject: asset.onRequestContactEmailSubject ?? '',
    };
  }
}
