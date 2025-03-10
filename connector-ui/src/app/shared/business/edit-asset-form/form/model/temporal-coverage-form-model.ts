/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
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
