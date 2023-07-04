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

  buildAssetFromProperties(
    props: Record<string, string | null>,
    opts?: {connectorEndpoint?: string},
  ): Asset {
    const language = props[AssetProperties.language]
      ? this.languageSelectItemService.findById(
          props[AssetProperties.language]!,
        )
      : null;
    const dataCategory = props[AssetProperties.dataCategory]
      ? this.dataCategorySelectItemService.findById(
          props[AssetProperties.dataCategory]!,
        )
      : null;
    const dataSubcategory = props[AssetProperties.dataSubcategory]
      ? this.dataSubcategorySelectItemService.findById(
          props[AssetProperties.dataSubcategory]!,
        )
      : null;
    const transportMode = props[AssetProperties.transportMode]
      ? this.transportModeSelectItemService.findById(
          props[AssetProperties.transportMode]!,
        )
      : null;
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
      originator: opts?.connectorEndpoint ?? props[AssetProperties.originator],
      originatorOrganization:
        props[AssetProperties.curatorOrganizationName] ??
        props[AssetProperties._legacyCuratorOrganizationName] ??
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
      httpDefaultPath: props[AssetProperties.httpDefaultPath],
      httpDefaultMethod: props[AssetProperties.httpDefaultMethod],
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
    props[AssetProperties.originator] = trimmedOrNull(
      this.config.connectorEndpoint,
    );
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
      props[AssetProperties.httpDefaultMethod] = datasource?.httpProxyMethod
        ? datasource?.httpMethod ?? null
        : null;
      props[AssetProperties.httpDefaultPath] = datasource?.httpProxyPath
        ? datasource?.httpDefaultPath ?? null
        : null;
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
