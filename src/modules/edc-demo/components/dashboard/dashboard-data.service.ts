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
import {CatalogBrowserService} from '../../services/catalog-browser.service';
import {ChartColorService} from './chart-color.service';
import {DashboardData, defaultDashboardData} from './dashboard-data';

@Injectable({providedIn: 'root'})
export class DashboardDataService {
  constructor(
    private appConfigService: AppConfigService,
    private catalogBrowserService: CatalogBrowserService,
    private contractDefinitionService: ContractDefinitionService,
    private contractAgreementService: ContractAgreementService,
    private policyService: PolicyService,
    private catalogApiUrlService: CatalogApiUrlService,
    private transferProcessService: TransferProcessService,
    private assetService: AssetService,
    private chartColorService: ChartColorService,
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
      map((contractAgreements) =>
        this.buildContractAgreementsChart(contractAgreements),
      ),
      Fetched.wrap({
        failureMessage: 'Failed fetching contract agreements.',
      }),
      map((contractAgreementChart) => ({
        contractAgreementChart,
      })),
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
    return this.catalogBrowserService.getContractOffers().pipe(
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
      map((transferData) => this.buildTransferChart(transferData)),
      Fetched.wrap({
        failureMessage: 'Failed fetching transfer processes.',
      }),
      map((transfersChart) => ({transfersChart})),
    );
  }

  private buildContractAgreementsChart(list: ContractAgreementDto[]) {
    const total = list.length;
    const transferable = list.filter(
      this.isTransferableContractAgreement,
    ).length;

    const elements = [
      {label: 'Transferable', amount: transferable},
      {label: 'Not Transferable', amount: total - transferable},
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

  private buildTransferChart(transfers: TransferProcessDto[]) {
    // Use the keys of the TransferProcessesStates Enum as order
    const order = Object.keys(TransferProcessStates);
    const states = [...new Set(transfers.map((it) => it.state))].sort(
      (a, b) => order.indexOf(a) - order.indexOf(b),
    );

    const amountsByState = states.map(
      (state) => transfers.filter((it) => it.state === state).length,
    );

    return {
      labels: states,
      datasets: [
        {
          label: 'Number of Transfer Processes',
          data: amountsByState,
          backgroundColor: this.chartColorService.getColors(states.length, 0),
        },
      ],
      options: {responsive: false},
    };
  }
}
