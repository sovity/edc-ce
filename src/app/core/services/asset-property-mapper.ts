import {Inject, Injectable} from '@angular/core';
import {AssetEditorDialogFormValue} from '../../routes/connector-ui/asset-page/asset-create-dialog/asset-editor-dialog-form-model';
import {DataCategorySelectItemService} from '../../routes/connector-ui/asset-page/data-category-select/data-category-select-item.service';
import {DataSubcategorySelectItemService} from '../../routes/connector-ui/asset-page/data-subcategory-select/data-subcategory-select-item.service';
import {LanguageSelectItemService} from '../../routes/connector-ui/asset-page/language-select/language-select-item.service';
import {TransportModeSelectItemService} from '../../routes/connector-ui/asset-page/transport-mode-select/transport-mode-select-item.service';
import {ActiveFeatureSet} from '../config/active-feature-set';
import {APP_CONFIG, AppConfig} from '../config/app-config';
import {removeNullValues} from '../utils/record-utils';
import {trimmedOrNull} from '../utils/string-utils';
import {AssetProperties} from './asset-properties';
import {Asset} from './models/asset';

/**
 * Maps between EDC Asset and our type safe asset
 */
@Injectable({
  providedIn: 'root',
})
export class AssetPropertyMapper {
  constructor(
    @Inject(APP_CONFIG) private config: AppConfig,
    private languageSelectItemService: LanguageSelectItemService,
    private transportModeSelectItemService: TransportModeSelectItemService,
    private dataCategorySelectItemService: DataCategorySelectItemService,
    private dataSubcategorySelectItemService: DataSubcategorySelectItemService,
    private activeFeatureSet: ActiveFeatureSet,
  ) {}

  buildAsset(opts: {
    connectorEndpoint: string;
    properties: Record<string, string | null>;
  }): Asset {
    const props = opts.properties;
    const lookup = <T>(key: string, fn: (id: string) => T) =>
      props[key] ? fn(props[key]!) : null;

    const language = lookup(AssetProperties.language, (id) =>
      this.languageSelectItemService.findById(id),
    );
    const dataCategory = lookup(AssetProperties.dataCategory, (id) =>
      this.dataCategorySelectItemService.findById(id),
    );
    const dataSubcategory = lookup(AssetProperties.dataSubcategory, (id) =>
      this.dataSubcategorySelectItemService.findById(id),
    );
    const transportMode = lookup(AssetProperties.transportMode, (id) =>
      this.transportModeSelectItemService.findById(id),
    );
    const keywords = (props[AssetProperties.keywords] ?? '')
      .split(',')
      .map((it) => it.trim())
      .filter((it) => it);

    const id = props[AssetProperties.id] ?? 'no-id-was-set';
    const additionalProperties = this.buildAdditionalProperties(props);

    return {
      id,
      name: props[AssetProperties.name] ?? id,
      version: props[AssetProperties.version],
      contentType: props[AssetProperties.contentType],
      originator: opts.connectorEndpoint,
      originatorOrganization:
        props[AssetProperties.curatorOrganizationName] ??
        'Unknown Organization',
      keywords,
      description: props[AssetProperties.description],
      language,
      publisher: props[AssetProperties.publisher],
      standardLicense: props[AssetProperties.standardLicense],
      endpointDocumentation: props[AssetProperties.endpointDocumentation],
      dataCategory,
      dataSubcategory,
      dataModel: props[AssetProperties.dataModel],
      geoReferenceMethod: props[AssetProperties.geoReferenceMethod],
      transportMode,
      httpProxyMethod: this._parseBoolean(
        props[AssetProperties.httpProxyMethod],
      ),
      httpProxyPath: this._parseBoolean(props[AssetProperties.httpProxyPath]),
      httpProxyQueryParams: this._parseBoolean(
        props[AssetProperties.httpProxyQueryParams],
      ),
      httpProxyBody: this._parseBoolean(props[AssetProperties.httpProxyBody]),
      additionalProperties,
    };
  }

  private buildAdditionalProperties(props: Record<string, string | null>) {
    const knownKeys = Object.values(AssetProperties);
    return Object.entries(props)
      .filter(([k, _]) => !knownKeys.includes(k))
      .map(([key, value]) => ({
        key,
        value: value ?? '',
      }));
  }

  buildProperties(
    formValue: AssetEditorDialogFormValue,
  ): Record<string, string> {
    const {metadata, advanced, datasource} = formValue;
    const props: Record<string, string | null> = {};
    props[AssetProperties.id] = trimmedOrNull(metadata?.id);
    props[AssetProperties.name] = trimmedOrNull(metadata?.name);
    props[AssetProperties.version] = trimmedOrNull(metadata?.version);
    props[AssetProperties.curatorOrganizationName] = trimmedOrNull(
      this.config.curatorOrganizationName,
    );
    props[AssetProperties.keywords] = trimmedOrNull(
      metadata?.keywords?.join(', '),
    );
    props[AssetProperties.contentType] = trimmedOrNull(metadata?.contentType);
    props[AssetProperties.description] = trimmedOrNull(metadata?.description);
    props[AssetProperties.language] = metadata?.language?.id ?? null;

    props[AssetProperties.publisher] = trimmedOrNull(metadata?.publisher);
    props[AssetProperties.standardLicense] = trimmedOrNull(
      metadata?.standardLicense,
    );
    props[AssetProperties.endpointDocumentation] = trimmedOrNull(
      metadata?.endpointDocumentation,
    );

    if (this.activeFeatureSet.hasMdsFields()) {
      props[AssetProperties.dataCategory] = advanced?.dataCategory?.id ?? null;
      props[AssetProperties.dataSubcategory] =
        advanced?.dataSubcategory?.id ?? null;
      props[AssetProperties.dataModel] = trimmedOrNull(advanced?.dataModel);
      props[AssetProperties.geoReferenceMethod] = trimmedOrNull(
        advanced?.geoReferenceMethod,
      );
      props[AssetProperties.transportMode] =
        advanced?.transportMode?.id ?? null;
    }

    if (datasource?.dataAddressType === 'Http') {
      props[AssetProperties.httpProxyMethod] = this._encodeBoolean(
        datasource?.httpProxyMethod,
      );
      props[AssetProperties.httpProxyPath] = this._encodeBoolean(
        datasource?.httpProxyPath,
      );
      props[AssetProperties.httpProxyQueryParams] = this._encodeBoolean(
        datasource?.httpProxyQueryParams,
      );
      props[AssetProperties.httpProxyBody] = this._encodeBoolean(
        datasource?.httpProxyBody,
      );
    }

    return removeNullValues(props);
  }

  private _parseBoolean(value: string | null): boolean | null {
    if (!value) {
      return null;
    }
    return value === 'true';
  }

  private _encodeBoolean(value?: boolean | null): string {
    return value ? 'true' : 'false';
  }
}
