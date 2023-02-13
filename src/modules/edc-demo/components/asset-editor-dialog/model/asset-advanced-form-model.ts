import {FormControl} from '@angular/forms';
import {DataCategorySelectItem} from '../../data-category-select/data-category-select-item';
import {DataSubcategorySelectItem} from '../../data-subcategory-select/data-subcategory-select-item';
import {TransportModeSelectItem} from '../../transport-mode-select/transport-mode-select-item';

/**
 * Form Model for AssetEditorDialog > Advanced
 * (MDS Properties)
 */
export interface AssetAdvancedFormModel {
  dataCategory: FormControl<DataCategorySelectItem | null>;
  dataSubcategory: FormControl<DataSubcategorySelectItem | null>;
  dataModel: FormControl<string>;
  geoReferenceMethod: FormControl<string>;
  transportMode: FormControl<TransportModeSelectItem | null>;
}
