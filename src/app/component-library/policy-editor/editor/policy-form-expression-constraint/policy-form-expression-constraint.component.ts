import {Component, HostBinding, Input} from '@angular/core';
import {PolicyVerbConfig} from '../../model/policy-verbs';
import {ExpressionFormHandler} from '../expression-form-handler';
import {ExpressionFormValue} from '../expression-form-value';
import {TreeNode} from '../tree';

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
