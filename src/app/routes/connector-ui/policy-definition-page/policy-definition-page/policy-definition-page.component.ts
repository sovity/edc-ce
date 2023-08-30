import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {BehaviorSubject} from 'rxjs';
import {first, map, switchMap} from 'rxjs/operators';
import {PolicyDefinitionDto, PolicyDefinitionPage} from '@sovity.de/edc-client';
import {EdcApiService} from '../../../../core/services/api/edc-api.service';
import {Fetched} from '../../../../core/services/models/fetched';
import {search} from '../../../../core/utils/search-utils';
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
    private edcApiService: EdcApiService,
    private policyCardBuilder: PolicyCardBuilder,
    private readonly dialog: MatDialog,
  ) {}

  ngOnInit(): void {
    this.fetch$
      .pipe(
        switchMap(() => {
          return this.edcApiService.getPolicyDefinitionPage().pipe(
            map(
              (policyDefinitionPage): PolicyList => ({
                policyCards: this.policyCardBuilder.buildPolicyCards(
                  this.filterPolicies(policyDefinitionPage),
                ),
                numTotalPolicies: policyDefinitionPage.policies.length,
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
    policyDefinitionPage: PolicyDefinitionPage,
  ): PolicyDefinitionPage {
    return {
      ...policyDefinitionPage,
      policies: search(
        policyDefinitionPage.policies,
        this.searchText,
        (policyDefinition: PolicyDefinitionDto) => {
          return [
            policyDefinition.policyDefinitionId,
            ...policyDefinition.policy.errors,
            ...(policyDefinition.policy.constraints?.map((it) => it.left) ??
              []),
          ].filter((it) => !!it);
        },
      ),
    };
  }
}
