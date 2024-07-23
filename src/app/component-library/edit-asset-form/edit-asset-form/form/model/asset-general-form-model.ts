import {FormControl, ɵFormGroupValue} from '@angular/forms';
import {DataCategorySelectItem} from '../../../data-category-select/data-category-select-item';
import {DataSubcategorySelectItem} from '../../../data-subcategory-select/data-subcategory-select-item';
import {LanguageSelectItem} from '../../../language-select/language-select-item';

/**
 * Form Model for Edit Asset Form > General
 */
export interface AssetGeneralFormModel {
  id: FormControl<string>;
  name: FormControl<string>;
  description: FormControl<string>;
  keywords: FormControl<string[]>;
  dataCategory?: FormControl<DataCategorySelectItem | null>;
  dataSubcategory?: FormControl<DataSubcategorySelectItem | null>;
  showAdvancedFields: FormControl<boolean>;
  version: FormControl<string>;
  contentType: FormControl<string>;
  language: FormControl<LanguageSelectItem | null>;
  publisher: FormControl<string>;
  standardLicense: FormControl<string>;
  endpointDocumentation: FormControl<string>;
}

/**
 * Form Value for Edit Asset Form > General
 */
export type AssetGeneralFormValue = ɵFormGroupValue<AssetGeneralFormModel>;
