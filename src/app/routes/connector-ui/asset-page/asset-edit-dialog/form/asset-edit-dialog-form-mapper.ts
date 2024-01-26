import {Injectable} from '@angular/core';
import {UiAssetMapped} from '../../../../../core/services/models/ui-asset-mapped';
import {LanguageSelectItemService} from '../../language-select/language-select-item.service';
import {AssetEditorDialogFormValue} from './model/asset-editor-dialog-form-model';

/**
 * Handles AngularForms for AssetEditorDialog
 */
@Injectable()
export class AssetEditDialogFormMapper {
  constructor(private languageSelectItemService: LanguageSelectItemService) {}

  forCreate(): AssetEditorDialogFormValue {
    return {
      mode: 'CREATE',
      metadata: {
        id: '',
        title: '',
        version: '',
        contentType: '',
        description: '',
        keywords: [],
        language: this.languageSelectItemService.english(),
        publisher: '',
        standardLicense: '',
        endpointDocumentation: '',
      },
      advanced: {
        dataModel: '',
        dataCategory: null,
        dataSubcategory: null,
        transportMode: null,
        geoReferenceMethod: '',
      },
      datasource: {
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
      },
    };
  }

  forEditMetadata(asset: UiAssetMapped): AssetEditorDialogFormValue {
    return {
      mode: 'EDIT_METADATA',
      metadata: {
        id: asset.assetId,
        title: asset.title,
        version: asset.version,
        contentType: asset.mediaType,
        description: asset.description,
        keywords: asset.keywords,
        language: asset.language,
        publisher: asset.publisherHomepage,
        standardLicense: asset.licenseUrl,
        endpointDocumentation: asset.landingPageUrl,
      },
      advanced: {
        dataModel: asset.dataModel,
        dataCategory: asset.dataCategory,
        dataSubcategory: asset.dataSubcategory,
        transportMode: asset.transportMode,
        geoReferenceMethod: asset.geoReferenceMethod,
        sovereignLegalName: asset.sovereignLegalName,
        geoLocation: asset.geoLocation,
        nutsLocations: asset.nutsLocation,
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
      datasource: this.forCreate().datasource,
    };
  }
}
