import {Injectable} from '@angular/core';
import {Observable, combineLatest, merge, of, sampleTime, scan} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {DashboardTransferAmounts, UiDataOffer} from '@sovity.de/edc-client';
import {EdcApiService} from '../../../../core/services/api/edc-api.service';
import {LastCommitInfoService} from '../../../../core/services/api/last-commit-info.service';
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
    private lastCommitInfoService: LastCommitInfoService,
    private connectorInfoPropertyGridGroupBuilder: ConnectorInfoPropertyGridGroupBuilder,
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
        failureMessage: 'Failed fetching data offers.',
      }),
      map((numOffers) => ({numCatalogEntries: numOffers})),
    );
  }

  private getAllDataOffers(): Observable<UiDataOffer[]> {
    const catalogUrls = this.catalogApiUrlService.getAllProviders();

    const dataOffers = catalogUrls.map((it) =>
      this.edcApiService
        .getCatalogPageDataOffers(it)
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
      {label: 'Completed', amount: transfers.numOk, color: '#b2e061'},
      {label: 'In Progress', amount: transfers.numRunning, color: '#7eb0d5'},
      {label: 'Error', amount: transfers.numError, color: '#fd7f6f'},
    ].filter((it) => it.amount);

    const total = transfers.numTotal;

    return {
      totalLabel: 'Total',
      totalValue: total,
      isEmpty: !total,
      emptyMessage: `No ${direction} transfer processes.`,
      labels: amounts.map((it) => it.label),
      datasets: [
        {
          label: 'Number of Transfer Processes',
          data: amounts.map((it) => it.amount),
          backgroundColor: amounts.map((it) => it.color),
        },
      ],
      options: {responsive: false},
    };
  }

  private dashboardData(): Observable<Partial<DashboardPageData>> {
    return combineLatest([
      this.lastCommitInfoService.getLastCommitInfoData().pipe(
        Fetched.wrap({
          failureMessage: 'Failed fetching Env and Jar Last Commit Data',
        }),
      ),
      this.lastCommitInfoService.getUiBuildDateDetails().pipe(
        Fetched.wrap({
          failureMessage: 'Failed fetching UI Last Build Date Data',
        }),
      ),
      this.lastCommitInfoService.getUiCommitDetails().pipe(
        Fetched.wrap({
          failureMessage: 'Failed fetching UI Last Commit Data',
        }),
      ),
      this.edcApiService.getDashboardPage().pipe(
        Fetched.wrap({
          failureMessage: 'Failed fetching Dashboard Page Data',
        }),
      ),
    ]).pipe(
      map(([lastCommitInfo, uiBuildDate, uiCommitDetails, fetched]) => ({
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
        connectorEndpoint: this.extractString(
          fetched,
          (it) => it.connectorEndpoint,
        ),
        incomingTransfersChart: fetched.map((it) =>
          this.buildTransferChart(it.transferProcessesConsuming, 'CONSUMING'),
        ),
        outgoingTransfersChart: fetched.map((it) =>
          this.buildTransferChart(it.transferProcessesProviding, 'PROVIDING'),
        ),
        connectorProperties:
          this.connectorInfoPropertyGridGroupBuilder.buildPropertyGridGroups(
            lastCommitInfo,
            uiBuildDate,
            uiCommitDetails,
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
