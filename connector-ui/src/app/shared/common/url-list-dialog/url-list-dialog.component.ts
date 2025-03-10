/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {UrlListDialogData} from './url-list-dialog.data';

@Component({
  selector: 'app-json-dialog',
  templateUrl: './url-list-dialog.component.html',
})
export class UrlListDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<UrlListDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UrlListDialogData,
  ) {}
}
