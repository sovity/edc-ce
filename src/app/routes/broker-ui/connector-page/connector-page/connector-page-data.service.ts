import {Injectable} from '@angular/core';
import {Observable, combineLatest} from 'rxjs';
import {map, switchMap} from 'rxjs/operators';
import {
  ConnectorPageQuery,
  ConnectorPageResult,
} from '@sovity.de/broker-server-client';
import {BrokerServerApiService} from '../../../../core/services/api/broker-server-api.service';
import {Fetched} from '../../../../core/services/models/fetched';
import {ConnectorPageData} from './connector-page-data';

@Injectable({providedIn: 'root'})
export class ConnectorPageDataService {
  constructor(private brokerServerApiService: BrokerServerApiService) {}

  connectorPageData$(
    refresh$: Observable<any>,
    searchText$: Observable<string>,
  ): Observable<ConnectorPageData> {
    return combineLatest([refresh$, searchText$]).pipe(
      switchMap(([_, searchText]) => this.fetchConnectors(searchText)),
      map((fetchedConnectors): ConnectorPageData => ({fetchedConnectors})),
    );
  }

  private fetchConnectors(
    searchQuery: string,
  ): Observable<Fetched<ConnectorPageResult>> {
    const query: ConnectorPageQuery = {
      searchQuery,
    };
    return this.brokerServerApiService
      .connectorPage(query)
      .pipe(Fetched.wrap({failureMessage: 'Failed fetching connectors.'}));
  }
}
