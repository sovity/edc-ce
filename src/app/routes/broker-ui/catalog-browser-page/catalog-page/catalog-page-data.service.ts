import {Injectable} from '@angular/core';
import {Observable, combineLatest} from 'rxjs';
import {map, switchMap} from 'rxjs/operators';
import {CatalogPageQuery} from '@sovity.de/edc-client';
import {EdcApiService} from '../../../../core/services/api/edc-api.service';
import {Fetched} from '../../../../core/services/models/fetched';
import {CatalogPageData} from './catalog-page-data';
import {BrokerCatalogMapper} from './mapping/broker-catalog-mapper';
import {BrokerCatalogPageResult} from './mapping/broker-catalog-page-result';

@Injectable({providedIn: 'root'})
export class CatalogPageDataService {
  constructor(
    private edcApiService: EdcApiService,
    private brokerCatalogMapper: BrokerCatalogMapper,
  ) {}

  contractOfferPageData$(
    refresh$: Observable<any>,
    searchText$: Observable<string>,
  ): Observable<CatalogPageData> {
    return combineLatest([refresh$, searchText$]).pipe(
      switchMap(([_, searchText]) => this.fetchCatalog(searchText)),
      map((fetchedDataOffers): CatalogPageData => ({fetchedDataOffers})),
    );
  }

  private fetchCatalog(
    searchQuery: string,
  ): Observable<Fetched<BrokerCatalogPageResult>> {
    const query: CatalogPageQuery = {
      searchQuery,
    };
    return this.edcApiService.brokerCatalog(query).pipe(
      Fetched.wrap({failureMessage: 'Failed fetching data offers.'}),
      Fetched.map((data) =>
        this.brokerCatalogMapper.buildUiCatalogPageResult(data),
      ),
    );
  }
}
