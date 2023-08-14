import {Injectable} from '@angular/core';
import {Observable, combineLatest, concat, interval, of} from 'rxjs';
import {filter, map, switchMap} from 'rxjs/operators';
import {
  ConnectorLimits,
  ContractAgreementCard,
  ContractAgreementPage,
} from '@sovity.de/edc-client';
import {ActiveFeatureSet} from '../../../../core/config/active-feature-set';
import {EdcApiService} from '../../../../core/services/api/edc-api.service';
import {Fetched} from '../../../../core/services/models/fetched';
import {ContractAgreementCardMapped} from '../contract-agreement-cards/contract-agreement-card-mapped';
import {ContractAgreementCardMappedService} from '../contract-agreement-cards/contract-agreement-card-mapped.service';
import {ContractAgreementPageData} from './contract-agreement-page.data';

@Injectable({providedIn: 'root'})
export class ContractAgreementPageService {
  constructor(
    private edcApiService: EdcApiService,
    private contractAgreementCardMappedService: ContractAgreementCardMappedService,
    private activeFeatureSet: ActiveFeatureSet,
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
    return combineLatest([
      this.edcApiService.getContractAgreementPage(),
      this.fetchLimits(),
    ]).pipe(
      map(([contractAgreementPage, connectorLimits]) =>
        this.buildContractAgreementPageData(
          contractAgreementPage,
          connectorLimits,
        ),
      ),
      Fetched.wrap({failureMessage: 'Failed fetching Contract Agreement Page'}),
    );
  }

  private buildContractAgreementPageData(
    contractAgreementPage: ContractAgreementPage,
    connectorLimits: ConnectorLimits | null,
  ): ContractAgreementPageData {
    let contractAgreements = this.mapContractAgreements(
      contractAgreementPage.contractAgreements,
    );

    let consumingContractAgreements = contractAgreements.filter(
      (it) => it.direction === 'CONSUMING',
    );
    let providingContractAgreements = contractAgreements.filter(
      (it) => it.direction === 'PROVIDING',
    );

    let isConsumingLimitsEnforced =
      connectorLimits?.maxActiveConsumingContractAgreements != null &&
      connectorLimits.maxActiveConsumingContractAgreements >= 0;
    if (isConsumingLimitsEnforced) {
      consumingContractAgreements =
        this.contractAgreementCardMappedService.withEnforcedLimits(
          connectorLimits?.maxActiveConsumingContractAgreements!,
          consumingContractAgreements,
        );
    }

    return {
      contractAgreements: [
        ...providingContractAgreements,
        ...consumingContractAgreements,
      ],
      consumingContractAgreements,
      providingContractAgreements,
      numTotalContractAgreements: contractAgreements.length,
    };
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
      consumingContractAgreements:
        this.contractAgreementCardMappedService.filter(
          contractAgreementPage.consumingContractAgreements,
          searchText,
        ),
      providingContractAgreements:
        this.contractAgreementCardMappedService.filter(
          contractAgreementPage.providingContractAgreements,
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

  private fetchLimits(): Observable<ConnectorLimits | null> {
    if (this.activeFeatureSet.hasConnectorLimits()) {
      return this.edcApiService.getEnterpriseEditionConnectorLimits();
    }
    return of(null);
  }
}
