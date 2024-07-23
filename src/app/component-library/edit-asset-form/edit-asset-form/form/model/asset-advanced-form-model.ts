import {
  FormArray,
  FormControl,
  FormGroup,
  ɵFormGroupValue,
} from '@angular/forms';
import {TransportModeSelectItem} from '../../../transport-mode-select/transport-mode-select-item';
import {TemporalCoverageFormModel} from './temporal-coverage-form-model';

/**
 * Form Model for Edit Asset Form > Advanced
 * (MDS Properties)
 */
export interface AssetAdvancedFormModel {
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
 * Form Value for Edit Asset Form > Advanced
 */
export type AssetAdvancedFormValue = ɵFormGroupValue<AssetAdvancedFormModel>;
