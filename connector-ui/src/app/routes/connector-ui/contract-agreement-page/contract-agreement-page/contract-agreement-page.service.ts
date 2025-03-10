/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Injectable} from '@angular/core';
import {Observable, combineLatest, concat, interval, of} from 'rxjs';
import {filter, map, switchMap} from 'rxjs/operators';
import {
  ConnectorLimits,
  ContractAgreementCard,
  ContractAgreementPage,
  ContractTerminationStatus,
  GetContractAgreementPageRequest,
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

  activeTerminationFilter?: ContractTerminationStatus | null;

  contractAgreementPageData$(
    refresh$: Observable<any>,
    silentPollingInterval: number,
    searchText$: Observable<string>,
    terminationStatusFilter?: ContractTerminationStatus | null,
  ): Observable<Fetched<ContractAgreementPageData>> {
    this.activeTerminationFilter = terminationStatusFilter;
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
    const requestBody: GetContractAgreementPageRequest = {
      contractAgreementPageQuery: {
        terminationStatus: this.activeTerminationFilter ?? undefined,
      },
    };

    return combineLatest([
      this.edcApiService.getContractAgreementPage(requestBody),
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
    const contractAgreements = this.mapContractAgreements(
      contractAgreementPage.contractAgreements,
    );

    let consumingContractAgreements = contractAgreements.filter(
      (it) => it.direction === 'CONSUMING',
    );
    const providingContractAgreements = contractAgreements.filter(
      (it) => it.direction === 'PROVIDING',
    );

    const isConsumingLimitsEnforced =
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
