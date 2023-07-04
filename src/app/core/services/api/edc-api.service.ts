import {Inject, Injectable} from '@angular/core';
import {Observable, from} from 'rxjs';
import {
  CatalogPageQuery,
  CatalogPageResult,
  ConnectorLimits,
  ConnectorPageQuery,
  ConnectorPageResult,
  ContractAgreementPage,
  EdcClient,
  KpiResult,
  buildEdcClient,
} from '@sovity.de/edc-client';
import {APP_CONFIG, AppConfig} from '../../config/app-config';

@Injectable({providedIn: 'root'})
export class EdcApiService {
  edcClient: EdcClient;

  constructor(@Inject(APP_CONFIG) private config: AppConfig) {
    this.edcClient = buildEdcClient({
      managementApiUrl: this.config.managementApiUrl,
      managementApiKey: this.config.managementApiKey,
    });
  }

  getContractAgreementPage(): Observable<ContractAgreementPage> {
    return from(this.edcClient.uiApi.contractAgreementEndpoint());
  }

  getKpis(): Observable<KpiResult> {
    return from(this.edcClient.useCaseApi.kpiEndpoint());
  }

  getEnterpriseEditionConnectorLimits(): Observable<ConnectorLimits> {
    return from(this.edcClient.enterpriseEditionApi.connectorLimits());
  }

  brokerCatalog(
    catalogPageQuery: CatalogPageQuery,
  ): Observable<CatalogPageResult> {
    return from(this.edcClient.brokerServerApi.catalogPage({catalogPageQuery}));
  }

  brokerConnectors(
    connectorPageQuery: ConnectorPageQuery,
  ): Observable<ConnectorPageResult> {
    return from(
      this.edcClient.brokerServerApi.connectorPage({connectorPageQuery}),
    );
  }
}
