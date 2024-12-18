import {FormControl, ɵFormGroupValue} from '@angular/forms';

/**
 * Form Model for AssetEditorDialog > Advanced > Temporal Coverage
 */
export interface TemporalCoverageFormModel {
  from: FormControl<Date | null>;
  toInclusive: FormControl<Date | null>;
}

/**
 * Form Value for AssetEditorDialog > Advanced > Temporal Coverage
 */
export type TemporalCoverageFormValue =
  ɵFormGroupValue<TemporalCoverageFormModel>;
