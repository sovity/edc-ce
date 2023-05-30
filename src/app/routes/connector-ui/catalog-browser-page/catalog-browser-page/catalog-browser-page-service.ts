import {Injectable} from '@angular/core';
import {Observable, combineLatest} from 'rxjs';
import {map, switchMap} from 'rxjs/operators';
import {CatalogApiUrlService} from '../../../../core/services/api/catalog-api-url.service';
import {ContractOfferService} from '../../../../core/services/api/contract-offer.service';
import {ContractOffer} from '../../../../core/services/models/contract-offer';
import {Fetched} from '../../../../core/services/models/fetched';
import {MultiFetched} from '../../../../core/services/models/multi-fetched';
import {assetSearchTargets, search} from '../../../../core/utils/search-utils';
import {
  CatalogBrowserPageData,
  ContractOfferRequest,
} from './catalog-browser-page.data';

@Injectable({providedIn: 'root'})
export class CatalogBrowserPageService {
  constructor(
    private contractOfferService: ContractOfferService,
    private catalogApiUrlService: CatalogApiUrlService,
  ) {}

  contractOfferPageData$(
    refresh$: Observable<any>,
    searchText$: Observable<string>,
  ): Observable<CatalogBrowserPageData> {
    return combineLatest([
      refresh$.pipe(switchMap(() => this.fetchCatalogs())),
      searchText$,
    ]).pipe(
      map(([data, searchText]): CatalogBrowserPageData => {
        // Merge fetch results
        const contractOffers = data.requestTotals.data.flat();

        // Apply filter
        const filteredContractOffers = this.filterContractOffers(
          contractOffers,
          searchText,
        );

        return {
          requests: data.requests,
          requestTotals: data.requestTotals,
          filteredContractOffers,
          numTotalContractOffers: contractOffers.length,
        };
      }),
    );
  }

  filterContractOffers(
    contractOffers: ContractOffer[],
    searchText: string,
  ): ContractOffer[] {
    return search(contractOffers, searchText, (contractOffer) =>
      assetSearchTargets(contractOffer.asset),
    );
  }

  fetchCatalogs(): Observable<
    Pick<CatalogBrowserPageData, 'requests' | 'requestTotals'>
  > {
    // Prepare to fetch individual Catalogs
    const urls = this.catalogApiUrlService.getAllProviders();
    const sources = urls.map((it) =>
      this.contractOfferService
        .getContractOffers(it)
        .pipe(Fetched.wrap({failureMessage: 'Failed fetching catalog.'})),
    );

    return combineLatest(sources).pipe(
      map((results) => MultiFetched.aggregate(results)),
      map(
        (
          requestTotals: MultiFetched<ContractOffer[]>,
        ): Pick<CatalogBrowserPageData, 'requests' | 'requestTotals'> => {
          const presetUrls = this.catalogApiUrlService.getPresetProviders();
          return {
            requestTotals,
            requests: requestTotals.results.map(
              (data, index): ContractOfferRequest => ({
                url: urls[index],
                isPresetUrl: presetUrls.includes(urls[index]),
                data,
              }),
            ),
          };
        },
      ),
    );
  }
}
