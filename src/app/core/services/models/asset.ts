import {UiAsset} from '@sovity.de/edc-client';
import {DataCategorySelectItem} from '../../../routes/connector-ui/asset-page/data-category-select/data-category-select-item';
import {DataSubcategorySelectItem} from '../../../routes/connector-ui/asset-page/data-subcategory-select/data-subcategory-select-item';
import {LanguageSelectItem} from '../../../routes/connector-ui/asset-page/language-select/language-select-item';
import {TransportModeSelectItem} from '../../../routes/connector-ui/asset-page/transport-mode-select/transport-mode-select-item';

/**
 * Asset (UI Dto / Type Safe)
 *
 * Also includes full items / labels for fixed vocabulary values, e.g. language
 */
export type Asset = Omit<
  UiAsset,
  | 'language'
  | 'dataCategory'
  | 'dataSubcategory'
  | 'transportMode'
  | 'additionalProperties'
  | 'additionalJsonProperties'
  | 'privateProperties'
  | 'privateJsonProperties'
> & {
  connectorEndpoint: string;

  language: LanguageSelectItem | null;

  // MDS Specific
  dataCategory: DataCategorySelectItem | null;
  dataSubcategory: DataSubcategorySelectItem | null;
  transportMode: TransportModeSelectItem | null;

  // Unhandled Additional Properties
  additionalProperties: AdditionalAssetProperty[];
};

export interface AdditionalAssetProperty {
  key: string;
  value: string;
}
