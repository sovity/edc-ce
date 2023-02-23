import {DataCategorySelectItem} from '../components/data-category-select/data-category-select-item';
import {DataSubcategorySelectItem} from '../components/data-subcategory-select/data-subcategory-select-item';
import {LanguageSelectItem} from '../components/language-select/language-select-item';
import {TransportModeSelectItem} from '../components/transport-mode-select/transport-mode-select-item';

/**
 * Asset (UI Dto / Type Safe)
 *
 * Also includes full items / labels for fixed vocabulary values, e.g. language
 */
export interface Asset {
  id: string;
  name: string | null;
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
}

export function dummyAsset(id: string): Asset {
  return {
    id,
    name: id,
    version: null,
    contentType: null,
    originator: null,
    originatorOrganization: null,
    keywords: [],

    description: null,
    language: null,
    publisher: null,
    standardLicense: null,
    endpointDocumentation: null,

    // MDS Specific
    dataCategory: null,
    dataSubcategory: null,
    dataModel: null,
    geoReferenceMethod: null,
    transportMode: null,
  };
}
