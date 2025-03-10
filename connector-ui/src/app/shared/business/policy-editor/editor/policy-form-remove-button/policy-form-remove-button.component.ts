/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Component, Input} from '@angular/core';
import {TreeNode} from '../../model/tree';
import {ExpressionFormHandler} from '../expression-form-handler';
import {ExpressionFormValue} from '../expression-form-value';

@Component({
  selector: 'policy-form-remove-button',
  templateUrl: './policy-form-remove-button.component.html',
})
export class PolicyFormRemoveButton {
  @Input()
  treeNode!: TreeNode<ExpressionFormValue>;

  constructor(public expressionFormHandler: ExpressionFormHandler) {}

  onRemoveClick() {
    this.expressionFormHandler.removeNode(this.treeNode);
  }
}
