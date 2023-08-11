import {Injectable} from '@angular/core';
import {AssetCreateRequest} from '@sovity.de/edc-client/dist/generated/models/AssetCreateRequest';
import {
  AssetEditorDialogFormValue
} from '../../routes/connector-ui/asset-page/asset-create-dialog/asset-editor-dialog-form-model';
import {AssetPropertyMapper} from './asset-property-mapper';
import {DataAddressMapper} from './data-address-mapper';


@Injectable()
export class AssetEntryBuilder {
  constructor(
    private assetPropertyMapper: AssetPropertyMapper,
    private dataAddressMapper: DataAddressMapper,
  ) {
  }

  /**
   * Build {@link AssetCreateRequest} from {@link AssetEditorDialogFormValue}
   *
   * @param formValue form value
   * @return asset create dto
   */
  buildAssetCreateRequest(formValue: AssetEditorDialogFormValue): AssetCreateRequest {
    let properties = this.assetPropertyMapper.buildProperties(formValue);
    const dataAddressProperties = this.dataAddressMapper.buildDataAddressProperties(
      formValue.datasource,
    );
    return {properties, dataAddressProperties, privateProperties: {}};
  }
}
