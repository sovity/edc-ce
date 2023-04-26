import {Injectable} from '@angular/core';
import {Observable, combineLatest, concat, interval} from 'rxjs';
import {filter, map, switchMap} from 'rxjs/operators';
import {ContractAgreementCard} from '@sovity.de/edc-client';
import {Fetched} from '../../models/fetched';
import {EdcApiService} from '../../services/edc-api.service';
import {ContractAgreementCardMapped} from '../contract-agreement-cards/contract-agreement-card-mapped';
import {ContractAgreementCardMappedService} from '../contract-agreement-cards/contract-agreement-card-mapped.service';
import {ContractAgreementPageData} from './contract-agreement-page.data';

@Injectable({providedIn: 'root'})
export class ContractAgreementPageService {
  constructor(
    private edcApiService: EdcApiService,
    private contractAgreementCardMappedService: ContractAgreementCardMappedService,
  ) {}

  contractAgreementPageData$(
    refresh$: Observable<any>,
    silentPollingInterval: number,
    searchText$: Observable<string>,
  ): Observable<Fetched<ContractAgreementPageData>> {
    return combineLatest([
      refresh$.pipe(
        switchMap(() =>
          concat(
            this.fetchData(),
            this.silentRefreshing(silentPollingInterval),
          ),
        ),
      ),
      searchText$,
    ]).pipe(
      map(([fetchedData, searchText]) =>
        fetchedData.map((contractAgreementPage) =>
          this.filterContractAgreementPage(contractAgreementPage, searchText),
        ),
      ),
    );
  }

  private fetchData(): Observable<Fetched<ContractAgreementPageData>> {
    return this.edcApiService.getContractAgreementPage().pipe(
      Fetched.wrap({failureMessage: 'Failed fetching contract definitions'}),
      Fetched.map((contractAgreementPage) => ({
        contractAgreements: this.mapContractAgreements(
          contractAgreementPage.contractAgreements,
        ),
        numTotalContractAgreements:
          contractAgreementPage.contractAgreements.length,
      })),
    );
  }

  private mapContractAgreements(
    contractAgreements: ContractAgreementCard[],
  ): ContractAgreementCardMapped[] {
    return contractAgreements.map((contractAgreement) =>
      this.contractAgreementCardMappedService.buildContractAgreementCardMapped(
        contractAgreement,
      ),
    );
  }

  private filterContractAgreementPage(
    contractAgreementPage: ContractAgreementPageData,
    searchText: string,
  ): ContractAgreementPageData {
    return {
      ...contractAgreementPage,
      contractAgreements: this.contractAgreementCardMappedService.filter(
        contractAgreementPage.contractAgreements,
        searchText,
      ),
    };
  }

  private silentRefreshing(silentPollingInterval: number) {
    return interval(silentPollingInterval).pipe(
      switchMap(() => this.fetchData()),
      filter((it) => it.isReady),
    );
  }
}
