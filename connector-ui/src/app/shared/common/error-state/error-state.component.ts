/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Component, HostBinding, Input} from '@angular/core';
import {FetchError} from '../../../core/services/models/fetched';

@Component({
  selector: 'error-state',
  templateUrl: './error-state.component.html',
})
export class ErrorStateComponent {
  @HostBinding('class.flex')
  @HostBinding('class.flex-col')
  @HostBinding('class.justify-center')
  @HostBinding('class.items-center')
  cls = true;

  @Input()
  error: FetchError | undefined;
}
