import {ComponentType} from '@angular/cdk/portal';
import {Injectable} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {Observable} from 'rxjs';
import {UiPolicyExpression} from '@sovity.de/edc-client';
import {showDialogUntil} from '../../../../core/utils/mat-dialog-utils';
import {filterNotNull} from '../../../../core/utils/rxjs-utils';
import {TimespanRestrictionDialogComponent} from './timespan-restriction-dialog/timespan-restriction-dialog.component';

export interface PolicyExpressionRecipe {
  title: string;
  tooltip: string;
  onclick: (until$: Observable<any>) => Observable<UiPolicyExpression>;
}

@Injectable()
export class PolicyExpressionRecipeService {
  recipes: PolicyExpressionRecipe[] = [
    {
      title: 'Timespan Restriction',
      tooltip:
        'Timespan at which the policy is evaluated. This can be used to restrict the data offer to certain time periods',
      onclick: (until$: Observable<any>) =>
        this.showRecipeDialog(TimespanRestrictionDialogComponent, until$),
    },
  ];

  constructor(private dialog: MatDialog) {}

  private showRecipeDialog(
    cmp: ComponentType<any>,
    until$: Observable<any>,
  ): Observable<UiPolicyExpression> {
    return showDialogUntil<unknown, UiPolicyExpression>(
      this.dialog,
      cmp,
      {},
      until$,
    ).pipe(filterNotNull());
  }
}
