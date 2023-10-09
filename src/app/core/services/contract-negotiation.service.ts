import {Injectable} from '@angular/core';
import {EMPTY, Observable, interval} from 'rxjs';
import {catchError, filter, first, switchMap, tap} from 'rxjs/operators';
import {
  ContractNegotiationRequest,
  UiContractNegotiation,
  UiContractOffer,
} from '@sovity.de/edc-client';
import {environment} from '../../../environments/environment';
import {EdcApiService} from './api/edc-api.service';
import {DataOffer} from './models/data-offer';
import {NotificationService} from './notification.service';

@Injectable({providedIn: 'root'})
export class ContractNegotiationService {
  runningContractOffers = new Set<string>();
  doneContractOffers = new Set<string>();

  constructor(
    private edcApiService: EdcApiService,
    private notificationService: NotificationService,
  ) {
    if (!environment.production) {
      // Test data on local dev
      this.runningContractOffers.add(
        'offer:6db25605-cd8c-4528-ade2-6a90349d06ac',
      );
      this.doneContractOffers.add('offer:6db25605-cd8c-4528-ade1-6a90389d06ac');
    }
  }

  negotiationState(
    contractOffer: UiContractOffer,
  ): 'ready' | 'negotiating' | 'negotiated' {
    const isNegotiated = this.isNegotiated(contractOffer);

    if (isNegotiated) {
      return 'negotiated';
    }

    const isBusy = this.isBusy(contractOffer);
    return isBusy ? 'negotiating' : 'ready';
  }

  isBusy(contractOffer: UiContractOffer) {
    return this.runningContractOffers.has(contractOffer.contractOfferId);
  }

  isNegotiated(contractOffer: UiContractOffer) {
    return this.doneContractOffers.has(contractOffer.contractOfferId);
  }

  negotiate(dataOffer: DataOffer, contractOffer: UiContractOffer) {
    const contractOfferId = contractOffer.contractOfferId;
    const initiateRequest: ContractNegotiationRequest = {
      counterPartyParticipantId: dataOffer.participantId,
      counterPartyAddress: dataOffer.endpoint!,
      assetId: dataOffer.asset.assetId,
      contractOfferId,
      policyJsonLd: contractOffer.policy.policyJsonLd,
    };

    this.initiateNegotiation(initiateRequest)
      .pipe(
        tap(() => this.onStarted(contractOfferId)),
        switchMap((negotiation) =>
          interval(1000).pipe(
            switchMap(() =>
              this.fetchNegotiation(negotiation.contractNegotiationId).pipe(
                catchError(() => EMPTY),
              ),
            ),
          ),
        ),
        filter(
          (negotiation) => negotiation.state.simplifiedState != 'IN_PROGRESS',
        ),
        first(),
      )
      .subscribe({
        next: (negotiation) => {
          if (negotiation.state.simplifiedState === 'AGREED') {
            this.onSuccess(contractOfferId);
          } else {
            this.onFailureNegotiating(contractOfferId);
          }
        },
        error: () => this.onFailureStarting(),
      });
  }

  private onFailureStarting() {
    this.notificationService.showError('Failure starting negotiation.');
  }

  private onStarted(contractOfferId: string) {
    this.runningContractOffers.add(contractOfferId);
  }

  private onFailureNegotiating(contractOfferId: string) {
    this.runningContractOffers.delete(contractOfferId);
    this.notificationService.showError('Failed negotiating contract.');
  }

  private onSuccess(contractOfferId: string) {
    this.runningContractOffers.delete(contractOfferId);
    this.doneContractOffers.add(contractOfferId);
    this.notificationService.showInfo('Contract Negotiation complete!');
  }

  private initiateNegotiation(
    initiateDto: ContractNegotiationRequest,
  ): Observable<UiContractNegotiation> {
    return this.edcApiService.initiateContractNegotiation(initiateDto);
  }

  private fetchNegotiation(id: string): Observable<UiContractNegotiation> {
    return this.edcApiService.getContractNegotiation(id);
  }
}
