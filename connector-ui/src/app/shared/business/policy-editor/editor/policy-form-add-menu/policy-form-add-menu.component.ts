import {Component, Input, OnDestroy} from '@angular/core';
import {Subject} from 'rxjs';
import {
  PolicyMultiExpressionConfig,
  PolicyMultiExpressionService,
} from '../../model/policy-multi-expressions';
import {PolicyVerbConfig, PolicyVerbService} from '../../model/policy-verbs';
import {TreeNode} from '../../model/tree';
import {ExpressionFormHandler} from '../expression-form-handler';
import {ExpressionFormValue} from '../expression-form-value';
import {
  PolicyExpressionRecipe,
  PolicyExpressionRecipeService,
} from '../recipes/policy-expression-recipe.service';

@Component({
  selector: 'policy-form-add-menu',
  templateUrl: './policy-form-add-menu.component.html',
})
export class PolicyFormAddMenuComponent implements OnDestroy {
  multiExpressions: PolicyMultiExpressionConfig[] = [];
  verbs: PolicyVerbConfig[] = [];

  @Input()
  treeNode!: TreeNode<ExpressionFormValue>;

  constructor(
    public expressionFormHandler: ExpressionFormHandler,
    public policyExpressionRecipeService: PolicyExpressionRecipeService,
    private policyVerbService: PolicyVerbService,
    private policyMultiExpressionService: PolicyMultiExpressionService,
  ) {}

  onMenuOpened() {
    this.verbs = this.policyVerbService.getSupportedPolicyVerbs();
    this.multiExpressions =
      this.policyMultiExpressionService.getSupportedMultiExpressions();
  }

  onAddConstraint(constraint: PolicyVerbConfig) {
    const path = this.treeNode.path;
    this.expressionFormHandler.addConstraint(path, constraint);
  }

  onAddMultiExpression(multi: PolicyMultiExpressionConfig) {
    const path = this.treeNode.path;
    this.expressionFormHandler.addMultiExpression(path, multi);
  }

  onAddRecipe(recipe: PolicyExpressionRecipe) {
    const path = this.treeNode.path;
    recipe
      .onclick(this.ngOnDestroy$)
      .subscribe((expression) =>
        this.expressionFormHandler.addExpression(path, expression),
      );
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
