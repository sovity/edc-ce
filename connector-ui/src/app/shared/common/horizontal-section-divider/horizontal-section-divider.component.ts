/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Component, HostBinding, Input} from '@angular/core';

@Component({
  selector: 'horizontal-section-divider',
  templateUrl: './horizontal-section-divider.component.html',
})
export class HorizontalSectionDividerComponent {
  @HostBinding('class.flex')
  @HostBinding('class.items-center')
  cls = true;

  @Input()
  text: string = '';
}
