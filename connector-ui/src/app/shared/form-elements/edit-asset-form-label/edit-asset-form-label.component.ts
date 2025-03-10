/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Component, Input} from '@angular/core';
import {AbstractControl, Validators} from '@angular/forms';

@Component({
  selector: 'edit-asset-form-label',
  templateUrl: './edit-asset-form-label.component.html',
})
export class EditAssetFormLabelComponent {
  @Input() label!: string;
  @Input() htmlFor?: string;
  @Input() ctrl?: AbstractControl<any>;

  isRequired(): boolean {
    return this.ctrl?.hasValidator(Validators.required) || false;
  }
}
