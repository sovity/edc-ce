import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {
  MonoTypeOperatorFunction,
  Observable,
  forkJoin,
  of,
  throwError,
} from 'rxjs';
import {catchError, map, timeout} from 'rxjs/operators';
import {ContractOffer} from '../models/contract-offer';
import {ContractOfferDto} from '../models/contract-offer-dto';
import {ContractOfferResponseDto} from '../models/contract-offer-response-dto';
import {AssetPropertyMapper} from './asset-property-mapper';
import {CatalogApiUrlService} from './catalog-api-url.service';

/**
 * Combines several services that are used from the {@link CatalogBrowserComponent}
 */
@Injectable({
  providedIn: 'root',
})
export class ContractOfferService {
  constructor(
    private httpClient: HttpClient,
    private catalogApiUrlService: CatalogApiUrlService,
    private assetPropertyMapper: AssetPropertyMapper,
  ) {}

  getAllContractOffers(): Observable<ContractOffer[]> {
    const catalogUrls = this.catalogApiUrlService.getAllProviders();

    const sources = catalogUrls.map((it) =>
      this.getContractOffers(it).pipe(
        timeout({
          first: 15000,
          with: () => {
            console.warn(`Timed out fetching catalog of ${it}`);
            return of([]);
          },
        }),
        catchError((err: HttpErrorResponse) => {
          console.warn('Failed fetching catalog of', it, err);
          return of([] as ContractOffer[]);
        }),
      ),
    );

    return forkJoin(sources).pipe(map((results) => results.flat()));
  }

  getContractOffers(connectorEndpoint: string): Observable<ContractOffer[]> {
    let url = this.catalogApiUrlService.buildCatalogApiUrl(connectorEndpoint);
    return this.httpClient
      .get<ContractOfferResponseDto>(url + '&limit=10000000')
      .pipe(
        map(({contractOffers}) => contractOffers),
        map((contractOffers) =>
          contractOffers.map((contractOffer) =>
            this.buildContractOffer(contractOffer),
          ),
        ),
      );
  }

  private buildContractOffer(
    contractOfferDto: ContractOfferDto,
  ): ContractOffer {
    return {
      ...contractOfferDto,
      asset: this.assetPropertyMapper.buildAssetFromProperties(
        contractOfferDto.asset.properties,
      ),
    };
  }

  timeout<T>(timeoutInMs: number, msg: string): MonoTypeOperatorFunction<T> {
    return timeout({
      first: timeoutInMs,
      with: () => throwError(() => new Error(msg)),
    });
  }
}
