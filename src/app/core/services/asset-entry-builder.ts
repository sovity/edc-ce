import {Injectable} from '@angular/core';
import {AssetEditorDialogFormValue} from '../../routes/connector-ui/asset-page/asset-create-dialog/asset-editor-dialog-form-model';
import {AssetEntryDto} from './api/legacy-managent-api-client';
import {AssetPropertyMapper} from './asset-property-mapper';
import {DataAddressMapper} from './data-address-mapper';

@Injectable()
export class AssetEntryBuilder {
  constructor(
    private assetPropertyMapper: AssetPropertyMapper,
    private dataAddressMapper: DataAddressMapper,
  ) {}

  /**
   * Build {@link AssetEntryDto} from {@link AssetEditorDialogFormValue}
   *
   * @param formValue form value
   * @return asset create dto
   */
  buildAssetEntry(formValue: AssetEditorDialogFormValue): AssetEntryDto {
    let properties = this.assetPropertyMapper.buildProperties(formValue);
    const dataAddress = this.dataAddressMapper.buildDataAddressProperties(
      formValue.datasource,
    );
    return {asset: {properties}, dataAddress};
  }
}
