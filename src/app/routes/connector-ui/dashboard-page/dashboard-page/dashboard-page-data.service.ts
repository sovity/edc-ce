import {Injectable} from '@angular/core';
import {Observable, combineLatest, merge, of, sampleTime, scan} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {TransferHistoryEntry, UiDataOffer} from '@sovity.de/edc-client';
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
      this.assetKpis(),
      this.catalogBrowserKpis(),
      this.contractAgreementKpis(),
      this.contractDefinitionKpis(),
      this.numCatalogs(),
      this.policyKpis(),
      this.transferProcessKpis(),
      this.connectorProperties(),
    ];

    // We merge all results as they come in, constructing our DashboardData
    // This allows single KPIs to have their own individual loading statuses
    return merge(...sources).pipe(
      scan((data, patch) => ({...data, ...patch}), initial),
    );
  }

  private policyKpis(): Observable<Partial<DashboardPageData>> {
    return this.edcApiService.getPolicyDefinitionPage().pipe(
      map((policyDefinitionPage) => policyDefinitionPage.policies.length),
      Fetched.wrap({failureMessage: 'Failed fetching number of policies.'}),
      map((numPolicies) => ({numPolicies})),
    );
  }

  private contractDefinitionKpis(): Observable<Partial<DashboardPageData>> {
    return this.edcApiService.getContractDefinitionPage().pipe(
      map((page) => page.contractDefinitions.length),
      Fetched.wrap({
        failureMessage: 'Failed fetching number of contract definitions.',
      }),
      map((numContractDefinitions) => ({numContractDefinitions})),
    );
  }

  private contractAgreementKpis(): Observable<Partial<DashboardPageData>> {
    return this.edcApiService.getContractAgreementPage().pipe(
      map((page) => page.contractAgreements.length),
      Fetched.wrap({
        failureMessage: 'Failed fetching contract agreements.',
      }),
      map((numContractAgreements) => ({numContractAgreements})),
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

  private assetKpis(): Observable<Partial<DashboardPageData>> {
    return this.edcApiService.getAssetPage().pipe(
      map((assetPage) => assetPage.assets.length),
      Fetched.wrap({
        failureMessage: 'Failed fetching assets.',
      }),
      map((numAssets) => ({numAssets})),
    );
  }

  private numCatalogs(): Observable<Partial<DashboardPageData>> {
    return of({
      numCatalogs: Fetched.ready(
        this.catalogApiUrlService.getPresetProviders().length,
      ),
    });
  }

  private transferProcessKpis(): Observable<Partial<DashboardPageData>> {
    return this.edcApiService.getTransferHistoryPage().pipe(
      Fetched.wrap({
        failureMessage: 'Failed fetching transfer processes.',
      }),
      map((transferData) => ({
        incomingTransfersChart: transferData.map((it) =>
          this.buildTransferChart(it.transferEntries, 'CONSUMING'),
        ),
        outgoingTransfersChart: transferData.map((it) =>
          this.buildTransferChart(it.transferEntries, 'PROVIDING'),
        ),
      })),
    );
  }

  private buildTransferChart(
    transfers: TransferHistoryEntry[],
    direction: 'CONSUMING' | 'PROVIDING',
  ): DonutChartData {
    const filteredTransfers =
      direction === 'CONSUMING'
        ? transfers.filter((it) => it.direction === 'CONSUMING')
        : transfers.filter((it) => it.direction === 'PROVIDING');

    const states = [
      ...new Set(
        filteredTransfers
          .sort((a, b) => a.state.code - b.state.code)
          .map((it) => it.state.name),
      ),
    ];

    const colorsByState = new Map<string, string>();
    colorsByState.set('IN_PROGRESS', '#7eb0d5');
    colorsByState.set('ERROR', '#fd7f6f');
    colorsByState.set('COMPLETED', '#b2e061');
    const defaultColor = '#bd7ebe';

    const amountsByState = states.map(
      (state) =>
        filteredTransfers.filter((it) => it.state.name === state).length,
    );

    return {
      totalLabel: 'Total',
      totalValue: filteredTransfers.length,
      isEmpty: !filteredTransfers.length,
      emptyMessage: `No ${direction} transfer processes.`,

      labels: states,
      datasets: [
        {
          label: 'Number of Transfer Processes',
          data: amountsByState,
          backgroundColor: states.map(
            (it) => colorsByState.get(it) ?? defaultColor,
          ),
        },
      ],
      options: {responsive: false},
    };
  }

  private connectorProperties(): Observable<Partial<DashboardPageData>> {
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
    ]).pipe(
      map(([lastCommitInfo, uiBuildDate, uiCommitDetails]) => ({
        connectorProperties:
          this.connectorInfoPropertyGridGroupBuilder.buildPropertyGridGroups(
            lastCommitInfo,
            uiBuildDate,
            uiCommitDetails,
          ),
      })),
    );
  }
}
