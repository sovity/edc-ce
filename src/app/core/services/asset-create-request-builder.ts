import {Injectable} from '@angular/core';
import {UiAssetCreateRequest} from '@sovity.de/edc-client';
import {AssetEditorDialogFormValue} from '../../routes/connector-ui/asset-page/asset-create-dialog/asset-editor-dialog-form-model';
import {DataAddressMapper} from './data-address-mapper';

@Injectable()
export class AssetCreateRequestBuilder {
  constructor(private dataAddressMapper: DataAddressMapper) {}

  /**
   * Build {@link UiAssetCreateRequest} from {@link AssetEditorDialogFormValue}
   *
   * @param formValue form value
   * @return asset create dto
   */
  buildAssetCreateRequest(
    formValue: AssetEditorDialogFormValue,
  ): UiAssetCreateRequest {
    const id = formValue.metadata?.id!;
    const title = formValue.metadata?.title!;
    const version = formValue.metadata?.version;
    const description = formValue.metadata?.description;
    const language = formValue.metadata?.language?.id;
    const keywords = formValue.metadata?.keywords;
    const licenseUrl = formValue.metadata?.standardLicense;
    const publisherHomepage = formValue.metadata?.publisher;
    const mediaType = formValue.metadata?.contentType;
    const landingPageUrl = formValue.metadata?.endpointDocumentation;

    const dataCategory = formValue.advanced?.dataModel;
    const dataSubcategory = formValue.advanced?.dataSubcategory?.id;
    const transportMode = formValue.advanced?.transportMode?.id;
    const geoReferenceMethod = formValue.advanced?.geoReferenceMethod;
    const dataModel = formValue.advanced?.dataModel;

    const dataAddressProperties =
      this.dataAddressMapper.buildDataAddressProperties(formValue.datasource);
    return {
      id,
      title,
      language,
      description,
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
    };
  }
}
