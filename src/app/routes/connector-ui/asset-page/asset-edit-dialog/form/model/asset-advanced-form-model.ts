import {
  FormArray,
  FormControl,
  FormGroup,
  ɵFormGroupValue,
} from '@angular/forms';
import {DataCategorySelectItem} from '../../../data-category-select/data-category-select-item';
import {DataSubcategorySelectItem} from '../../../data-subcategory-select/data-subcategory-select-item';
import {TransportModeSelectItem} from '../../../transport-mode-select/transport-mode-select-item';
import {TemporalCoverageFormModel} from './temporal-coverage-form-model';

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
  sovereignLegalName: FormControl<string>;
  geoLocation: FormControl<string>;
  nutsLocations: FormArray<FormControl<string>>;
  dataSampleUrls: FormArray<FormControl<string>>;
  referenceFileUrls: FormArray<FormControl<string>>;
  referenceFilesDescription: FormControl<string>;
  conditionsForUse: FormControl<string>;
  dataUpdateFrequency: FormControl<string>;
  temporalCoverage: FormGroup<TemporalCoverageFormModel>;
}

/**
 * Form Value for AssetEditorDialog > Advanced
 */
export type AssetAdvancedFormValue = ɵFormGroupValue<AssetAdvancedFormModel>;
