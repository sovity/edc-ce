import {Injectable} from '@angular/core';
import {Observable, merge, of, scan} from 'rxjs';
import {map} from 'rxjs/operators';
import {AppConfigService} from '../../../app/config/app-config.service';
import {
  AssetService,
  ContractAgreementDto,
  ContractAgreementService,
  ContractDefinitionService,
  PolicyService,
  TransferProcessDto,
  TransferProcessService,
} from '../../../edc-dmgmt-client';
import {Fetched} from '../../models/fetched';
import {TransferProcessStates} from '../../models/transfer-process-states';
import {CatalogApiUrlService} from '../../services/catalog-api-url.service';
import {ContractOfferService} from '../../services/contract-offer.service';
import {TransferProcessUtils} from '../../services/transfer-process-utils';
import {DonutChartData} from '../dashboard-donut-chart/donut-chart-data';
import {ChartColorService} from './chart-color.service';
import {DashboardData, defaultDashboardData} from './dashboard-data';

@Injectable({providedIn: 'root'})
export class DashboardDataService {
  constructor(
    private appConfigService: AppConfigService,
    private catalogBrowserService: ContractOfferService,
    private contractDefinitionService: ContractDefinitionService,
    private contractAgreementService: ContractAgreementService,
    private policyService: PolicyService,
    private catalogApiUrlService: CatalogApiUrlService,
    private transferProcessService: TransferProcessService,
    private assetService: AssetService,
    private chartColorService: ChartColorService,
    private transferProcessUtils: TransferProcessUtils,
  ) {}

  /**
   * Fetch {@link DashboardData}.
   */
  getDashboardData(): Observable<DashboardData> {
    const initial = defaultDashboardData();

    // Dashboard is built from different API calls
    const sources: Observable<Partial<DashboardData>>[] = [
      this.assetKpis(),
      this.catalogBrowserKpis(),
      this.connectorInformation(),
      this.contractAgreementKpis(),
      this.contractDefinitionKpis(),
      this.numCatalogs(),
      this.policyKpis(),
      this.transferProcessKpis(),
    ];

    // We merge all results as they come in, constructing our DashboardData
    // This allows single KPIs to have their own individual loading statuses
    return merge(...sources).pipe(
      scan((data, patch) => ({...data, ...patch}), initial),
    );
  }

  private policyKpis(): Observable<Partial<DashboardData>> {
    return this.policyService.getAllPolicies().pipe(
      map((policies) => policies.length),
      Fetched.wrap({failureMessage: 'Failed fetching number of policies.'}),
      map((numPolicies) => ({numPolicies})),
    );
  }

  private contractDefinitionKpis(): Observable<Partial<DashboardData>> {
    return this.contractDefinitionService.getAllContractDefinitions().pipe(
      map((contractDefinitions) => contractDefinitions.length),
      Fetched.wrap({
        failureMessage: 'Failed fetching number of contract definitions.',
      }),
      map((numContractDefinitions) => ({numContractDefinitions})),
    );
  }

  private contractAgreementKpis(): Observable<Partial<DashboardData>> {
    return this.contractAgreementService.getAllAgreements().pipe(
      map((contractAgreements) => contractAgreements.length),
      Fetched.wrap({
        failureMessage: 'Failed fetching contract agreements.',
      }),
      map((numContractAgreements) => ({numContractAgreements})),
    );
  }

  private isTransferableContractAgreement(
    contractAgreement: ContractAgreementDto,
  ): boolean {
    const now = new Date();
    return (
      (!contractAgreement.contractStartDate ||
        now >= new Date(contractAgreement.contractStartDate)) &&
      (!contractAgreement.contractEndDate ||
        now <= new Date(contractAgreement.contractEndDate))
    );
  }

  private catalogBrowserKpis(): Observable<Partial<DashboardData>> {
    return this.catalogBrowserService.getAllContractOffers().pipe(
      map((contractDefinitions) => contractDefinitions.length),
      Fetched.wrap({
        failureMessage: 'Failed fetching data offers.',
      }),
      map((numOffers) => ({numCatalogEntries: numOffers})),
    );
  }

  private assetKpis(): Observable<Partial<DashboardData>> {
    return this.assetService.getAllAssets().pipe(
      map((assets) => assets.length),
      Fetched.wrap({
        failureMessage: 'Failed fetching assets.',
      }),
      map((numAssets) => ({numAssets})),
    );
  }

  private connectorInformation(): Observable<Partial<DashboardData>> {
    return of({
      connectorUrl: this.appConfigService.config.originator,
      connectorOrganization:
        this.appConfigService.config.originatorOrganization,
    });
  }

  private numCatalogs(): Observable<Partial<DashboardData>> {
    return of({
      numCatalogs: Fetched.ready(
        this.catalogApiUrlService.getPresetProviders().length,
      ),
    });
  }

  private transferProcessKpis(): Observable<Partial<DashboardData>> {
    return this.transferProcessService.getAllTransferProcesses().pipe(
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

  private buildContractAgreementsChart(list: ContractAgreementDto[]) {
    const total = list.length;

    const elements = [
      {label: 'Contract Agreements (Incoming & Outgoing)', amount: total},
    ].filter((it) => it.amount != 0);

    return {
      labels: elements.map((it) => it.label),
      datasets: [
        {
          label: 'Contract Agreements',
          data: elements.map((it) => it.amount),
          backgroundColor: this.chartColorService.getColors(elements.length, 3),
        },
      ],
      options: {responsive: false},
    };
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
}
