import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {BehaviorSubject} from 'rxjs';
import {first, map, switchMap} from 'rxjs/operators';
import {
  PolicyDefinition,
  PolicyService,
} from '../../../../core/services/api/legacy-managent-api-client';
import {Fetched} from '../../../../core/services/models/fetched';
import {NewPolicyDialogResult} from '../new-policy-dialog/new-policy-dialog-result';
import {NewPolicyDialogComponent} from '../new-policy-dialog/new-policy-dialog.component';
import {PolicyCard} from '../policy-cards/policy-card';
import {PolicyCardBuilder} from '../policy-cards/policy-card-builder';

export interface PolicyList {
  policyCards: PolicyCard[];
  numTotalPolicies: number;
}

@Component({
  selector: 'policy-definition-page',
  templateUrl: './policy-definition-page.component.html',
  styleUrls: ['./policy-definition-page.component.scss'],
})
export class PolicyDefinitionPageComponent implements OnInit {
  policyList: Fetched<PolicyList> = Fetched.empty();
  searchText: string = '';
  deleteBusy = false;
  private fetch$ = new BehaviorSubject(null);

  constructor(
    private policyService: PolicyService,
    private policyCardBuilder: PolicyCardBuilder,
    private readonly dialog: MatDialog,
  ) {}

  ngOnInit(): void {
    this.fetch$
      .pipe(
        switchMap(() => {
          return this.policyService.getAllPolicies(0, 10_000_000).pipe(
            map(
              (policyDefinitions): PolicyList => ({
                policyCards: this.policyCardBuilder.buildPolicyCards(
                  this.filterPolicies(policyDefinitions),
                ),
                numTotalPolicies: policyDefinitions.length,
              }),
            ),
            Fetched.wrap({
              failureMessage: 'Failed fetching policies.',
            }),
          );
        }),
      )
      .subscribe((policyList) => (this.policyList = policyList));
  }

  onSearch() {
    this.refresh();
  }

  onCreate() {
    const dialogRef = this.dialog.open(NewPolicyDialogComponent);
    dialogRef
      .afterClosed()
      .pipe(first())
      .subscribe((result: NewPolicyDialogResult) => {
        if (result.refreshList) {
          this.refresh();
        }
      });
  }

  refresh() {
    this.fetch$.next(null);
  }

  private filterPolicies(
    policyDefinitions: PolicyDefinition[],
  ): PolicyDefinition[] {
    return policyDefinitions.filter((policy) =>
      this.isFiltered(policy, this.searchText),
    );
  }

  /**
   * simple full-text search - serialize to JSON and see if "searchText"
   * is contained
   */
  private isFiltered(policy: PolicyDefinition, searchText: string) {
    return JSON.stringify(policy).includes(searchText);
  }
}
