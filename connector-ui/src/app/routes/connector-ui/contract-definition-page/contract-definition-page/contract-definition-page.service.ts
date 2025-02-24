import {Injectable} from '@angular/core';
import {Observable, combineLatest, of} from 'rxjs';
import {catchError, map, switchMap} from 'rxjs/operators';
import {EdcApiService} from '../../../../core/services/api/edc-api.service';
import {AssetService} from '../../../../core/services/asset.service';
import {Fetched} from '../../../../core/services/models/fetched';
import {search} from '../../../../core/utils/search-utils';
import {ContractDefinitionCard} from '../contract-definition-cards/contract-definition-card';
import {ContractDefinitionCardBuilder} from '../contract-definition-cards/contract-definition-card-builder';

export interface ContractDefinitionList {
  contractDefinitionCards: ContractDefinitionCard[];
  numTotalContractDefinitions: number;
}

@Injectable({providedIn: 'root'})
export class ContractDefinitionPageService {
  constructor(
    private edcApiService: EdcApiService,
    private assetServiceMapped: AssetService,
    private contractDefinitionCardBuilder: ContractDefinitionCardBuilder,
  ) {}

  contractDefinitionPageData$(
    refresh$: Observable<any>,
    searchText$: Observable<string>,
  ): Observable<Fetched<ContractDefinitionList>> {
    return combineLatest([
      refresh$.pipe(switchMap(() => this.fetchCards())),
      searchText$,
    ]).pipe(
      map(([fetchedCards, searchText]) =>
        fetchedCards.map((cards) => ({
          contractDefinitionCards: this.filterCards(cards, searchText),
          numTotalContractDefinitions: cards.length,
        })),
      ),
    );
  }

  filterCards(
    cards: ContractDefinitionCard[],
    searchText: string,
  ): ContractDefinitionCard[] {
    return search(cards, searchText, (card) => [
      card.id,
      card.accessPolicy.policyDefinitionId,
      card.contractPolicy.policyDefinitionId,
      ...card.criteria.map((it) => it.label),
      ...card.criteria
        .flatMap((it) => it.values)
        .flatMap((it) => it.searchTargets),
    ]);
  }
  //ed
  fetchCards(): Observable<Fetched<ContractDefinitionCard[]>> {
    return combineLatest([
      this.edcApiService.getContractDefinitionPage(),
      this.assetServiceMapped.fetchAssets().pipe(
        catchError((err) => {
          console.warn('Failed fetching assets.', err);
          return of([]);
        }),
      ),
      this.edcApiService.getPolicyDefinitionPage().pipe(
        map((policyDefinitionPage) => policyDefinitionPage.policies),
        catchError((err) => {
          console.warn('Failed fetching policy definitions.', err);
          return of([]);
        }),
      ),
    ]).pipe(
      map(([contractDefinitions, assets, policyDefinitions]) =>
        this.contractDefinitionCardBuilder.buildContractDefinitionCards(
          contractDefinitions,
          assets,
          policyDefinitions,
        ),
      ),
      Fetched.wrap({failureMessage: 'Failed fetching contract definitions'}),
    );
  }
}
