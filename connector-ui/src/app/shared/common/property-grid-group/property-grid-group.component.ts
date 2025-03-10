/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Component, HostBinding, Input} from '@angular/core';
import {PropertyGridGroup} from './property-grid-group';

@Component({
  selector: 'property-grid-group',
  templateUrl: './property-grid-group.component.html',
})
export class PropertyGridGroupComponent {
  @Input()
  propGroups: PropertyGridGroup[] = [];

  @Input()
  columns: number = 3;

  @HostBinding('class.flex')
  @HostBinding('class.flex-col')
  @HostBinding('class.justify-start')
  cls = true;
}
