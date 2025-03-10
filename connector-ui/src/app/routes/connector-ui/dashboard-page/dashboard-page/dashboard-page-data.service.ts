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
import {Injectable} from '@angular/core';
import {Observable, combineLatest, merge, of, sampleTime, scan} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {TranslateService} from '@ngx-translate/core';
import {DashboardTransferAmounts, UiDataOffer} from '@sovity.de/edc-client';
import {EdcApiService} from '../../../../core/services/api/edc-api.service';
import {ConnectorEndpointUrlMapper} from '../../../../core/services/connector-endpoint-url-mapper';
import {ConnectorInfoPropertyGridGroupBuilder} from '../../../../core/services/connector-info-property-grid-group-builder';
import {Fetched} from '../../../../core/services/models/fetched';
import {CatalogApiUrlService} from '../../catalog-browser-page/catalog-browser-page/catalog-api-url.service';
import {DonutChartData} from '../dashboard-donut-chart/donut-chart-data';
import {DashboardPageData, defaultDashboardData} from './dashboard-page-data';

@Injectable({providedIn: 'root'})
export class DashboardPageDataService {
  constructor(
    private edcApiService: EdcApiService,
    private catalogApiUrlService: CatalogApiUrlService,
    private connectorInfoPropertyGridGroupBuilder: ConnectorInfoPropertyGridGroupBuilder,
    private translateService: TranslateService,
    private connectorEndpointUrlMapper: ConnectorEndpointUrlMapper,
  ) {}

  /**
   * Fetch {@link DashboardPageData}.
   */
  getDashboardData(): Observable<DashboardPageData> {
    const initial = defaultDashboardData();

    // Dashboard is built from different API calls
    const sources: Observable<Partial<DashboardPageData>>[] = [
      this.catalogBrowserKpis(),
      this.numCatalogs(),
      this.dashboardData(),
    ];

    // We merge all results as they come in, constructing our DashboardData
    // This allows single KPIs to have their own individual loading statuses
    return merge(...sources).pipe(
      scan((data, patch) => ({...data, ...patch}), initial),
    );
  }

  private catalogBrowserKpis(): Observable<Partial<DashboardPageData>> {
    return this.getAllDataOffers().pipe(
      map((dataOffers) => dataOffers.length),
      Fetched.wrap({
        failureMessage: this.translateService.instant(
          'dashboard_page.failed_offers',
        ),
      }),
      map((numOffers) => ({numCatalogEntries: numOffers})),
    );
  }

  private getAllDataOffers(): Observable<UiDataOffer[]> {
    const catalogUrls = this.catalogApiUrlService.getAllProviders();

    const dataOffers = catalogUrls
      .map((it) =>
        this.connectorEndpointUrlMapper.extractConnectorEndpointAndParticipantId(
          it,
        ),
      )
      .map((it) =>
        this.edcApiService
          .getCatalogPageDataOffers(it.connectorEndpoint, it.participantId)
          .pipe(catchError(() => of([]))),
      );

    return merge(...dataOffers).pipe(
      sampleTime(50),
      map((results) => results.flat()),
    );
  }

  private numCatalogs(): Observable<Partial<DashboardPageData>> {
    return of({
      numCatalogs: Fetched.ready(
        this.catalogApiUrlService.getPresetProviders().length,
      ),
    });
  }

  private buildTransferChart(
    transfers: DashboardTransferAmounts,
    direction: 'CONSUMING' | 'PROVIDING',
  ): DonutChartData {
    const amounts: {label: string; amount: number; color: string}[] = [
      {
        label: this.translateService.instant('dashboard_page.completed'),
        amount: transfers.numOk,
        color: '#b2e061',
      },
      {
        label: this.translateService.instant('dashboard_page.progress'),
        amount: transfers.numRunning,
        color: '#7eb0d5',
      },
      {
        label: this.translateService.instant('dashboard_page.error'),
        amount: transfers.numError,
        color: '#fd7f6f',
      },
    ].filter((it) => it.amount);

    const total = transfers.numTotal;
    const emptyMessage =
      direction === 'CONSUMING'
        ? this.translateService.instant('dashboard_page.no_transfer')
        : this.translateService.instant('dashboard_page.no_transfer2');
    return {
      totalLabel: this.translateService.instant('general.total'),
      totalValue: total,
      isEmpty: !total,
      emptyMessage,
      labels: amounts.map((it) => it.label),
      datasets: [
        {
          label: this.translateService.instant('dashboard_page.num_transfer'),
          data: amounts.map((it) => it.amount),
          backgroundColor: amounts.map((it) => it.color),
        },
      ],
      options: {responsive: false},
    };
  }

  private dashboardData(): Observable<Partial<DashboardPageData>> {
    return combineLatest([
      this.edcApiService.buildInfo().pipe(
        Fetched.wrap({
          failureMessage: this.translateService.instant(
            'dashboard_page.failed_versions',
          ),
        }),
      ),
      this.edcApiService.getDashboardPage().pipe(
        Fetched.wrap({
          failureMessage: this.translateService.instant(
            'dashboard_page.failed_dashboard',
          ),
        }),
      ),
    ]).pipe(
      map(([versionInfo, fetched]) => ({
        title: this.extractString(fetched, (it) => it.connectorTitle),
        description: this.extractString(
          fetched,
          (it) => it.connectorDescription,
        ),
        numAssets: fetched.map((it) => it.numAssets),
        numPolicies: fetched.map((it) => it.numPolicies),
        numContractDefinitions: fetched.map((it) => it.numContractDefinitions),
        numContractAgreements: fetched.map(
          (it) =>
            it.numContractAgreementsConsuming +
            it.numContractAgreementsProviding,
        ),
        connectorEndpointAndParticipantId: this.extractString(fetched, (it) =>
          this.connectorEndpointUrlMapper.mergeConnectorEndpointAndParticipantId(
            it.connectorEndpoint,
            it.connectorParticipantId,
          ),
        ),
        incomingTransfersChart: fetched.map((it) =>
          this.buildTransferChart(it.transferProcessesConsuming, 'CONSUMING'),
        ),
        outgoingTransfersChart: fetched.map((it) =>
          this.buildTransferChart(it.transferProcessesProviding, 'PROVIDING'),
        ),
        connectorProperties:
          this.connectorInfoPropertyGridGroupBuilder.buildPropertyGridGroups(
            versionInfo,
            fetched,
          ),
      })),
    );
  }

  private extractString<T>(
    fetched: Fetched<T>,
    extractor: (item: T) => string,
  ): string {
    return fetched.match({
      ifLoading: () => 'Loading...',
      ifError: () => 'Failed loading.',
      ifOk: extractor,
    });
  }
}
