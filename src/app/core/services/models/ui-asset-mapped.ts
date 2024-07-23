import {UiAsset} from '@sovity.de/edc-client';
import {DataCategorySelectItem} from 'src/app/component-library/edit-asset-form/data-category-select/data-category-select-item';
import {DataSubcategorySelectItem} from 'src/app/component-library/edit-asset-form/data-subcategory-select/data-subcategory-select-item';
import {LanguageSelectItem} from 'src/app/component-library/edit-asset-form/language-select/language-select-item';
import {TransportModeSelectItem} from 'src/app/component-library/edit-asset-form/transport-mode-select/transport-mode-select-item';

/**
 * UiAsset with replaced fixed vocabulary items.
 *
 * This exists, because certain metadata has labels which are added in the UI, e.g. language.
 */
export type UiAssetMapped = Omit<
  UiAsset,
  'language' | 'dataCategory' | 'dataSubcategory' | 'transportMode'
> & {
  connectorEndpoint: string;

  language: LanguageSelectItem | null;

  // MDS Specific
  dataCategory: DataCategorySelectItem | null;
  dataSubcategory: DataSubcategorySelectItem | null;
  transportMode: TransportModeSelectItem | null;

  // Unhandled Additional Properties
  customJsonProperties: AdditionalAssetProperty[];
  customJsonLdProperties: AdditionalAssetProperty[];
  privateCustomJsonProperties: AdditionalAssetProperty[];
  privateCustomJsonLdProperties: AdditionalAssetProperty[];
};

export interface AdditionalAssetProperty {
  key: string;
  value: string;
}
