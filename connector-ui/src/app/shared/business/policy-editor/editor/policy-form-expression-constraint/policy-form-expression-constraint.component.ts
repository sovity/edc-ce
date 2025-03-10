/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Component, HostBinding, Input} from '@angular/core';
import {PolicyVerbConfig} from '../../model/policy-verbs';
import {TreeNode} from '../../model/tree';
import {ExpressionFormHandler} from '../expression-form-handler';
import {ExpressionFormValue} from '../expression-form-value';

@Component({
  selector: 'policy-form-expression-constraint',
  templateUrl: './policy-form-expression-constraint.component.html',
})
export class PolicyFormExpressionConstraintComponent {
  @HostBinding('class.flex')
  @HostBinding('class.gap-4')
  cls = true;

  @Input()
  treeNode!: TreeNode<ExpressionFormValue>;

  get expr(): ExpressionFormValue {
    return this.treeNode.value;
  }

  get verb(): PolicyVerbConfig {
    return this.expr.verb!;
  }

  constructor(public expressionFormHandler: ExpressionFormHandler) {}
}
