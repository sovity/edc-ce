/*
 * Copyright 2025 sovity GmbH
 * Copyright 2024 Fraunhofer Institute for Applied Information Technology FIT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 *     Fraunhofer FIT - contributed initial internationalization support
 */
import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';
import {
  BehaviorSubject,
  EMPTY,
  Observable,
  Subject,
  combineLatest,
  concat,
  distinctUntilChanged,
  interval,
  merge,
  of,
  share,
  switchMap,
} from 'rxjs';
import {catchError, filter, map, takeUntil} from 'rxjs/operators';
import {ContractTerminationStatus} from '@sovity.de/edc-client';
import {EdcApiService} from '../../../../core/services/api/edc-api.service';
import {Fetched} from '../../../../core/services/models/fetched';
import {value$} from '../../../../core/utils/form-group-utils';
import {filterNotNull} from '../../../../core/utils/rxjs-utils';
import {AssetDetailDialogDataService} from '../../../../shared/business/asset-detail-dialog/asset-detail-dialog-data.service';
import {AssetDetailDialogService} from '../../../../shared/business/asset-detail-dialog/asset-detail-dialog.service';
import {ContractAgreementCardMapped} from '../contract-agreement-cards/contract-agreement-card-mapped';
import {ContractAgreementCardMappedService} from '../contract-agreement-cards/contract-agreement-card-mapped.service';
import {ContractAgreementPageData} from './contract-agreement-page.data';
import {ContractAgreementPageService} from './contract-agreement-page.service';

@Component({
  selector: 'app-contract-agreement-page',
  templateUrl: './contract-agreement-page.component.html',
  styleUrls: ['./contract-agreement-page.component.scss'],
})
export class ContractAgreementPageComponent implements OnInit, OnDestroy {
  page: Fetched<ContractAgreementPageData> = Fetched.empty();
  page$: Observable<Fetched<ContractAgreementPageData>> = new Subject();
  searchText = new FormControl<string>('');
  terminationFilterControl: FormControl = new FormControl<string>('ONGOING');

  terminationFilter: ContractTerminationStatus | null = 'ONGOING';

  private fetch$ = new BehaviorSubject(null);

  constructor(
    private assetDetailDialogDataService: AssetDetailDialogDataService,
    private assetDetailDialogService: AssetDetailDialogService,
    private contractAgreementPageService: ContractAgreementPageService,
    private contractAgreementCardMappedService: ContractAgreementCardMappedService,
    private apiService: EdcApiService,
  ) {}

  ngOnInit(): void {
    this.fetchContracts();
  }

  fetchContracts() {
    this.page$ = this.contractAgreementPageService
      .contractAgreementPageData$(
        this.fetch$,
        5000,
        this.searchText$(),
        this.terminationFilter,
      )
      .pipe(takeUntil(this.ngOnDestroy$), share());
    this.page$.subscribe((contractAgreementList) => {
      this.page = contractAgreementList;
    });
  }

  onTerminationFilterChange() {
    if (this.terminationFilterControl.value === 'all') {
      this.terminationFilter = null;
    } else {
      this.terminationFilter = this.terminationFilterControl
        .value as ContractTerminationStatus;
    }

    this.fetchContracts();
  }

  onContractAgreementClick(card: ContractAgreementCardMapped) {
    const refresh$ = new Subject();

    const cardUpdates$ = merge(interval(5000), refresh$).pipe(
      switchMap(() =>
        this.apiService
          .getContractAgreementById(card.contractAgreementId)
          .pipe(catchError(() => EMPTY)),
      ),
      map((it) =>
        this.contractAgreementCardMappedService.buildContractAgreementCardMapped(
          it,
        ),
      ),
    );

    const cardUpdatesWithCorrectActive$ = combineLatest([
      this.card$(card.contractAgreementId),
      cardUpdates$,
    ]).pipe(
      map(([oldCard, newCard]) => ({
        ...newCard,
        isConsumingLimitsEnforced: oldCard.isConsumingLimitsEnforced,
      })),
    );

    const dialogData$ = merge(of(card), cardUpdatesWithCorrectActive$).pipe(
      map((card) =>
        this.assetDetailDialogDataService.contractAgreementDetails(card, () =>
          refresh$.next(undefined),
        ),
      ),
    );

    return this.assetDetailDialogService.open(dialogData$, this.ngOnDestroy$);
  }

  private card$(
    contractAgreementId: string,
  ): Observable<ContractAgreementCardMapped> {
    return concat(of(this.page), this.page$).pipe(
      filter((fetched) => fetched.isReady),
      map((fetched) => fetched.data),
      map((page) =>
        page.contractAgreements.find(
          (it) => it.contractAgreementId === contractAgreementId,
        ),
      ),
      filterNotNull(),
    );
  }

  refresh() {
    this.fetch$.next(null);
  }

  private searchText$(): Observable<string> {
    return (value$(this.searchText) as Observable<string>).pipe(
      map((it) => (it ?? '').trim()),
      distinctUntilChanged(),
    );
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
