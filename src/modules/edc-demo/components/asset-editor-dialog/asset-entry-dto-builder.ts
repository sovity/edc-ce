import {Inject, Injectable} from '@angular/core';
import {AssetDto, AssetEntryDto, CONNECTOR_ORIGINATOR, DataAddressDto} from "../../../edc-dmgmt-client";
import {AssetEditorDialogFormValue} from "./asset-editor-dialog-form-model";
import {isFeatureSetActive} from "../../pipes/is-active-feature-set.pipe";


@Injectable()
export class AssetEntryDtoBuilder {
  constructor(@Inject(CONNECTOR_ORIGINATOR) private connectorOriginator: string) {
  }

  /**
   * Build {@link AssetEntryDto} from {@link AssetEditorDialogFormValue}
   *
   * @param formValue form value
   * @return asset create dto
   */
  buildAssetEntryDto(formValue: AssetEditorDialogFormValue): AssetEntryDto {
    const {metadata, advanced, datasource} = formValue;
    let properties: AssetDto["properties"] = {
      "asset:prop:id": this.trimSafe(metadata?.id),
      "asset:prop:name": this.trimSafe(metadata?.name),
      "asset:prop:version": this.trimSafe(metadata?.version),
      "asset:prop:originator": this.trimSafe(this.connectorOriginator),
      "asset:prop:contenttype": this.trimSafe(metadata?.contenttype),
      "asset:prop:description": this.trimSafe(metadata?.description),
      'asset:prop:language': metadata?.language?.id ?? '',
      'asset:prop:paymentModality': metadata?.paymentModality?.id ?? '',

      "asset:prop:publisher": this.trimSafe(datasource?.publisher),
      "asset:prop:standardLicense": this.trimSafe(datasource?.standardLicense),
      "asset:prop:endpointDocumentation": this.trimSafe(datasource?.endpointDocumentation),
    }

    if (isFeatureSetActive('mds')) {
      properties = {
        ...properties,
        'asset:prop:dataCategory': this.trimSafe(advanced?.dataCategory),
        'asset:prop:dataModel': this.trimSafe(advanced?.dataModel),
        'asset:prop:geoReferenceMethod': this.trimSafe(advanced?.geoReferenceMethod),
        'asset:prop:transportMode': this.trimSafe(advanced?.transportMode),
      }
    }

    const dataAddress = this.buildDataAddressDto(datasource)
    return {asset: {properties}, dataAddress};
  }


  private buildDataAddressDto(datasource: AssetEditorDialogFormValue['datasource']): DataAddressDto {
    switch (datasource?.dataAddressType) {
      case 'Json':
        return JSON.parse(this.trimSafe(datasource.dataDestination))
      case 'Rest-Api':
        return {
          properties: {
            "type": "HttpData",
            "baseUrl": this.trimSafe(datasource.baseUrl)
          }
        }
      default:
        throw new Error(`Invalid data address type: ${datasource?.dataAddressType}`)
    }
  }

  /**
   * Trims string, defaults to empty string if null.
   * @param s string or null or undefined
   */
  private trimSafe(s?: string | null): string {
    return (s ?? '').trim();
  }
}
