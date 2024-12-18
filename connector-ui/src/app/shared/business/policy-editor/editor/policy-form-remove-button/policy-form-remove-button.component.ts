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
