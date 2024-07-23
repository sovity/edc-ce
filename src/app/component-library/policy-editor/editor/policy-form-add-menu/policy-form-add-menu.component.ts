import {Component, Input, OnDestroy} from '@angular/core';
import {Subject} from 'rxjs';
import {
  PolicyMultiExpressionConfig,
  SUPPORTED_MULTI_EXPRESSIONS,
} from '../../model/policy-multi-expressions';
import {
  PolicyVerbConfig,
  SUPPORTED_POLICY_VERBS,
} from '../../model/policy-verbs';
import {ExpressionFormHandler} from '../expression-form-handler';
import {ExpressionFormValue} from '../expression-form-value';
import {
  PolicyExpressionRecipe,
  PolicyExpressionRecipeService,
} from '../recipes/policy-expression-recipe.service';
import {TreeNode} from '../tree';

@Component({
  selector: 'policy-form-add-menu',
  templateUrl: './policy-form-add-menu.component.html',
})
export class PolicyFormAddMenuComponent implements OnDestroy {
  multiExpressions = SUPPORTED_MULTI_EXPRESSIONS;
  constraints = SUPPORTED_POLICY_VERBS;

  @Input()
  treeNode!: TreeNode<ExpressionFormValue>;

  constructor(
    public expressionFormHandler: ExpressionFormHandler,
    public policyExpressionRecipeService: PolicyExpressionRecipeService,
  ) {}

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
