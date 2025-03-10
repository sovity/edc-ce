/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Component, Input} from '@angular/core';

@Component({
  selector: 'date',
  template: `<span [title]="date | date : 'EEEE yyyy-MM-dd hh:mm'">{{
    date | date : 'yyyy-MM-dd'
  }}</span>`,
})
export class DateComponent {
  @Input()
  date?: Date | null;
}
