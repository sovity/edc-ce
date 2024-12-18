import {Injectable} from '@angular/core';
import {LanguageSelectItemService} from '../../../../../shared/form-elements/language-select/language-select-item.service';
import {AssetCreateDialogFormValue} from './model/asset-create-dialog-form-model';
import {AssetDatasourceFormValue} from './model/asset-datasource-form-model';

/**
 * Handles AngularForms for AssetCreateDialog
 */
@Injectable()
export class AssetCreateDialogFormMapper {
  constructor(private languageSelectItemService: LanguageSelectItemService) {}

  forCreate(): AssetCreateDialogFormValue {
    return {
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
      datasource: this.emptyHttpDatasource(),
    };
  }

  private emptyHttpDatasource(): AssetDatasourceFormValue {
    return {
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
}
