import {Injectable} from '@angular/core';
import {AssetEntryDto, DataAddressDto} from "../../edc-dmgmt-client";
import {AssetEditorDialogFormValue} from "../components/asset-editor-dialog/asset-editor-dialog-form-model";
import {AssetPropertyMapper} from "./asset-property-mapper";


@Injectable()
export class AssetEntryBuilder {
  constructor(private assetPropertyMapper: AssetPropertyMapper) {
  }

  /**
   * Build {@link AssetEntryDto} from {@link AssetEditorDialogFormValue}
   *
   * @param formValue form value
   * @return asset create dto
   */
  buildAssetEntry(formValue: AssetEditorDialogFormValue): AssetEntryDto {
    let properties = this.uncheckedCast(this.assetPropertyMapper.buildProperties(formValue))
    const dataAddress = this.buildDataAddressDto(formValue.datasource)
    return {asset: {properties}, dataAddress};
  }


  private buildDataAddressDto(datasource: AssetEditorDialogFormValue['datasource']): DataAddressDto {
    switch (datasource?.dataAddressType) {
      case 'Json':
        return JSON.parse(datasource.dataDestination?.trim() ?? '')
      case 'Rest-Api':
        return {
          properties: {
            "type": "HttpData",
            "baseUrl": datasource.baseUrl?.trim() ?? ''
          }
        }
      default:
        throw new Error(`Invalid data address type: ${datasource?.dataAddressType}`)
    }
  }

  private uncheckedCast(props: Record<string, string | null>): Record<string, string> {
    return props as Record<string, string>;
  }
}
