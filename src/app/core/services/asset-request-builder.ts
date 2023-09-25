import {Inject, Injectable} from '@angular/core';
import {UiAssetCreateRequest} from '@sovity.de/edc-client';
import {AssetEditorDialogFormValue} from '../../routes/connector-ui/asset-page/asset-create-dialog/asset-editor-dialog-form-model';
import {APP_CONFIG, AppConfig} from '../config/app-config';
import {DataAddressMapper} from './data-address-mapper';

@Injectable()
export class AssetRequestBuilder {
  constructor(
    @Inject(APP_CONFIG) private config: AppConfig,
    private dataAddressMapper: DataAddressMapper,
  ) {}

  /**
   * Build {@link UiAssetCreateRequest} from {@link AssetEditorDialogFormValue}
   *
   * @param formValue form value
   * @return asset create dto
   */
  buildAssetCreateRequest(
    formValue: AssetEditorDialogFormValue,
  ): UiAssetCreateRequest {
    let id = formValue.metadata?.id!;
    let name = formValue.metadata?.name!;
    let version = formValue.metadata?.version;
    let description = formValue.metadata?.description;
    let language = formValue.metadata?.language?.id;
    let keywords = formValue.metadata?.keywords;
    let licenseUrl = formValue.metadata?.standardLicense;
    let creatorOrganizationName = this.config.curatorOrganizationName;
    let publisherHomepage = formValue.metadata?.publisher;
    let mediaType = formValue.metadata?.contentType;

    let dataCategory = formValue.advanced?.dataModel;
    let dataSubcategory = formValue.advanced?.dataSubcategory?.id;
    let transportMode = formValue.advanced?.transportMode?.id;
    let geoReferenceMethod = formValue.advanced?.geoReferenceMethod;
    let dataModel = formValue.advanced?.dataModel;

    let landingPageUrl = formValue.datasource?.httpUrl;

    const dataAddressProperties =
      this.dataAddressMapper.buildDataAddressProperties(formValue.datasource);
    return {
      id,
      name,
      language,
      description,
      creatorOrganizationName,
      publisherHomepage,
      licenseUrl,
      version,
      keywords,
      mediaType,
      landingPageUrl,
      dataCategory,
      dataSubcategory,
      dataModel,
      geoReferenceMethod,
      transportMode,
      dataAddressProperties,
      additionalProperties: {},
      privateProperties: {},
      additionalJsonProperties: {},
    };
  }
}
