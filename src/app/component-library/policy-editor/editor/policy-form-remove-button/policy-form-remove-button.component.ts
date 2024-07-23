import {Component, Input} from '@angular/core';
import {ExpressionFormHandler} from '../expression-form-handler';
import {ExpressionFormValue} from '../expression-form-value';
import {TreeNode} from '../tree';

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
