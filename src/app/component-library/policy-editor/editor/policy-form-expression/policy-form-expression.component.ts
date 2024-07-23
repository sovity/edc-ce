import {Component, Input, TrackByFunction} from '@angular/core';
import {ExpressionFormHandler} from '../expression-form-handler';
import {ExpressionFormValue} from '../expression-form-value';
import {TreeNode} from '../tree';

@Component({
  selector: 'policy-form-expression',
  templateUrl: './policy-form-expression.component.html',
})
export class PolicyFormExpressionComponent {
  @Input()
  treeNode!: TreeNode<ExpressionFormValue>;

  trackByFn: TrackByFunction<TreeNode<ExpressionFormValue>> = (_, it) => it.id;

  get expr(): ExpressionFormValue {
    return this.treeNode.value;
  }

  constructor(public expressionFormHandler: ExpressionFormHandler) {}
}
