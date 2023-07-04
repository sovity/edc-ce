import {Inject, Injectable} from '@angular/core';
import {Observable, from} from 'rxjs';
import {
  BrokerServerClient,
  CatalogPageQuery,
  CatalogPageResult,
  ConnectorDetailPageQuery,
  ConnectorDetailPageResult,
  ConnectorPageQuery,
  ConnectorPageResult,
  DataOfferDetailPageQuery,
  DataOfferDetailPageResult,
  buildBrokerServerClient,
} from '@sovity.de/broker-server-client';
import {APP_CONFIG, AppConfig} from '../../config/app-config';

@Injectable({providedIn: 'root'})
export class BrokerServerApiService {
  brokerServerClient: BrokerServerClient;

  constructor(@Inject(APP_CONFIG) private config: AppConfig) {
    this.brokerServerClient = buildBrokerServerClient({
      managementApiUrl: this.config.managementApiUrl,
      managementApiKey: this.config.managementApiKey,
    });
  }

  catalogPage(
    catalogPageQuery: CatalogPageQuery,
  ): Observable<CatalogPageResult> {
    return from(
      this.brokerServerClient.brokerServerApi.catalogPage({catalogPageQuery}),
    );
  }

  dataOfferDetailPage(
    dataOfferDetailPageQuery: DataOfferDetailPageQuery,
  ): Observable<DataOfferDetailPageResult> {
    return from(
      this.brokerServerClient.brokerServerApi.dataOfferDetailPage({
        dataOfferDetailPageQuery,
      }),
    );
  }

  connectorPage(
    connectorPageQuery: ConnectorPageQuery,
  ): Observable<ConnectorPageResult> {
    return from(
      this.brokerServerClient.brokerServerApi.connectorPage({
        connectorPageQuery,
      }),
    );
  }

  connectorDetailPage(
    connectorDetailPageQuery: ConnectorDetailPageQuery,
  ): Observable<ConnectorDetailPageResult> {
    return from(
      this.brokerServerClient.brokerServerApi.connectorDetailPage({
        connectorDetailPageQuery,
      }),
    );
  }
}
