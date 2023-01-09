import {DataCategorySelectItem} from "../components/data-category-select/data-category-select-item";
import {DataSubcategorySelectItem} from "../components/data-subcategory-select/data-subcategory-select-item";
import {TransportModeSelectItem} from "../components/transport-mode-select/transport-mode-select-item";
import {LanguageSelectItem} from "../components/language-select/language-select-item";

/**
 * Type-Safe representation of Asset Properties.
 *
 * Also includes full items / labels for fixed vocabulary values, e.g. language
 */
export interface AssetDto {
  id: string,
  idsId: string,
  name: string,
  version: string,
  contentType: string,
  originator: string,

  description: string,
  language: LanguageSelectItem | null,
  publisher: string,
  standardLicense: string,
  endpointDocumentation: string,

  // MDS Specific
  dataCategory: DataCategorySelectItem | null,
  dataSubcategory: DataSubcategorySelectItem | null,
  dataModel: string,
  geoReferenceMethod: string,
  transportMode: TransportModeSelectItem | null,
}
