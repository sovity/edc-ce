import {DataCategorySelectItem} from '../../../routes/connector-ui/asset-page/data-category-select/data-category-select-item';
import {DataSubcategorySelectItem} from '../../../routes/connector-ui/asset-page/data-subcategory-select/data-subcategory-select-item';
import {LanguageSelectItem} from '../../../routes/connector-ui/asset-page/language-select/language-select-item';
import {TransportModeSelectItem} from '../../../routes/connector-ui/asset-page/transport-mode-select/transport-mode-select-item';

/**
 * Asset (UI Dto / Type Safe)
 *
 * Also includes full items / labels for fixed vocabulary values, e.g. language
 */
export interface Asset {
  id: string;
  name: string;
  version: string | null;
  contentType: string | null;
  originator: string | null;
  originatorOrganization: string | null;
  keywords: string[];

  description: string | null;
  language: LanguageSelectItem | null;
  publisher: string | null;
  standardLicense: string | null;
  endpointDocumentation: string | null;

  // MDS Specific
  dataCategory: DataCategorySelectItem | null;
  dataSubcategory: DataSubcategorySelectItem | null;
  dataModel: string | null;
  geoReferenceMethod: string | null;
  transportMode: TransportModeSelectItem | null;

  // HTTP Parameterization Metadata
  httpProxyMethod: boolean | null;
  httpProxyPath: boolean | null;
  httpProxyQueryParams: boolean | null;
  httpProxyBody: boolean | null;
  httpDefaultPath: string | null;
  httpDefaultMethod: string | null;

  // Unhandled Additional Properties
  additionalProperties: AdditionalAssetProperty[];
}

export interface AdditionalAssetProperty {
  key: string;
  value: string;
}
