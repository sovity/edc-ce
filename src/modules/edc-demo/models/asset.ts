import {DataCategorySelectItem} from "../components/data-category-select/data-category-select-item";
import {DataSubcategorySelectItem} from "../components/data-subcategory-select/data-subcategory-select-item";
import {TransportModeSelectItem} from "../components/transport-mode-select/transport-mode-select-item";
import {LanguageSelectItem} from "../components/language-select/language-select-item";

/**
 * Asset (UI Dto / Type Safe)
 *
 * Also includes full items / labels for fixed vocabulary values, e.g. language
 */
export interface Asset {
  id: string,
  idsId: string | null,
  name: string | null,
  version: string | null,
  contentType: string | null,
  originator: string | null,
  originatorOrganization: string|null,
  keywords: string[],

  description: string | null,
  language: LanguageSelectItem | null,
  publisher: string | null,
  standardLicense: string | null,
  endpointDocumentation: string | null,

  // MDS Specific
  dataCategory: DataCategorySelectItem | null,
  dataSubcategory: DataSubcategorySelectItem | null,
  dataModel: string | null,
  geoReferenceMethod: string | null,
  transportMode: TransportModeSelectItem | null,
}
