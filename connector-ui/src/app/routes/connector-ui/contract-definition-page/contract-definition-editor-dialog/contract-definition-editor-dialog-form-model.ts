/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {FormControl, ɵFormGroupValue} from '@angular/forms';
import {PolicyDefinitionDto} from '@sovity.de/edc-client';
import {UiAssetMapped} from '../../../../core/services/models/ui-asset-mapped';

/**
 * Form Value Type
 */
export type ContractDefinitionEditorDialogFormValue =
  ɵFormGroupValue<ContractDefinitionEditorDialogFormModel>;

/**
 * Form Group Template Type
 */
export interface ContractDefinitionEditorDialogFormModel {
  id: FormControl<string>;
  accessPolicy: FormControl<PolicyDefinitionDto | null>;
  contractPolicy: FormControl<PolicyDefinitionDto | null>;
  assets: FormControl<UiAssetMapped[]>;
}
