/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Component, Input} from '@angular/core';
import {FormControl} from '@angular/forms';
import {TRANSPORT_MODE_SELECT_DATA} from './transport-mode-select-data';
import {TransportModeSelectItem} from './transport-mode-select-item';

@Component({
  selector: 'transport-mode-select',
  templateUrl: 'transport-mode-select.component.html',
})
export class TransportModeSelectComponent {
  @Input()
  label!: string;

  @Input()
  control!: FormControl<TransportModeSelectItem | null>;

  items = TRANSPORT_MODE_SELECT_DATA;
}
