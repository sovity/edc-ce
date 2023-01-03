import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams} from '@angular/common/http';
import {Inject, Injectable} from '@angular/core';
import {EMPTY, Observable} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {Asset} from '../models/asset';
import {ContractOffer} from '../models/contract-offer';
import {
    API_KEY,
    ContractNegotiationDto,
    ContractNegotiationService,
    NegotiationId,
    NegotiationInitiateRequestDto,
    TransferId,
    TransferProcessDto,
    TransferProcessService,
    TransferRequestDto
} from "../../edc-dmgmt-client";


/**
 * Combines several services that are used from the {@link CatalogBrowserComponent}
 */
@Injectable({
    providedIn: 'root'
})
export class AuthService {

    constructor(private httpClient: HttpClient,
                private transferProcessService: TransferProcessService,
                private negotiationService: ContractNegotiationService,
                @Inject(API_KEY) private apiKey: string,) {
    }

    getContractOffers(): Observable<ContractOffer[]> {
        return this.post<ContractOffer[]>(this.catalogApiUrl)
            .pipe(map(contractOffers => contractOffers.map(contractOffer => {
                contractOffer.asset = new Asset(contractOffer.asset.properties)
                return contractOffer;
            })));
    }

    initiateTransfer(transferRequest: TransferRequestDto): Observable<string> {
        return this.transferProcessService.initiateTransfer(transferRequest).pipe(map(t => t.id!))
    }

    getTransferProcessesById(id: string): Observable<TransferProcessDto> {
        return this.transferProcessService.getTransferProcess(id);
    }

    initiateNegotiation(initiateDto: NegotiationInitiateRequestDto): Observable<string> {
        return this.negotiationService.initiateContractNegotiation(initiateDto, 'body', false,).pipe(map(t => t.id!))
    }

    getNegotiationState(id: string): Observable<ContractNegotiationDto> {
        return this.negotiationService.getNegotiation(id);
    }

    getAgreementForNegotiation(contractId: string): Observable<ContractNegotiationDto> {
        return this.negotiationService.getAgreementForNegotiation(contractId);
    }

    private post<T>(urlPath: string,
                    params?: HttpParams | { [param: string]: string | number | boolean | ReadonlyArray<string | number | boolean>; })
        : Observable<T> {
        const url = `${urlPath}`;
        return this.catchError(this.httpClient.post<T>(url, {params}), url, 'POST');
    }

    private catchError<T>(observable: Observable<T>, url: string, method: string): Observable<T> {
        return observable
            .pipe(
                catchError((httpErrorResponse: HttpErrorResponse) => {
                    if (httpErrorResponse.error instanceof Error) {
                        console.error(`Error accessing URL '${url}', Method: 'GET', Error: '${httpErrorResponse.error.message}'`);
                    } else {
                        console.error(`Unsuccessful status code accessing URL '${url}', Method: '${method}', StatusCode: '${httpErrorResponse.status}', Error: '${httpErrorResponse.error?.message}'`);
                    }

                    return EMPTY;
                }));
    }

    logout() {
    }
}
