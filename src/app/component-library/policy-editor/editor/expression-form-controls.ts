import {Injectable} from '@angular/core';
import {
  FormControl,
  UntypedFormControl,
  UntypedFormGroup,
} from '@angular/forms';
import {OperatorDto, UiPolicyLiteral} from '@sovity.de/edc-client';
import {PolicyOperatorConfig} from '../model/policy-operators';
import {ExpressionFormValue} from './expression-form-value';
import {TreeNode} from './tree';

/**
 * Manages the FormGroup across the expression tree
 *
 * Controls are needed for both constraint operators and values
 *
 * Must be provided at the component level as viewProvider.
 */
@Injectable()
export class ExpressionFormControls {
  formGroup = new UntypedFormGroup({});

  getValue(node: TreeNode<ExpressionFormValue>): UiPolicyLiteral {
    const formValue = this.getValueFormControl(node).value;
    return node.value.verb!.adapter.buildValueFn(
      formValue,
      this.getOperator(node),
    );
  }

  getOperator(node: TreeNode<ExpressionFormValue>): PolicyOperatorConfig {
    return this.getOperatorFormControl(node).value;
  }

  registerControls(
    nodeId: string,
    expr: ExpressionFormValue,
    operator: OperatorDto,
    value: UiPolicyLiteral,
  ) {
    if (expr.type !== 'CONSTRAINT') {
      return;
    }

    const supportedOperators = expr.supportedOperators ?? [];
    const operatorConfig =
      supportedOperators.find((it) => it.id === operator) ??
      supportedOperators[0];

    const operatorControl = new UntypedFormControl(operatorConfig);

    const valueControl = expr.verb!.adapter.fromControlFactory();
    valueControl.reset(expr.verb!.adapter.buildFormValueFn(value));

    this.formGroup.addControl(`${nodeId}-value`, valueControl);
    this.formGroup.addControl(`${nodeId}-op`, operatorControl);
  }

  unregisterControls(node: TreeNode<ExpressionFormValue>) {
    this.dfs(node, (node) => this.unregisterNodeControls(node));
  }

  getValueFormControl(treeNode: TreeNode<ExpressionFormValue>): FormControl {
    return this.formGroup.get(`${treeNode.id}-value`) as FormControl;
  }

  getOperatorFormControl(
    treeNode: TreeNode<ExpressionFormValue>,
  ): UntypedFormControl {
    return this.formGroup.get(`${treeNode.id}-op`) as FormControl;
  }

  private unregisterNodeControls(node: TreeNode<ExpressionFormValue>) {
    if (node.value.type !== 'CONSTRAINT') {
      return;
    }

    [`${node.id}-value`, `${node.id}-op`].forEach((it) =>
      this.formGroup.removeControl(it),
    );
  }

  private dfs(
    treeNode: TreeNode<ExpressionFormValue>,
    callback: (node: TreeNode<ExpressionFormValue>) => void,
  ) {
    callback(treeNode);
    treeNode.children.forEach((child) => this.dfs(child, callback));
  }
}
