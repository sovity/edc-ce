import {Injectable} from '@angular/core';
import {Observable, combineLatest, merge, of, scan} from 'rxjs';
import {map} from 'rxjs/operators';
import {CatalogApiUrlService} from '../../../../core/services/api/catalog-api-url.service';
import {ContractOfferService} from '../../../../core/services/api/contract-offer.service';
import {EdcApiService} from '../../../../core/services/api/edc-api.service';
import {
  ContractAgreementService,
  ContractDefinitionService,
  PolicyService,
  TransferProcessDto,
  TransferProcessService,
} from '../../../../core/services/api/legacy-managent-api-client';
import {
  ConnectorInfoPropertyGridGroupBuilder
} from '../../../../core/services/connector-info-property-grid-group-builder';
import {LastCommitInfoService} from '../../../../core/services/last-commit-info.service';
import {Fetched} from '../../../../core/services/models/fetched';
import {TransferProcessStates} from '../../../../core/services/models/transfer-process-states';
import {TransferProcessUtils} from '../../../../core/services/transfer-process-utils';
import {DonutChartData} from '../dashboard-donut-chart/donut-chart-data';
import {DashboardPageData, defaultDashboardData} from './dashboard-page-data';

@Injectable({providedIn: 'root'})
export class DashboardPageDataService {
  constructor(
    private edcApiService: EdcApiService,
    private catalogBrowserService: ContractOfferService,
    private contractDefinitionService: ContractDefinitionService,
    private contractAgreementService: ContractAgreementService,
    private policyService: PolicyService,
    private catalogApiUrlService: CatalogApiUrlService,
    private transferProcessService: TransferProcessService,
    private transferProcessUtils: TransferProcessUtils,
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
    return this.policyService.getAllPolicies(0, 10_000_000).pipe(
      map((policies) => policies.length),
      Fetched.wrap({failureMessage: 'Failed fetching number of policies.'}),
      map((numPolicies) => ({numPolicies})),
    );
  }

  private contractDefinitionKpis(): Observable<Partial<DashboardPageData>> {
    return this.contractDefinitionService
      .getAllContractDefinitions(0, 10_000_000)
      .pipe(
        map((contractDefinitions) => contractDefinitions.length),
        Fetched.wrap({
          failureMessage: 'Failed fetching number of contract definitions.',
        }),
        map((numContractDefinitions) => ({numContractDefinitions})),
      );
  }

  private contractAgreementKpis(): Observable<Partial<DashboardPageData>> {
    return this.contractAgreementService.getAllAgreements(0, 10_000_000).pipe(
      map((contractAgreements) => contractAgreements.length),
      Fetched.wrap({
        failureMessage: 'Failed fetching contract agreements.',
      }),
      map((numContractAgreements) => ({numContractAgreements})),
    );
  }

  private catalogBrowserKpis(): Observable<Partial<DashboardPageData>> {
    return this.catalogBrowserService.getAllContractOffers().pipe(
      map((contractDefinitions) => contractDefinitions.length),
      Fetched.wrap({
        failureMessage: 'Failed fetching data offers.',
      }),
      map((numOffers) => ({numCatalogEntries: numOffers})),
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
    return this.transferProcessService
      .getAllTransferProcesses(0, 10_000_000)
      .pipe(
        Fetched.wrap({
          failureMessage: 'Failed fetching transfer processes.',
        }),
        map((transferData) => ({
          incomingTransfersChart: transferData.map((it) =>
            this.buildTransferChart(it, 'incoming'),
          ),
          outgoingTransfersChart: transferData.map((it) =>
            this.buildTransferChart(it, 'outgoing'),
          ),
        })),
      );
  }

  private buildTransferChart(
    transfers: TransferProcessDto[],
    direction: 'incoming' | 'outgoing',
  ): DonutChartData {
    const filteredTransfers =
      direction === 'incoming'
        ? transfers.filter((it) => this.transferProcessUtils.isIncoming(it))
        : transfers.filter((it) => this.transferProcessUtils.isOutgoing(it));

    // Use the keys of the TransferProcessesStates Enum as order
    const order = Object.keys(TransferProcessStates);
    const states = [...new Set(filteredTransfers.map((it) => it.state))].sort(
      (a, b) => order.indexOf(a) - order.indexOf(b),
    );

    const colorsByState = new Map<string, string>();
    colorsByState.set('IN_PROGRESS', '#7eb0d5');
    colorsByState.set('ERROR', '#fd7f6f');
    colorsByState.set('COMPLETED', '#b2e061');
    const defaultColor = '#bd7ebe';

    const amountsByState = states.map(
      (state) => filteredTransfers.filter((it) => it.state === state).length,
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
