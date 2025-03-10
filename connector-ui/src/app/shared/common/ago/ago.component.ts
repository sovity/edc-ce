/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Component, Input} from '@angular/core';

/**
 * Displays a date as estimated relative time (e.g. "3 days ago").
 *
 * But also shows the full date as a tooltip.
 */
@Component({
  selector: 'ago',
  template: `<span
    [matTooltip]="(date | date : 'EEEE dd/MM/yyyy HH:mm') ?? ''"
    >{{ date | ago | async }}</span
  >`,
})
export class AgoComponent {
  @Input()
  date?: Date | null;
}
