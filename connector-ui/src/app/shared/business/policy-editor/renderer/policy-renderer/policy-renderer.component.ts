/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Component, Input} from '@angular/core';
import {PolicyExpressionMapped} from '../../model/policy-expression-mapped';

@Component({
  selector: 'app-policy-renderer',
  templateUrl: './policy-renderer.component.html',
})
export class PolicyRendererComponent {
  @Input()
  expression!: PolicyExpressionMapped;

  @Input()
  errors: string[] = [];
}
