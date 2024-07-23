import {Component, HostBinding, Input, TrackByFunction} from '@angular/core';
import {ExpressionFormValue} from '../expression-form-value';
import {TreeNode} from '../tree';

@Component({
  selector: 'policy-form-expression-multi',
  templateUrl: './policy-form-expression-multi.component.html',
})
export class PolicyFormExpressionMultiComponent {
  @HostBinding('class.flex')
  @HostBinding('class.flex-col')
  @HostBinding('class.justify-stretch')
  cls = true;

  @Input()
  treeNode!: TreeNode<ExpressionFormValue>;

  trackByFn: TrackByFunction<TreeNode<ExpressionFormValue>> = (_, it) => it.id;

  get expr(): ExpressionFormValue {
    return this.treeNode.value;
  }
}
