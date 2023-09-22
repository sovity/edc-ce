import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable, merge, of, sampleTime} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {AssetPropertyMapper} from '../asset-property-mapper';
import {ContractOffer} from '../models/contract-offer';
import {ContractOfferDto} from '../models/contract-offer-dto';
import {ContractOfferResponseDto} from '../models/contract-offer-response-dto';
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
        catchError((err: HttpErrorResponse) => {
          console.warn('Failed fetching catalog of', it, err);
          return of([] as ContractOffer[]);
        }),
      ),
    );

    return merge(...sources).pipe(
      sampleTime(50),
      map((results) => results.flat()),
    );
  }

  getContractOffers(connectorEndpoint: string): Observable<ContractOffer[]> {
    let url = this.catalogApiUrlService.buildCatalogApiUrl(connectorEndpoint);
    return this.httpClient
      .get<ContractOfferResponseDto>(url + '&limit=10000000')
      .pipe(
        map(({contractOffers}) => contractOffers),
        map((contractOffers) =>
          contractOffers.map((contractOffer) =>
            this.buildContractOffer(contractOffer, connectorEndpoint),
          ),
        ),
      );
  }

  private buildContractOffer(
    contractOfferDto: ContractOfferDto,
    connectorEndpoint: string,
  ): ContractOffer {
    return {
      ...contractOfferDto,
      asset: this.assetPropertyMapper.buildAsset({
        connectorEndpoint,
        uiAsset: contractOfferDto.asset,
      }),
    };
  }
}
