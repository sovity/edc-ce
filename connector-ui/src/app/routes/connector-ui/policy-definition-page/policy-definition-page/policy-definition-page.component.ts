import {Component, OnInit} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {map, switchMap} from 'rxjs/operators';
import {EdcApiService} from '../../../../core/services/api/edc-api.service';
import {Fetched} from '../../../../core/services/models/fetched';
import {search} from '../../../../core/utils/search-utils';
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
  ) {}

  ngOnInit(): void {
    this.fetch$
      .pipe(
        switchMap(() => {
          return this.edcApiService.getPolicyDefinitionPage().pipe(
            map(
              (policyDefinitionPage): PolicyList => ({
                policyCards:
                  this.policyCardBuilder.buildPolicyCards(policyDefinitionPage),

                numTotalPolicies: policyDefinitionPage.policies.length,
              }),
            ),
            map((policyList) => this.filterPolicies(policyList)),
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

  refresh() {
    this.fetch$.next(null);
  }

  private filterPolicies(policyList: PolicyList): PolicyList {
    const policyCards = search(
      policyList.policyCards,
      this.searchText,
      (policyCard: PolicyCard) => [
        policyCard.id,
        ...policyCard.irregularities,
        policyCard.searchText,
      ],
    );

    return {
      policyCards,
      numTotalPolicies: policyCards.length,
    };
  }
}
