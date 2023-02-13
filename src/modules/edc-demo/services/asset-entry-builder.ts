import {Injectable} from '@angular/core';
import {AssetEntryDto, DataAddressDto} from '../../edc-dmgmt-client';
import {AssetEditorDialogFormValue} from '../components/asset-editor-dialog/asset-editor-dialog-form-model';
import {HttpDatasourceFormMapper} from '../components/asset-editor-dialog/model/http-datasource-form-mapper';
import {HttpDatasourceProperties} from '../models/http-datasource-properties';
import {removeNullValues} from '../utils/record-utils';
import {AssetPropertyMapper} from './asset-property-mapper';

@Injectable()
export class AssetEntryBuilder {
  constructor(
    private assetPropertyMapper: AssetPropertyMapper,
    private httpDatasourceFormMapper: HttpDatasourceFormMapper,
  ) {}

  /**
   * Build {@link AssetEntryDto} from {@link AssetEditorDialogFormValue}
   *
   * @param formValue form value
   * @return asset create dto
   */
  buildAssetEntry(formValue: AssetEditorDialogFormValue): AssetEntryDto {
    let properties = this.assetPropertyMapper.buildProperties(formValue);
    const dataAddress = this.buildDataAddressDto(formValue.datasource);
    return {asset: {properties}, dataAddress};
  }

  private buildDataAddressDto(
    datasource: AssetEditorDialogFormValue['datasource'],
  ): DataAddressDto {
    switch (datasource?.dataAddressType) {
      case 'Custom-Datasource-Json':
        return JSON.parse(datasource.dataDestination?.trim() ?? '');
      case 'Http':
        const httpDatasourceProperties =
          this.httpDatasourceFormMapper.buildHttpDatasourceProperties(
            datasource,
          );
        return {
          properties: {
            type: 'HttpData',
            ...this.encodeHttpDatasourceProperties(httpDatasourceProperties),
          },
        };
      default:
        throw new Error(
          `Invalid data address type: ${datasource?.dataAddressType}`,
        );
    }
  }

  private encodeHttpDatasourceProperties(
    httpDatasource: HttpDatasourceProperties,
  ): Record<string, string> {
    const props: Record<string, string | null> = {
      baseUrl: httpDatasource.url,
      method: httpDatasource.method,
      body: httpDatasource.body,
      contentType: httpDatasource.contentType,
      authKey: httpDatasource.authHeaderName,
      authCode: httpDatasource.authHeaderValue,
      secretName: httpDatasource.authHeaderSecretName,
      ...Object.fromEntries(
        Object.entries(httpDatasource.headers).map(
          ([headerName, headerValue]) => [`header:${headerName}`, headerValue],
        ),
      ),
    };
    return removeNullValues(props);
  }
}
