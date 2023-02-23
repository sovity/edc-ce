import {Injectable} from '@angular/core';
import {Observable, combineLatest, throwError} from 'rxjs';
import {map, switchMap, timeout} from 'rxjs/operators';
import {ContractOffer} from '../../models/contract-offer';
import {Fetched} from '../../models/fetched';
import {MultiFetched} from '../../models/multi-fetched';
import {CatalogApiUrlService} from '../../services/catalog-api-url.service';
import {ContractOfferService} from '../../services/contract-offer.service';
import {NotificationService} from '../../services/notification.service';
import {assetSearchTargets, search} from '../../utils/search-utils';
import {
  CatalogBrowserPageData,
  ContractOfferRequest,
} from './catalog-browser-page.data';

@Injectable({providedIn: 'root'})
export class CatalogBrowserPageService {
  constructor(
    private contractOfferService: ContractOfferService,
    private notificationService: NotificationService,
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
      this.contractOfferService.getContractOffers(it).pipe(
        timeout({
          first: 5000,
          with: () => throwError(() => new Error(`Timed out after 5s.`)),
        }),
        Fetched.wrap({failureMessage: 'Failed fetching catalog.'}),
      ),
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
