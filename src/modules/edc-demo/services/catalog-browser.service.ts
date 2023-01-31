import {
  HttpClient,
  HttpErrorResponse,
  HttpHeaders,
  HttpParams,
} from '@angular/common/http';
import {Inject, Injectable} from '@angular/core';
import {EMPTY, Observable, OperatorFunction, forkJoin, of} from 'rxjs';
import {catchError, map, timeout} from 'rxjs/operators';
import {
  API_KEY,
  ContractNegotiationDto,
  ContractNegotiationService,
  NegotiationInitiateRequestDto,
  TransferProcessDto,
  TransferProcessService,
  TransferRequestDto,
} from '../../edc-dmgmt-client';
import {ContractOffer} from '../models/contract-offer';
import {ContractOfferResponseDto} from '../models/contract-offer-response-dto';
import {AssetPropertyMapper} from './asset-property-mapper';
import {CatalogApiUrlService} from './catalog-api-url.service';

/**
 * Combines several services that are used from the {@link CatalogBrowserComponent}
 */
@Injectable({
  providedIn: 'root',
})
export class CatalogBrowserService {
  constructor(
    private httpClient: HttpClient,
    private transferProcessService: TransferProcessService,
    private negotiationService: ContractNegotiationService,
    @Inject(API_KEY) private apiKey: string,
    private catalogApiUrlService: CatalogApiUrlService,
    private assetPropertyMapper: AssetPropertyMapper,
  ) {}

  getContractOffers(): Observable<ContractOffer[]> {
    const catalogApiUrlArray = this.catalogApiUrlService.getCatalogApiUrls();
    let allContractOffers: Observable<ContractOffer[]>[] = [];
    for (const catalogApiUrl of catalogApiUrlArray) {
      const contractOffers = this.httpClient
        .get<ContractOfferResponseDto>(catalogApiUrl)
        .pipe(map(({contractOffers}) => contractOffers))
        .pipe(timeout({first: 5_000, with: () => of([])}))
        .pipe(this.logErrors(catalogApiUrl, 'GET', of([])))
        .pipe(
          map((contractOffers) =>
            contractOffers.map((contractOffer) => ({
              ...contractOffer,
              asset: this.assetPropertyMapper.readProperties(
                contractOffer.asset.properties,
              ),
            })),
          ),
        );
      allContractOffers.push(contractOffers);
    }
    return forkJoin(allContractOffers).pipe(
      map((x) =>
        x.reduce(
          (previousValue, currentValue) => previousValue.concat(currentValue),
          [],
        ),
      ),
    );
  }

  initiateTransfer(transferRequest: TransferRequestDto): Observable<string> {
    return this.transferProcessService
      .initiateTransfer(transferRequest)
      .pipe(map((t) => t.id!));
  }

  getTransferProcessesById(id: string): Observable<TransferProcessDto> {
    return this.transferProcessService.getTransferProcess(id);
  }

  initiateNegotiation(
    initiateDto: NegotiationInitiateRequestDto,
  ): Observable<string> {
    return this.negotiationService
      .initiateContractNegotiation(initiateDto, 'body', false)
      .pipe(map((t) => t.id!));
  }

  getNegotiationState(id: string): Observable<ContractNegotiationDto> {
    return this.negotiationService.getNegotiation(id);
  }

  getAgreementForNegotiation(
    contractId: string,
  ): Observable<ContractNegotiationDto> {
    return this.negotiationService.getAgreementForNegotiation(contractId);
  }

  private post<T>(
    urlPath: string,
    params?:
      | HttpParams
      | {
          [param: string]:
            | string
            | number
            | boolean
            | ReadonlyArray<string | number | boolean>;
        },
  ): Observable<T> {
    const url = `${urlPath}`;
    let headers = new HttpHeaders({'X-Api-Key': this.apiKey});
    return this.httpClient
      .post<T>(url, {headers, params})
      .pipe(this.logErrors(url, 'POST', EMPTY));
  }

  /**
   * Catches errors, logs them to console.error
   * @param url additional logging: url
   * @param method additional logging: method
   * @param fallback optional default value to fall back to
   * @private
   */
  private logErrors<T, R>(
    url: string,
    method: string,
    fallback: Observable<R>,
  ): OperatorFunction<T, T | R> {
    return (observable) =>
      observable.pipe(
        catchError((httpErrorResponse: HttpErrorResponse) => {
          if (httpErrorResponse.error instanceof Error) {
            console.error(
              `Error accessing URL '${url}', Method: 'GET', Error: '${httpErrorResponse.error.message}'`,
            );
          } else {
            console.error(
              `Unsuccessful status code accessing URL '${url}', Method: '${method}', StatusCode: '${httpErrorResponse.status}', Error: '${httpErrorResponse.error?.message}'`,
            );
          }

          return fallback;
        }),
      );
  }
}
