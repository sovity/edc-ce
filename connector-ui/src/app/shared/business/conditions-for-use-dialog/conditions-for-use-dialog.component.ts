/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {ConditionsForUseDialogData} from './conditions-for-use-dialog.data';

@Component({
  selector: 'conditions-for-use-dialog',
  templateUrl: './conditions-for-use-dialog.component.html',
})
export class ConditionsForUseDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ConditionsForUseDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ConditionsForUseDialogData,
  ) {}
}
