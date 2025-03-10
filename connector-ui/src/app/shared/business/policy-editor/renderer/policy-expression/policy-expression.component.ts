/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Component, HostBinding, Input} from '@angular/core';
import {PolicyExpressionMapped} from '../../model/policy-expression-mapped';

@Component({
  selector: 'policy-expression',
  templateUrl: './policy-expression.component.html',
})
export class PolicyExpressionComponent {
  @HostBinding('class.flex')
  @HostBinding('class.flex-col')
  @HostBinding('class.justify-stretch')
  cls = true;

  @Input()
  expression!: PolicyExpressionMapped;
}
