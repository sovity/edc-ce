import {Injectable} from '@angular/core';
import {Observable, combineLatest} from 'rxjs';
import {map, switchMap} from 'rxjs/operators';
import {ConnectorPageQuery, ConnectorPageResult} from '@sovity.de/edc-client';
import {EdcApiService} from '../../../../core/services/api/edc-api.service';
import {Fetched} from '../../../../core/services/models/fetched';
import {ConnectorPageData} from './connector-page-data';

@Injectable({providedIn: 'root'})
export class ConnectorPageDataService {
  constructor(private edcApiService: EdcApiService) {}

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
    return this.edcApiService
      .brokerConnectors(query)
      .pipe(Fetched.wrap({failureMessage: 'Failed fetching connectors.'}));
  }
}
